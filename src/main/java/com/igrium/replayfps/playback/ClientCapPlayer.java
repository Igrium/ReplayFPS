package com.igrium.replayfps.playback;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jetbrains.annotations.Nullable;

import com.igrium.replayfps.channel.handler.ChannelHandler;
import com.igrium.replayfps.util.AnimationUtils;
import com.igrium.replayfps.util.ConcurrentBuffer;
import com.mojang.logging.LogUtils;

public class ClientCapPlayer implements Closeable {
    private final ClientCapReader reader;

    private int lastFrameRead = -1;
    @Nullable
    private UnserializedFrame lastFrame;

    /**
     * Create a ClientCap player.
     * @param reader Reader to use.
     * @throws IOException If an IO exception occurs reading the file header.
     */
    public ClientCapPlayer(ClientCapReader reader) throws IOException {
        this.reader = reader;
        if (reader.getHeader() == null) {
            reader.readHeader();
        }
    }
    
    /**
     * The amount of frames that can be buffered at a time.
     */
    private int bufferLength = 1024;

    public int getBufferLength() {
        return bufferLength;
    }

    public void setBufferLength(int bufferLength) {
        this.bufferLength = bufferLength;
    }

    public ClientCapReader getReader() {
        return reader;
    }

    private Optional<Exception> error = Optional.empty();

    public final Optional<Exception> getError() {
        return error;
    }

    public boolean hasErrored() {
        return error.isPresent();
    }

    /**
     * Read and apply the cliencap animation on the current frame.
     * @param context Playback context.
     */
    public void tickPlayer(ClientPlaybackContext context) {
        if (hasErrored())
            return;
        int frameNumber = -1;
        try {
            int timestamp = context.timestamp();
            frameNumber = AnimationUtils.countFrames(timestamp, reader.getHeader().getFramerate(),
                    reader.getHeader().getFramerateBase());

            if (frameNumber == lastFrameRead && lastFrame != null) {
                applyFrame(context, lastFrame);
                return;
            }

            if (frameNumber != reader.getPlayhead()) {
                reader.seek(frameNumber);
            }

            lastFrame = reader.readFrame();
            lastFrameRead = frameNumber;

            applyFrame(context, lastFrame);

        } catch (Exception e) {
            LogUtils.getLogger().error("An error occured while reading animation frame " + frameNumber, e);
            error = Optional.of(e);
            return;
        }

    }

    protected void applyFrame(ClientPlaybackContext context, UnserializedFrame frame) throws Exception {
        for (var entry : frame.getValues().entrySet()) {
            applyChannel(entry.getKey(), entry.getValue(), context);
        }
    }

    // Seperate function to handle generic
    private <T> void applyChannel(ChannelHandler<T> handler, Object value, ClientPlaybackContext context) throws Exception, ClassCastException {
        if (value == null)
            return;
        T casted = handler.getType().cast(value);
        handler.apply(casted, context);
    }

    @Override
    /**
     * Close this player and its reader.
     * @throws IOException If an IO exception occurs closing the reader.
     */
    public void close() throws IOException {
        reader.close();
    }

    public static class ClientCapBuffer extends ConcurrentBuffer<UnserializedFrame> implements Closeable {

        private ExecutorService executor = Executors.newSingleThreadExecutor();
        private final ClientCapReader reader;

        public ClientCapBuffer(ExecutorService executor, ClientCapReader reader) {
            super(executor);
            this.executor = executor;
            this.reader = reader;
        }

        @Override
        public ExecutorService getExecutor() {
            return executor;
        }

        @Override
        protected UnserializedFrame load(int index) throws Exception {
            if (index != reader.getPlayhead()) {
                reader.seek(index);
            }
            return reader.readFrame();
        }

        @Override
        public void close() {
            executor.shutdown();
        }

        public static ClientCapBuffer create(ClientCapReader reader) {
            return new ClientCapBuffer(Executors.newSingleThreadExecutor(r -> new Thread(r, "ClientCap Buffer")), reader);
        }
    }
}
