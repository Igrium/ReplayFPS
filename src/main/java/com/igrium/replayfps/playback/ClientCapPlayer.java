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
import com.igrium.replayfps.util.GlobalReplayContext;
import com.mojang.logging.LogUtils;

import net.minecraft.client.MinecraftClient;

public class ClientCapPlayer implements Closeable {
    private final ClientCapReader reader;

    private int lastFrameAIndex = -1;
    private int lastFrameBIndex = -1;

    private UnserializedFrame lastFrameA;
    private UnserializedFrame lastFrameB;

    @Nullable
    private UnserializedFrame lastFrame;

    private ClientCapBuffer buffer;

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
        this.buffer = ClientCapBuffer.create(reader);
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
            // TODO: figure out why replay appears half a second behind instead of this cheeky workaround.
            int timestamp = context.timestamp(); 
            int framerate = reader.getHeader().getFramerate();
            int framerateBase = reader.getHeader().getFramerateBase();

            frameNumber = AnimationUtils.countFrames(timestamp, framerate, framerateBase);

            if (frameNumber < 0) return;
            
            long prevFrameTime = AnimationUtils.getDuration(frameNumber, framerate, framerateBase);
            long nextFrameTime = AnimationUtils.getDuration(frameNumber + 1, framerate, framerateBase);
            
            float delta;
            if (nextFrameTime == prevFrameTime) {
                delta = 0;
            } else {
                delta = (timestamp - prevFrameTime) / (float) (nextFrameTime - prevFrameTime);
            }

            UnserializedFrame prevFrame = getFrame(frameNumber, true);
            UnserializedFrame nextFrame = getFrame(frameNumber + 1, true);

            // Storing these prevents unneeded polls of the buffer.
            lastFrameAIndex = frameNumber;
            lastFrameA = prevFrame;

            lastFrameBIndex = frameNumber + 1;
            lastFrameB = nextFrame;

            if (context.localPlayer().isPresent()
                    && context.localPlayer().get().equals(MinecraftClient.getInstance().cameraEntity)) {
                for (var entry : prevFrame.getValues().entrySet()) {
                    Object other = nextFrame != null ? nextFrame.getValue(entry.getKey()) : null;
                    interpolateAndApply(entry.getKey(), entry.getValue(), other, delta, context);
                }
            } else {
                GlobalReplayContext.ENTITY_POS_OVERRIDES.clear();
            }

        } catch (Exception e) {
            LogUtils.getLogger().error("An error occured while reading animation frame " + frameNumber, e);
            error = Optional.of(e);
            return;
        }

    }

    private UnserializedFrame getFrame(int index, boolean poll) {
        if (index == lastFrameAIndex && lastFrameA != null)
            return lastFrameA;
        if (index == lastFrameBIndex && lastFrameB != null)
            return lastFrameB;
        
        if (index != buffer.getIndex()) {
            buffer.seek(index);
            LogUtils.getLogger().info("Seeking frame " + index);
        }

        return poll ? buffer.poll() : buffer.peek();
    }


    private <T> void interpolateAndApply(ChannelHandler<T> handler, Object value, @Nullable Object value2, float delta, ClientPlaybackContext context) throws Exception, ClassCastException {
        if (value == null)
            return;
        T casted = handler.getType().cast(value);

        if (delta < 0) delta = 0;
        if (delta > 1) delta = 1;
        
        if (handler.shouldInterpolate() && value2 != null) {
            T casted2 = handler.getType().cast(value2);
            handler.apply(handler.getChannelType().interpolate(casted, casted2, delta), context);

        } else {
            handler.apply(casted, context);
        }
    }

    @Override
    /**
     * Close this player and its reader.
     * @throws IOException If an IO exception occurs closing the reader.
     */
    public void close() throws IOException {
        reader.close();
        buffer.close();
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
