package com.igrium.replayfps.clientcap;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.igrium.replayfps.clientcap.channels.AnimChannel;
import com.igrium.replayfps.clientcap.channels.AnimChannels;

/**
 * Represents the metadata for a single client capture file.
 */
public class ClientCapFile {
    private static final byte VERSION = 0;

    // TODO: Customizable channels
    private List<AnimChannel<?>> channels = new ArrayList<>(AnimChannels.getStandardChannels());
    private int chunkLength = 4000; // 4 seconds.

    public List<AnimChannel<?>> getChannels() {
        return Collections.unmodifiableList(channels);
    }

    /**
     * The number of milliseconds each chunk will account for.
     */
    public int getChunkLength() {
        return chunkLength;
    }

    /**
     * Set the number of milliseconds each chunk will account for. <b>Warning:
     * invalidates all previous chunks.</code>
     * 
     * @param chunkLength The new chunk length.
     */
    public void setChunkLength(int chunkLength) {
        this.chunkLength = chunkLength;
    }

    public int getFrameSize() {
        int size = 0;
        for (AnimChannel<?> channel : channels) {
            size += channel.getChannelType().getLength();
        }
        return size;
    }

    public void writeHeader(OutputStream os) throws IOException {
        DataOutputStream buffer = new DataOutputStream(new BufferedOutputStream(os));
        buffer.writeByte(VERSION);

        buffer.writeShort(chunkLength);
        

        ByteArrayOutputStream declaration = new ByteArrayOutputStream(16);
        DataOutputStream declarationData = new DataOutputStream(declaration);
        for (AnimChannel<?> channel : channels) {
            declarationData.writeUTF(AnimChannels.getName(channel));
        }

        byte[] declarationBytes = declaration.toByteArray();
        buffer.writeShort(declarationBytes.length);
        buffer.write(declarationBytes);
        buffer.flush();
    }

    /**
     * Captures a new frame that's compatible with this file. <b>Does <i>not</i> add
     * it to the file.</b>
     * 
     * @param captureContext The capture context.
     * @param chunkDelta     The number of milliseconds since the beginning of this
     *                       chunk.
     * @return The captured frame.
     */
    public Frame captureFrame(ClientCaptureContext captureContext, int chunkDelta) {
        Frame frame = new Frame(this);
        frame.delta = chunkDelta;

        for (int i = 0; i < channels.size(); i++) {
            frame.values[i] = channels.get(i).capture(captureContext);
        }
        return frame;
    }

    public static class Chunk {
        public final List<Frame> frames = new ArrayList<>();

        public void serialize(OutputStream os) throws IOException {
            DataOutputStream buffer = new DataOutputStream(new BufferedOutputStream(os));
            buffer.writeShort(frames.size());

            for (Frame frame : frames) {
                frame.serialize(buffer);
            }
            buffer.flush();
        }
    }

    public static class Frame {
        private int delta;
        
        private final ClientCapFile file;

        public Frame(ClientCapFile file) {
            this.file = file;
            values = new Object[file.channels.size()];
        }

        public final Object[] values;

        /**
         * The time since the beginning of the chunk in miliseconds.
         */
        public int getDelta() {
            return delta;
        }

        public void setDelta(int delta) {
            this.delta = delta;
        }

        public ClientCapFile getFile() {
            return file;
        }

        public void serialize(DataOutputStream out) throws IOException {
            out.writeInt(delta);
            for (int i = 0; i < values.length; i++) {
                serializeChannel(file.channels.get(i), i, null);
            }
        }

        private <T> void serializeChannel(AnimChannel<T> channel, int index, DataOutputStream out) throws IOException {
            T value = channel.cast(values[index]);
            channel.getChannelType().write(out, value);
        }
    }

    
}
