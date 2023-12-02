package com.igrium.replayfps.core.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UTFDataFormatException;

/**
 * A re-implementation of {@link DataOutputStream} that doesn't require
 * per-stream instantiation.
 */
public class DataWriter {
    private final byte[] writeBuffer = new byte[8];

    /**
     * Writes a {@code boolean} to the underlying output stream as
     * a 1-byte value. The value {@code true} is written out as the
     * value {@code (byte)1}; the value {@code false} is
     * written out as the value {@code (byte)0}.
     * 
     * @param out Stream to write to.
     * @param v   Value to write.
     * @throws IOException If an IO exception occurs.
     */
    public final void writeBoolean(OutputStream out, boolean v) throws IOException {
        out.write(v ? 1 : 0);
    }

    /**
     * Writes a {@code short} to the underlying output stream as two
     * bytes, high byte first.
     * 
     * @param out Stream to write to.
     * @param v   Value to write.
     * @throws IOException If an IO exception occurs.
     */
    public final void writeShort(OutputStream out, int v) throws IOException {
        writeBuffer[0] = (byte) (v >>> 8);
        writeBuffer[1] = (byte) (v >>> 0);
        out.write(writeBuffer, 0, 2);
    }

    /**
     * Writes a {@code char} to the underlying output stream as a
     * 2-byte value, high byte first.
     * 
     * @param out Stream to write to.
     * @param v   Value to write.
     * @throws IOException If an IO exception occurs.
     */
    public final void writeChar(OutputStream out, int v) throws IOException {
        writeBuffer[0] = (byte) (v >>> 8);
        writeBuffer[1] = (byte) (v >>> 0);
        out.write(writeBuffer, 0, 2);
    }

    /**
     * Writes an {@code int} to the underlying output stream as four
     * bytes, high byte first.
     * 
     * @param out Stream to write to.
     * @param v   Value to write.
     * @throws IOException If an IO exception occurs.
     */
    public final void writeInt(OutputStream out, int v) throws IOException {
        writeBuffer[0] = (byte) (v >>> 24);
        writeBuffer[1] = (byte) (v >>> 16);
        writeBuffer[2] = (byte) (v >>> 8);
        writeBuffer[3] = (byte) (v >>> 0);
        out.write(writeBuffer, 0, 4);
    }

    /**
     * Writes a {@code long} to the underlying output stream as eight
     * bytes, high byte first.
     * 
     * @param out Stream to write to.
     * @param v   Value to write.
     * @throws IOException If an IO exception occurs.
     */
    public final void writeLong(OutputStream out, long v) throws IOException {
        writeBuffer[0] = (byte) (v >>> 56);
        writeBuffer[1] = (byte) (v >>> 48);
        writeBuffer[2] = (byte) (v >>> 40);
        writeBuffer[3] = (byte) (v >>> 32);
        writeBuffer[4] = (byte) (v >>> 24);
        writeBuffer[5] = (byte) (v >>> 16);
        writeBuffer[6] = (byte) (v >>> 8);
        writeBuffer[7] = (byte) (v >>> 0);
        out.write(writeBuffer, 0, 8);
    }

    /**
     * Converts the float argument to an {@code int} using the
     * {@code floatToIntBits} method in class {@code Float},
     * and then writes that {@code int} value to the underlying
     * output stream as a 4-byte quantity, high byte first.
     * 
     * @param out Stream to write to.
     * @param v   Value to write.
     * @throws IOException If an IO exception occurs.
     */
    public final void writeFloat(OutputStream out, float v) throws IOException {
        writeInt(out, Float.floatToIntBits(v));
    }

    /**
     * Converts the double argument to a {@code long} using the
     * {@code doubleToLongBits} method in class {@code Double},
     * and then writes that {@code long} value to the underlying
     * output stream as an 8-byte quantity, high byte first.
     * 
     * @param out Stream to write to.
     * @param v   Value to write.
     * @throws IOException If an IO exception occurs.
     */
    public final void writeDouble(OutputStream out, double v) throws IOException {
        writeLong(out, Double.doubleToLongBits(v));
    }

    /**
     * Writes out the string to the underlying output stream as a
     * sequence of bytes. Each character in the string is written out, in
     * sequence, by discarding its high eight bits.
     * 
     * @param out Stream to write to.
     * @param s   A string of bytes to write.
     * @throws IOException If an IO exception occurs.
     */
    public final void writeBytes(OutputStream out, String s) throws IOException {
        int len = s.length();
        for (int i = 0; i < len; i++) {
            out.write((byte) s.charAt(i));
        }
    }

    /**
     * Writes a string to the underlying output stream as a sequence of
     * characters. Each character is written to the data output stream as
     * if by the {@code writeChar} method.
     * 
     * @param out Stream to write to.
     * @param s   Value to write.
     * @throws IOException If an IO exception occurs.
     */
    public final void writeChars(OutputStream out, String s) throws IOException {
        int len = s.length();
        for (int i = 0; i < len; i++) {
            int v = s.charAt(i);
            writeBuffer[0] = (byte) (v >>> 8);
            writeBuffer[1] = (byte) (v >>> 0);
            out.write(writeBuffer, 0, 2);
        }
    }

    /**
     * Writes a string to the specified DataOutput using
     * <a href="DataInput.html#modified-utf-8">modified UTF-8</a>
     * encoding in a machine-independent manner.
     * <p>
     * First, two bytes are written to out as if by the {@code writeShort}
     * method giving the number of bytes to follow. This value is the number of
     * bytes actually written out, not the length of the string. Following the
     * length, each character of the string is output, in sequence, using the
     * modified UTF-8 encoding for the character.
     * 
     * @param out Stream to write to.
     * @param str A string to be written.
     * @throws UTFDataFormatException if the modified UTF-8 encoding of
     *                                {@code str} would exceed 65535 bytes in length
     * @throws IOException            If an IO exception occurs.
     */
    public final void writeUTF(OutputStream out, String str) throws IOException {
        // This is a real cheap way of doing it, but it's used infrequently enough not
        // to worry about memory allocatinon overhead.
        new DataOutputStream(out).writeUTF(str);
    }
}
