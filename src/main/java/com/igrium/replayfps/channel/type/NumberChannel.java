package com.igrium.replayfps.channel.type;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public abstract class NumberChannel<T extends Number> extends ChannelType<T> {

    public int readInt(DataInput in) throws IOException {
        return read(in).intValue();
    }

    public long readLong(DataInput in) throws IOException {
        return read(in).longValue();
    }

    public short readShort(DataInput in) throws IOException {
        return read(in).shortValue();
    }

    public byte readByte(DataInput in) throws IOException {
        return read(in).byteValue();
    }

    public float readFloat(DataInput in) throws IOException {
        return read(in).floatValue();
    }

    public double readDouble(DataInput in) throws IOException {
        return read(in).doubleValue();
    }

    public static class ShortChannel extends NumberChannel<Short> {

        @Override
        public Class<Short> getType() {
            return Short.class;
        }

        @Override
        public int getSize() {
            return Short.BYTES;
        }

        @Override
        public Short read(DataInput in) throws IOException {
            return in.readShort();
        }

        @Override
        public void write(DataOutput out, Short val) throws IOException {
            out.writeShort(val);
        }

    }

    public static class IntegerChannel extends NumberChannel<Integer> {

        @Override
        public Class<Integer> getType() {
            return Integer.class;
        }

        @Override
        public int getSize() {
            return Integer.BYTES;
        }

        @Override
        public Integer read(DataInput in) throws IOException {
            return in.readInt();
        }

        @Override
        public void write(DataOutput out, Integer val) throws IOException {
            out.writeInt(val);
        }

    }

    public static class LongChannel extends NumberChannel<Long> {

        @Override
        public Class<Long> getType() {
            return Long.class;
        }

        @Override
        public int getSize() {
            return Long.BYTES;
        }

        @Override
        public Long read(DataInput in) throws IOException {
            return in.readLong();
        }

        @Override
        public void write(DataOutput out, Long val) throws IOException {
            out.writeLong(val);
        }

    }

    public static class FloatChannel extends NumberChannel<Float> {

        @Override
        public Class<Float> getType() {
            return Float.class;
        }

        @Override
        public int getSize() {
            return Float.BYTES;
        }

        @Override
        public Float read(DataInput in) throws IOException {
            return in.readFloat();
        }

        @Override
        public void write(DataOutput out, Float val) throws IOException {
            out.writeFloat(val);
        }

    }

    public static class DoubleChannel extends NumberChannel<Double> {

        @Override
        public Class<Double> getType() {
            return Double.class;
        }

        @Override
        public int getSize() {
            return Double.BYTES;
        }

        @Override
        public Double read(DataInput in) throws IOException {
            return in.readDouble();
        }

        @Override
        public void write(DataOutput out, Double val) throws IOException {
            out.writeDouble(val);
        }
    }

    public static class UnsignedShortChannel extends NumberChannel<Integer> {

        @Override
        public Class<Integer> getType() {
            return Integer.class;
        }

        @Override
        public int getSize() {
            return Short.BYTES;
        }

        @Override
        public Integer read(DataInput in) throws IOException {
            return in.readUnsignedShort();
        }

        @Override
        public void write(DataOutput out, Integer val) throws IOException {
            out.writeShort(val);
        }
        
    }
    
    public static class UnsignedByteChannel extends NumberChannel<Integer> {

        @Override
        public Class<Integer> getType() {
            return Integer.class;
        }

        @Override
        public int getSize() {
            return Byte.BYTES;
        }

        @Override
        public Integer read(DataInput in) throws IOException {
            return in.readUnsignedByte();
        }

        @Override
        public void write(DataOutput out, Integer val) throws IOException {
            out.writeByte(val);
        }
        
    }
}
