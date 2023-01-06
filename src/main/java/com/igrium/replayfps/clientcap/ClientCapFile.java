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
 * Represents a single "client capture" file.
 */
public class ClientCapFile {
    private static final byte VERSION = 0;

    private List<AnimChannel<?>> channels = new ArrayList<>();
    private int chunkLengthTicks = 1000;

    public List<AnimChannel<?>> getChannels() {
        return Collections.unmodifiableList(channels);
    }

    public int getChunkLengthTicks() {
        return chunkLengthTicks;
    }

    public void setChunkLengthTicks(int chunkLength) {
        this.chunkLengthTicks = chunkLength;
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

        buffer.writeShort(chunkLengthTicks);
        

        ByteArrayOutputStream declaration = new ByteArrayOutputStream(16);
        DataOutputStream declarationData = new DataOutputStream(declaration);
        for (AnimChannel<?> channel : channels) {
            declarationData.writeUTF(AnimChannels.getName(channel));
        }

        byte[] declarationBytes = declaration.toByteArray();
        buffer.writeShort(declarationBytes.length);
        buffer.write(declarationBytes);
    }

    /**
     * Captures a new frame that's compatible with this file. Does <i>not</code> add
     * it to the file.
     * 
     * @param captureContext The capture context.
     * @param chunkDelta     The number of milliseconds since the beginning of this
     *                       chunk.
     * @return The captured frame.
     */
    public Frame captureFrame(ClientCaptureContext captureContext, int chunkDelta) {
        Frame frame = new Frame();
        frame.delta = chunkDelta;

        for (int i = 0; i < channels.size(); i++) {
            frame.values[i] = channels.get(i).capture(captureContext);
        }
        return frame;
    }

    public class Chunk {
        public final List<Frame> frames = new ArrayList<>();
        private long time;

        /**
         * The world time (ticks) at which this chunk starts.
         */
        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public void serialize(OutputStream os) throws IOException {
            DataOutputStream buffer = new DataOutputStream(new BufferedOutputStream(os));
            buffer.writeInt((int) time);
            buffer.writeShort(frames.size());

            for (Frame frame : frames) {
                frame.serialize(buffer);
            }
            buffer.flush();
        }
    }

    public class Frame {
        private int delta;

        /**
         * The time since the beginning of the chunk in miliseconds.
         */
        public int getDelta() {
            return delta;
        }

        public void setDelta(int delta) {
            this.delta = delta;
        }

        public final Object[] values = new Object[channels.size()];

        public void serialize(DataOutputStream out) throws IOException {
            out.writeInt(delta);
            for (int i = 0; i < values.length; i++) {
                serializeChannel(channels.get(i), i, null);
            }
        }

        private <T> void serializeChannel(AnimChannel<T> channel, int index, DataOutputStream out) throws IOException {
            T value = channel.cast(values[index]);
            channel.getChannelType().write(out, value);
        }
    }

    
}
