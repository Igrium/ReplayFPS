package com.igrium.replayfps.clientcap.channeltype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class NumberChannelType<T extends Number> implements ChannelType<T> {

    public int readInt(DataInputStream in) throws IOException {
        return read(in).intValue();
    }

    public long readLong(DataInputStream in) throws IOException {
        return read(in).longValue();
    }

    public float readFloat(DataInputStream in) throws IOException {
        return read(in).floatValue();
    }

    public double readDouble(DataInputStream in) throws IOException {
        return read(in).doubleValue();
    }

    public short readShort(DataInputStream in) throws IOException {
        return read(in).shortValue();
    }

    /**
     * Read a frame from this channel and cast to a byte. This does <b>NOT</b> read a single byte from the stream.
     * @param in Input stream to read from.
     * @return Byte value.
     * @throws IOException If an IO exception occurs.
     */
    public byte readByte(DataInputStream in) throws IOException {
        return read(in).byteValue();
    }

    /**
     * Cast the given number to the correct type.
     * @param number The value to cast.
     * @return The cast value.
     */
    protected abstract T valueOf(Number value);

    /**
     * Write a number value to this channel.
     * @param out Output stream to write to.
     * @param value Value to serialize.
     * @throws IOException If an IO exception occurs.
     */
    public void writeValue(DataOutputStream out, Number value) throws IOException {
        this.write(out, valueOf(value));
    }
    
    @Override
    public T lerp(T from, T to, float fac) {
        double a = from.doubleValue();
        double b = to.doubleValue();
        return valueOf(a * (1.0 - fac) + (b * fac));
    }
}
