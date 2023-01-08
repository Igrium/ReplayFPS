package com.igrium.replayfps.clientcap;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;

import com.google.common.io.CountingInputStream;
import com.igrium.replayfps.clientcap.animchannels.AnimChannelType;
import com.igrium.replayfps.clientcap.animchannels.AnimChannelTypes;

/**
 * Represents the metadata for a single client capture file.
 */
public class ClientCapFile {
    private static final byte VERSION = 0;

    // TODO: Customizable channels
    private List<AnimChannelType<?>> channels = new ArrayList<>(AnimChannelTypes.getStandardChannels());
    private int chunkLength = 4000; // 4 seconds.

    public List<AnimChannelType<?>> getChannels() {
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
        if (chunkLength <= 0) {
            throw new IllegalArgumentException("Chunk length must be greater than zero.");
        }
        this.chunkLength = chunkLength;
    }

    public int getFrameSize() {
        int size = 0;
        for (AnimChannelType<?> channel : channels) {
            size += channel.getLength();
        }
        return size;
    }

    public void writeHeader(OutputStream os) throws IOException {
        DataOutputStream buffer = new DataOutputStream(new BufferedOutputStream(os));
        buffer.writeByte(VERSION);

        buffer.writeShort(chunkLength);

        ByteArrayOutputStream declaration = new ByteArrayOutputStream(16);
        DataOutputStream declarationData = new DataOutputStream(declaration);
        for (AnimChannelType<?> channel : channels) {
            declarationData.writeUTF(AnimChannelTypes.getName(channel));
        }

        byte[] declarationBytes = declaration.toByteArray();
        buffer.writeShort(declarationBytes.length);
        buffer.write(declarationBytes);
        buffer.flush();
    }

    /**
     * Create a ClientCap object from a file header.
     * @param is The input stream with the header.
     * @return The parsed object. Will not contain any chunks yet.
     * @throws IOException If an IO exception occurs.
     */
    public static ClientCapFile readHeader(InputStream is) throws IOException {
        ClientCapFile file = new ClientCapFile();
        file.parseHeader(is);
        return file;
    }

    protected void parseHeader(InputStream is) throws IOException {
        CountingInputStream counter = new CountingInputStream(is);
        DataInputStream data = new DataInputStream(counter);

        int version = data.readUnsignedByte();
        if (version != VERSION) {
            LogManager.getLogger().warn("Attempting to load unsupported ClientCap version: "+version);
        }

        setChunkLength(data.readUnsignedShort());

        int declarationLength = data.readUnsignedShort();
        long declarationStart = counter.getCount();

        channels.clear();
        while (counter.getCount() - declarationStart < declarationLength) {
            String name = data.readUTF();
            AnimChannelType<?> channel = AnimChannelTypes.REGISTRY.get(name);
            if (channel == null) {
                throw new IOException("Unknown channel type: "+name);
            }
            channels.add(channel);
        }

        if (counter.getCount() - declarationStart != declarationLength) {
            throw new IOException(
                    String.format("The channel channel declaration did not match its declared size. (%d != %d)",
                            counter.getCount() - declarationStart, declarationLength));
            
        }
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

    /**
     * Get the chunk at a particular timestamp.
     * 
     * @param timestamp The timestamp in milliseconds.
     * @return The index of the chunk. It is not guarenteed that this chunk will
     *         actually exist.
     */
    public int chunkAt(int timestamp) {
        return Math.floorDiv(timestamp, chunkLength);
    }
    
    /**
     * Calculate the size of a theoretical chunk in this file.
     * @param numFrames The number of frames in the chunk.
     * @return The number of bytes in the chunk.
     */
    public int calcChunkSize(int numFrames) {
        int frameSize = Short.BYTES;
        for (AnimChannelType<?> channel : channels) {
            frameSize += channel.getLength();
        }
        return frameSize * numFrames;
    }

    /**
     * Read a chunk from the file.
     * @param in The input stream of the file.
     * @return The parsed chunk.
     * @throws IOException If an IO exception occurs.
     */
    public Chunk readChunk(InputStream in) throws IOException {
        DataInputStream data = new DataInputStream(in);

        Chunk chunk = new Chunk();
        int length = data.readUnsignedShort();
        for (int i = 0; i < length; i++) {
            chunk.frames.add(parseFrame(data));
        }

        return chunk;
    }

    public static class Chunk {
        public final List<Frame> frames = new ArrayList<>();

        public void serialize(DataOutputStream out) throws IOException {
            out.writeShort(frames.size());

            for (Frame frame : frames) {
                frame.serialize(out);
            }
        }

        /**
         * Get the frame at a particular time in the chunk. If <code>time</code> is
         * greater than the length of the chunk, return the last frame.
         * 
         * @param time The time in milliseconds since the start of the chunk.
         * @return The index of the frame at that time. <code>-1</code> if the chunk has
         *         no frames or the provided time is before the first frame.
         */
        public int frameAt(int time) {
            if (time < 0) {
                throw new IllegalArgumentException("Time cannot be negative!");
            }

            int index = -1;

            // This only works because the frames must be in order.
            for (int i = 0; i < frames.size(); i++) {
                if (frames.get(i).delta <= time) {
                    index = i;
                }
            }
            return index;
        }
    }

    protected Frame parseFrame(DataInputStream in) throws IOException  {
        Frame frame = new Frame(this);
        frame.delta = in.readUnsignedShort();
        for (int i = 0; i < channels.size(); i++) {
            AnimChannelType<?> channel = channels.get(i);
            frame.values[i] = channel.read(in);
        }
        return frame;
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
            out.writeShort(delta);
            for (int i = 0; i < values.length; i++) {
                serializeChannel(file.channels.get(i), i, out);
            }
        }

        private <T> void serializeChannel(AnimChannelType<T> channel, int index, DataOutputStream out) throws IOException {
            T value = channel.cast(values[index]);
            channel.write(out, value);
        }

        /**
         * Apply the data in this frame to the game.
         * @param context The playback context.
         */
        public void apply(ClientPlaybackContext context) {
            for (int i = 0; i < values.length; i++) {
                applyChannel(file.channels.get(i), i, context);
            }
        }

        private <T> void applyChannel(AnimChannelType<T> channel, int index, ClientPlaybackContext context) {
            T value = channel.cast(values[index]);
            channel.apply(value, context);
        }
    }

    
}
