package com.igrium.replayfps.playback;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.igrium.replayfps.playback.ClientCapReader.ParsedChannelFrame;
import com.igrium.replayfps.util.AnimationUtils;
import com.mojang.logging.LogUtils;

public class ClientCapPlayer implements Closeable {
    private final ClientCapReader reader;

    private int lastFrameRead = -1;
    @Nullable
    private ParsedChannelFrame<?>[] lastFrame;

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

    protected void applyFrame(ClientPlaybackContext context, ParsedChannelFrame<?>[] frame) {
        for (var channel : frame) {
            channel.apply(context);
        }
    }

    @Override
    /**
     * Close this player and its reader.
     * @throws IOException If an IO exception occurs closing the reader.
     */
    public void close() throws IOException {
        reader.close();
    }
}
