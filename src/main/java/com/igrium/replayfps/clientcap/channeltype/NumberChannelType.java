package com.igrium.replayfps.clientcap.channeltype;

import java.io.DataInputStream;
import java.io.IOException;

public interface NumberChannelType<T extends Number> extends ChannelType<T> {

    default int readInt(DataInputStream in) throws IOException {
        return read(in).intValue();
    }

    default long readLong(DataInputStream in) throws IOException {
        return read(in).longValue();
    }

    default float readFloat(DataInputStream in) throws IOException {
        return read(in).floatValue();
    }

    default double readDouble(DataInputStream in) throws IOException {
        return read(in).doubleValue();
    }

    default short readShort(DataInputStream in) throws IOException {
        return read(in).shortValue();
    }

    /**
     * Read a frame from this channel and cast to a byte. This does <b>NOT</b> read a single byte from the stream.
     * @param in Input stream to read from.
     * @return Byte value.
     * @throws IOException If an IO exception occurs.
     */
    default byte readByte(DataInputStream in) throws IOException {
        return read(in).byteValue();
    }
}
