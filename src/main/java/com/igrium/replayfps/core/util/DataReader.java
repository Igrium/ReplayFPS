package com.igrium.replayfps.core.util;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;
import java.util.Objects;

/**
 * A re-implementation of {@link DataInputStream} that doesn't require
 * per-stream instantiation.
 */
public class DataReader {

    public final void readFully(InputStream in, byte b[], int off, int len) throws EOFException, IOException {
        Objects.checkFromIndexSize(off, len, b.length);
        int n = 0;
        while (n < len) {
            int count = in.read(b, off + n, len - n);
            if (count < 0)
                throw new EOFException();
            n += count;
        }
    }

    /**
     * See the general contract of the {@code readBoolean}
     * method of {@code DataInput}.
     * <p>
     * Bytes for this operation are read from the contained
     * input stream.
     * @param in Stream to read from.
     * @return The value read.
     * @throws EOFException If the end of the stream has been reached.
     * @throws IOException If an IO exception occurs.
     */
    public final boolean readBoolean(InputStream in) throws EOFException, IOException {
        int ch = in.read();
        if (ch < 0)
            throw new EOFException();
        return (ch != 0);
    }
    
    /**
     * See the general contract of the {@code readUnsignedByte}
     * method of {@code DataInput}.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     * @param in Stream to read from.
     * @return The value read.
     * @throws EOFException If the end of the stream has been reached.
     * @throws IOException If an IO exception occurs.
     */
    public final int readUnsignedByte(InputStream in) throws EOFException, IOException {
        int ch = in.read();
        if (ch < 0)
            throw new EOFException();
        return ch;
    }
    
    public final short readShort(InputStream in) throws EOFException, IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (short)((ch1 << 8) + (ch2 << 0));
    }

    public final char readChar(InputStream in) throws EOFException, IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (char)((ch1 << 8) + (ch2 << 0));
    }

    public final int readInt(InputStream in) throws EOFException, IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        int ch3 = in.read();
        int ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    private byte readBuffer[] = new byte[8];

    public final long readLong(InputStream in) throws IOException {
        readFully(in, readBuffer, 0, 8);
        return (((long)readBuffer[0] << 56) +
                ((long)(readBuffer[1] & 255) << 48) +
                ((long)(readBuffer[2] & 255) << 40) +
                ((long)(readBuffer[3] & 255) << 32) +
                ((long)(readBuffer[4] & 255) << 24) +
                ((readBuffer[5] & 255) << 16) +
                ((readBuffer[6] & 255) <<  8) +
                ((readBuffer[7] & 255) <<  0));
    }

    public final float readFloat(InputStream in) throws EOFException, IOException {
        return Float.intBitsToFloat(readInt(in));
    }

    public final double readDouble(InputStream in) throws EOFException, IOException {
        return Double.longBitsToDouble(readLong(in));
    }

    public final String readUTF(InputStream in) throws EOFException, IOException, UTFDataFormatException {
        return new DataInputStream(in).readUTF();
    }
}
