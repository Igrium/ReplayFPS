package com.igrium.replayfps.recording;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import com.igrium.replayfps.channel.handler.ChannelHandler;
import com.igrium.replayfps.playback.UnserializedFrame;
import com.igrium.replayfps.util.AnimationUtils;
import com.igrium.replayfps.util.NoHeaderException;
import com.mojang.logging.LogUtils;

import net.minecraft.util.Util;

/**
 * Captures and saves frames to a file.
 */
public class ClientCapRecorder implements Closeable {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final BufferedOutputStream out;
    private final ClientCapWriter writer;

    @Nullable
    private ClientCapHeader header;

    private int saveInterval = 512;

    public int getSaveInterval() {
        return saveInterval;
    }

    public void setSaveInterval(int saveInterval) {
        this.saveInterval = saveInterval;
    }

    public ClientCapRecorder(OutputStream out) {
        this.out = new BufferedOutputStream(out);
        this.writer = new ClientCapWriter(out);
        startTime = System.currentTimeMillis();
    }

    @Nullable
    public ClientCapHeader getHeader() {
        return header;
    }

    public ClientCapWriter getWriter() {
        return writer;
    }

    /**
     * Write the file header.
     * @param header Header to write.
     * @throws IllegalStateException If the header has already been written.
     */
    public void writeHeader(ClientCapHeader header) throws IllegalStateException {
        if (this.header != null) {
            throw new IllegalStateException("Header has already been written.");
        }

        this.header = header;
        try {
            header.writeHeader(out);
            out.flush();
        } catch (IOException e) {
            LOGGER.error("Error writing clientcap header. Recording will be aborted.", e);
            this.error = Optional.of(e);
            return;
        }
        
    }

    /* FRAME CAPTURE */

    /**
     * Capture a frame.
     * @param context Capture context.
     * @return The frame.
     * @throws Exception If the frame capture fails.
     */
    public UnserializedFrame captureFrame(ClientCaptureContext context) throws Exception {
        assertHeaderWritten();
        Object[] values = new Object[header.numChannels()];

        int i = 0;
        for (ChannelHandler<?> handler : header.getChannels()) {
            values[i] = handler.capture(context);
            i++;
        }

        return new UnserializedFrame(header, values);
    }

    private int framesSinceLastSave;
    
    protected UnserializedFrame writeFrame(ClientCaptureContext context) throws Exception {
        assertHeaderWritten();

        UnserializedFrame frame = captureFrame(context);
        writer.writeFrame(frame);

        framesSinceLastSave++;
        if (framesSinceLastSave > saveInterval) {
            out.flush();
            framesSinceLastSave = 0;
        }

        return frame;
    }
    
    /* RECORDING */

    private boolean isRecording;
    public final boolean isRecording() {
        return isRecording;
    }

    private long startTime;
    public final long getStartTime() {
        return startTime;
    }

    public void startRecording() throws IllegalStateException {
        if (isRecording) throw new IllegalStateException("We are already recording.");
        isRecording = true;
        startTime = Util.getMeasuringTimeMs();
    }

    private volatile boolean serverWasPaused;
    public void setServerWasPaused() {
        this.serverWasPaused = true;
    }
    
    private long lastTimestamp;
    private long timePassedWhilePaused;

    private Optional<Exception> error = Optional.empty();
    public Optional<Exception> getError() {
        return error;
    }

    public boolean hasErrored() {
        return error.isPresent();
    }

    /**
     * Called every frame wile capturing.
     * @param context The render context.
     */
    public void tick(ClientCaptureContext context) {
        if (header == null || !isRecording) return;
        if (hasErrored()) return;

        long now = Util.getMeasuringTimeMs();
        // Real time since recording started.
        long timeRecording = now - startTime;

        // This math makes sense to me now, but don't ask me to explain it later.
        // UPDATE: It's later and I don't understand it.
        if (serverWasPaused) {
            timePassedWhilePaused = timeRecording - lastTimestamp;
            serverWasPaused = false;
        }
        long timestamp = timeRecording - timePassedWhilePaused;
        lastTimestamp = timestamp;

        int currentFrame = AnimationUtils.countFrames((int) timestamp, header.getFramerate(), header.getFramerateBase());
        // It doesn't matter if this is negative because we're only using it for a for loop.
        int framesToCapture = currentFrame - writer.getWrittenFrames();
        if (framesToCapture < 0) {
            LOGGER.warn(String.format("More frames have been captured than the current timestamp suggests. (%d > %d)",
                    writer.getWrittenFrames(), currentFrame));
        }

        for (int i = 0; i < framesToCapture; i++) {
            try {
                writeFrame(context);
            } catch (Exception e) {
                LOGGER.error(String.format(
                        "Error capturing frame %d. Capture will be aborted.", writer.getWrittenFrames()), e);
                this.error = Optional.of(e);
                return;
            }
        }
    }

    private void assertHeaderWritten() throws NoHeaderException {
        if (header == null)
            throw new NoHeaderException("Header has not been written.");
    }

    @Override
    public void close() throws IOException {
        out.flush();
        out.close();
    }

}
