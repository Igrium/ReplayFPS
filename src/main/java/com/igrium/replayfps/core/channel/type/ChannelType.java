package com.igrium.replayfps.core.channel.type;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A data channel that can exist in the replay. Each channel is serialized for
 * each frame, and must be the same size in each frame. The size of each channel
 * will deternmine the size of the frame.
 */
public interface ChannelType<T> {

    public Class<T> getType();

    /**
     * The size of the serialized data in this channel.
     * 
     * @return Size of the frame in bytes.
     */
    public int getSize();

    /**
     * Read a frame of this channel from the specified input stream.
     * 
     * @param in Input stream to read from.
     * @return Parsed value.
     * @throws IOException If an IO exception occurs.
     */
    public T read(DataInput in) throws IOException;

    /**
     * Write a frame of this channel to the specified output stream.
     * 
     * @param out Output stream to write to.
     * @param val Value to write.
     * @throws IOException If an IO exception occurs.
     */
    public void write(DataOutput out, T val) throws IOException;

    /**
     * Get a "default" value for this channel. Used primarily for testing.
     * @return Channel's default value.
     */
    public T defaultValue();

    /**
     * Interpolate linearly between two values of this type.
     * 
     * @param from  Value A.
     * @param to    Value B.
     * @param delta A float from 0 - 1 determinating the progress of the
     *              interpolation. <code>0</code> will return <code>from</code>, and
     *              <code>1</code> will return <code>to</code>. Behavior for values
     *              outside this range is undefined.
     * @return The interpolated value.
     */
    public default T interpolate(T from, T to, float delta) {
        return from;
    }

    /**
     * Get a simple name that identifies this channel type in the debugger.
     * @return Channel name.
     */
    public default String getName() {
        return getType().getSimpleName();
    }

    /**
     * Get raw scalar values that to represent this channel in the debugger graph.
     * 
     * @param value Value to extract.
     * @return An array of all relevent values as floats. Must return the same
     *         number of values each call.
     */
    public default float[] getRawValues(T value) {
        return new float[0];
    }
}
