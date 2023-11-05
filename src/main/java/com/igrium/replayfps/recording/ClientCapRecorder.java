package com.igrium.replayfps.recording;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jetbrains.annotations.Nullable;

import com.igrium.replayfps.channel.handler.ChannelHandler;
import com.mojang.logging.LogUtils;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.util.Util;

/**
 * Captures and saves frames to a file.
 */
public class ClientCapRecorder implements Closeable {

    private final BufferedOutputStream out;

    private int saveInterval = 256;

    @Nullable
    private ClientCapHeader header;
    
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

    public void writeHeader(ClientCapHeader header) throws IOException {
        if (this.header != null) {
            throw new IllegalStateException("The header has already been written!");
        }

        this.header = header;
        header.writeHeader(out);
        out.flush();
    }

    private int framesSinceLastSave;


    public void captureFrame() throws Exception {
        assertHeaderWritten();
        
        DataOutputStream dataOut = new DataOutputStream(out);
        for (ChannelHandler<?> handler : header.getChannels()) {
            ChannelHandler.writeChannel(dataOut, handler);
        }

        if (framesSinceLastSave >= saveInterval) {
            out.flush();
            framesSinceLastSave = 0;
        } else {
            framesSinceLastSave++;
        }
    }

    // TODO: Check if this method of counting frames causes drift

    private long lastFrameCapture;

    /**
     * Called every frame while capturing.
     * @param context The render context.
     */
    public void tick(WorldRenderContext context) {
        if (header == null) return;

        long now = Util.getMeasuringTimeMs();
        int frameDelta = (int) (now - lastFrameCapture);

        // Should never happen.
        if (frameDelta <= 0) return;

        // How many times should have been captured between now and 
        int numFrames = Math.floorDiv(frameDelta, getFrameIntervalMillis());

        for (int i = 0; i < numFrames; i++) {
            try {
                captureFrame();
            } catch (Exception e) {
                LogUtils.getLogger().error("Error capturing frame.", e);
            }
        }
        lastFrameCapture = now;
    }


    @Override
    public void close() throws IOException {
        out.close();
    }
    
    private void assertHeaderWritten() {
        if (header == null) {
            throw new IllegalStateException("Header has not been initialized!");
        }
    }
}
