package com.igrium.replayfps.recording;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jetbrains.annotations.Nullable;

import com.igrium.replayfps.channel.handler.ChannelHandler;
import com.igrium.replayfps.util.AnimationUtils;
import com.mojang.logging.LogUtils;

import net.minecraft.util.Util;

/**
 * Captures and saves frames to a file.
 */
public class ClientCapRecorder implements Closeable {

    private final BufferedOutputStream out;

    private int saveInterval = 256;

    @Nullable
    private ClientCapHeader header;

    public ClientCapHeader getHeader() {
        return header;
    }
    
    public void setSaveInterval(int saveInterval) {
        this.saveInterval = saveInterval;
    }

    public int getSaveInterval() {
        return saveInterval;
    }

    public ClientCapRecorder(OutputStream out) {
        this.out = new BufferedOutputStream(out);
    }

    public float getFrameRate() {
        assertHeaderWritten();
        return header.getFramerateFloat();
    }

    public float getFrameInterval() {
        assertHeaderWritten();
        return header.getFrameInterval();
    }

    public int getFrameIntervalMillis() {
        assertHeaderWritten();
        return header.getFrameIntervalMillis();
    }

    private long startTime;
    private boolean isRecording;

    public final boolean isRecording() {
        return isRecording;
    }

    public final long getStartTime() {
        return startTime;
    }

    public void writeHeader(ClientCapHeader header) throws IOException {
        if (this.header != null) {
            throw new IllegalStateException("The header has already been written!");
        }

        this.header = header;
        header.writeHeader(out);
        out.flush();
    }
    
    public void beginCapture() {
        assertHeaderWritten();
        startTime = Util.getMeasuringTimeMs();
        isRecording = true;
    }

    private int framesSinceLastSave;

    private int framesCaptured;

    public int getFramesCaptured() {
        return framesCaptured;
    }
    
    public void captureFrame(ClientCaptureContext context) throws Exception {
        try {
            assertHeaderWritten();
            assertRecording();
            
            DataOutputStream dataOut = new DataOutputStream(out);
            for (ChannelHandler<?> handler : header.getChannels()) {
                ChannelHandler.writeChannel(context, dataOut, handler);
            }

            if (framesSinceLastSave >= saveInterval) {
                out.flush();
                framesSinceLastSave = 0;
            } else {
                framesSinceLastSave++;
            }
        } finally {
            framesCaptured++;
        }
    }

    private long lastTimestamp;
    private boolean serverWasPaused;
    private long timePassedWhilePaused;

    public boolean serverWasPaused() {
        return serverWasPaused;
    }

    public void setServerWasPaused() {
        this.serverWasPaused = true;
    }
    
    /**
     * Called every frame while capturing.
     * @param context The render context.
     */
    public void tick(ClientCaptureContext context) {
        if (header == null || !isRecording) return;

        long now = Util.getMeasuringTimeMs();

        // This math makes sense to me now, but don't ask me to explain it later.
        // UPDATE: It's later and I don't understand it.
        if (serverWasPaused) {
            timePassedWhilePaused = now - startTime - lastTimestamp;
            serverWasPaused = false;
        }
        long timestamp = now - startTime - timePassedWhilePaused;
        lastTimestamp = timestamp;
        
        int currentFrame = AnimationUtils.countFrames(timestamp, header.getFramerate(), header.getFramerateBase());
        // It doesn't matter if this is negative because we're only using it for a for loop.
        int framesToCapture = currentFrame - framesCaptured;

        for (int i = 0; i < framesToCapture; i++) {
            try {
                captureFrame(context);
            } catch (Exception e) {
                stopRecording();
                LogUtils.getLogger().error("Error capturing frame " + framesCaptured, e);
                break;
            }
        }
    }

    public void stopRecording() {
        isRecording = false;
    }

    @Override
    public void close() throws IOException {
        stopRecording();
        out.close();
    }
    
    private void assertHeaderWritten() {
        if (header == null) {
            throw new IllegalStateException("Header has not been initialized!");
        }
    }

    private void assertRecording() {
        if (!isRecording) {
            throw new IllegalStateException("Not currently recording!");
        }
    }
}
