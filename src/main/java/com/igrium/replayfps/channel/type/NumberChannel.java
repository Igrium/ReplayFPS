package com.igrium.replayfps.channel.type;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.igrium.replayfps.util.DataReader;
import com.igrium.replayfps.util.DataWriter;

public abstract class NumberChannel<T extends Number> extends ChannelType<T> {

    public int readInt(InputStream in) throws IOException {
        return read(in).intValue();
    }

    public long readLong(InputStream in) throws IOException {
        return read(in).longValue();
    }

    public short readShort(InputStream in) throws IOException {
        return read(in).shortValue();
    }

    public byte readByte(InputStream in) throws IOException {
        return read(in).byteValue();
    }

    public float readFloat(InputStream in) throws IOException {
        return read(in).floatValue();
    }

    public double readDouble(InputStream in) throws IOException {
        return read(in).doubleValue();
    }

    public static class ShortChannel extends NumberChannel<Short> {

        private final DataWriter writer = new DataWriter();
        private final DataReader reader = new DataReader();

        @Override
        public Class<Short> getType() {
            return Short.class;
        }

        @Override
        public int getSize() {
            return Short.BYTES;
        }

        @Override
        public Short read(InputStream in) throws IOException {
            return reader.readShort(in);
        }

        @Override
        public void write(OutputStream out, Short val) throws IOException {
            writer.writeShort(out, val);
        }

    }

    public static class IntegerChannel extends NumberChannel<Integer> {

        private final DataWriter writer = new DataWriter();
        private final DataReader reader = new DataReader();

        @Override
        public Class<Integer> getType() {
            return Integer.class;
        }

        @Override
        public int getSize() {
            return Integer.BYTES;
        }

        @Override
        public Integer read(InputStream in) throws IOException {
            return reader.readInt(in);
        }

        @Override
        public void write(OutputStream out, Integer val) throws IOException {
            writer.writeInt(out, val);
        }

    }

    public static class LongChannel extends NumberChannel<Long> {

        private final DataWriter writer = new DataWriter();
        private final DataReader reader = new DataReader();

        @Override
        public Class<Long> getType() {
            return Long.class;
        }

        @Override
        public int getSize() {
            return Long.BYTES;
        }

        @Override
        public Long read(InputStream in) throws IOException {
            return reader.readLong(in);
        }

        @Override
        public void write(OutputStream out, Long val) throws IOException {
            writer.writeLong(out, val);
        }

    }

    public static class FloatChannel extends NumberChannel<Float> {

        private final DataWriter writer = new DataWriter();
        private final DataReader reader = new DataReader();

        @Override
        public Class<Float> getType() {
            return Float.class;
        }

        @Override
        public int getSize() {
            return Float.BYTES;
        }

        @Override
        public Float read(InputStream in) throws IOException {
            return reader.readFloat(in);
        }

        @Override
        public void write(OutputStream out, Float val) throws IOException {
            writer.writeFloat(out, val);
        }

    }

    public static class DoubleChannel extends NumberChannel<Double> {
        private final DataWriter writer = new DataWriter();
        private final DataReader reader = new DataReader();

        @Override
        public Class<Double> getType() {
            return Double.class;
        }

        @Override
        public int getSize() {
            return Double.BYTES;
        }

        @Override
        public Double read(InputStream in) throws IOException {
            return reader.readDouble(in);
        }

        @Override
        public void write(OutputStream out, Double val) throws IOException {
            writer.writeDouble(out, val);
        }
    }

}
