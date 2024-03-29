package com.igrium.replayfps.core.channel.type;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public abstract class NumberChannel<T extends Number> implements ChannelType<T> {

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

    @Override
    public float[] getRawValues(T value) {
        return new float[] { value.floatValue() };
    }

    public static class ByteChannel extends NumberChannel<Byte> {

        @Override
        public Class<Byte> getType() {
            return Byte.class;
        }

        @Override
        public int getSize() {
            return Byte.BYTES;
        }

        @Override
        public Byte read(DataInput in) throws IOException {
            return in.readByte();
        }

        @Override
        public void write(DataOutput out, Byte val) throws IOException {
            out.writeByte(val);
        }

        @Override
        public Byte defaultValue() {
            return 0;
        }
        
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

        @Override
        public Short defaultValue() {
            return 0;
        }

        @Override
        public Short interpolate(Short from, Short to, float delta) {
            return (short) (delta * (to - from) + from);
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

        @Override
        public Integer defaultValue() {
            return 0;
        }

        @Override
        public Integer interpolate(Integer from, Integer to, float delta) {
            return (int) (delta * (to - from) + from);
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

        @Override
        public Long defaultValue() {
            return 0l;
        }

        @Override
        public Long interpolate(Long from, Long to, float delta) {
            return (long) (delta * (to - from) + from);
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

        @Override
        public Float defaultValue() {
            return 0f;
        }

        @Override
        public Float interpolate(Float from, Float to, float delta) {
            return delta * (to - from) + from;
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

        @Override
        public Double defaultValue() {
            return 0d;
        }
        
        @Override
        public Double interpolate(Double from, Double to, float delta) {
            return delta * (to - from) + from;
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

        @Override
        public Integer defaultValue() {
            return 0;
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

        @Override
        public Integer defaultValue() {
            return 0;
        }
        
    }
}
