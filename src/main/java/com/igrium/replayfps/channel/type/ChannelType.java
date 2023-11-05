package com.igrium.replayfps.channel.type;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A data channel that can exist in the replay. Each channel is serialized for
 * each frame, and must be the same size in each frame. The size of each channel
 * will deternmine the size of the frame.
 */
public abstract class ChannelType<T> {

    public abstract Class<T> getType();

    /**
     * The size of the serialized data in this channel.
     * 
     * @return Size of the frame in bytes.
     */
    public abstract int getSize();

    /**
     * Read a frame of this channel from the specified input stream.
     * 
     * @param in Input stream to read from.
     * @return Parsed value.
     * @throws IOException If an IO exception occurs.
     */
    public abstract T read(DataInput in) throws IOException;

    /**
     * Write a frame of this channel to the specified output stream.
     * 
     * @param out Output stream to write to.
     * @param val Value to write.
     * @throws IOException If an IO exception occurs.
     */
    public abstract void write(DataOutput out, T val) throws IOException;

}
