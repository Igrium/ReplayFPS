package com.igrium.replayfps.util;

import java.util.NoSuchElementException;
import java.util.Optional;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public final class SerializableFields {

    public static abstract class NumberField<T extends Number> extends SerializableField<T> {
        public byte getByte() throws NoSuchElementException {
            Optional<T> val = getValue();
            return val.isPresent() ? val.get().byteValue() : 0;
        }

        public short getShort() throws NoSuchElementException {
            Optional<T> val = getValue();
            return val.isPresent() ? val.get().shortValue() : 0;
        }
        
        public int getInt() throws NoSuchElementException {
            Optional<T> val = getValue();
            return val.isPresent() ? val.get().intValue() : 0;
        }

        public long getLong() throws NoSuchElementException {
            Optional<T> val = getValue();
            return val.isPresent() ? val.get().longValue() : 0;
        }

        public float getFloat() throws NoSuchElementException {
            Optional<T> val = getValue();
            return val.isPresent() ? val.get().floatValue() : 0;
        }

        public double getDouble() throws NoSuchElementException {
            Optional<T> val = getValue();
            return val.isPresent() ? val.get().doubleValue() : 0;
        }
    }

    public static class ByteField extends NumberField<Byte> {

        @Override
        protected Byte doRead(PacketByteBuf buffer) throws Exception {
            return buffer.readByte();
        }

        @Override
        protected void doWrite(Byte value, PacketByteBuf buffer) {
            buffer.writeByte(value);
        }
        
    }

    public static class ShortField extends NumberField<Short> {

        @Override
        protected Short doRead(PacketByteBuf buffer) throws Exception {
            return buffer.readShort();
        }

        @Override
        protected void doWrite(Short value, PacketByteBuf buffer) {
            buffer.writeShort(value);
        }
        
    }

    public static class LongField extends NumberField<Long> {

        @Override
        protected Long doRead(PacketByteBuf buffer) throws Exception {
            return buffer.readLong();
        }

        @Override
        protected void doWrite(Long value, PacketByteBuf buffer) {
            buffer.writeLong(value);
        }
        
    }

    public static class IntField extends NumberField<Integer> {

        @Override
        protected Integer doRead(PacketByteBuf buffer) {
            return buffer.readInt();
        }

        @Override
        protected void doWrite(Integer value, PacketByteBuf buffer) {
            buffer.writeInt(value);
        }
        
    }

    public static class FloatField extends NumberField<Float> {

        @Override
        protected Float doRead(PacketByteBuf buffer) throws Exception {
            return buffer.readFloat();
        }

        @Override
        protected void doWrite(Float value, PacketByteBuf buffer) {
            buffer.writeFloat(value);
        }
        
    }

    public static class DoubleField extends NumberField<Double> {

        @Override
        protected Double doRead(PacketByteBuf buffer) throws Exception {
            return buffer.readDouble();
        }

        @Override
        protected void doWrite(Double value, PacketByteBuf buffer) {
            buffer.writeDouble(value);
        }
        
    }

    public static class BoolField extends SerializableField<Boolean> {

        @Override
        protected Boolean doRead(PacketByteBuf buffer) throws Exception {
            return buffer.readBoolean();
        }

        @Override
        protected void doWrite(Boolean value, PacketByteBuf buffer) {
            buffer.writeBoolean(value);
        }
        
        public boolean getBool() throws NoSuchElementException {
            Optional<Boolean> val = getValue();
            return val.isPresent() ? val.get().booleanValue() : false;
        }
    }

    public static class CharField extends SerializableField<Character> {

        @Override
        protected Character doRead(PacketByteBuf buffer) throws Exception {
            return buffer.readChar();
        }

        @Override
        protected void doWrite(Character value, PacketByteBuf buffer) {
            buffer.writeChar(value);
        }

        public char getChar() throws NoSuchElementException {
            Optional<Character> val = getValue();
            return val.isPresent() ? val.get().charValue() : '\u0000';
        }
    }

    public static class StringField extends SerializableField<String> {

        @Override
        protected String doRead(PacketByteBuf buffer) throws Exception {
            return buffer.readString();
        }

        @Override
        protected void doWrite(String value, PacketByteBuf buffer) {
            buffer.writeString(value);
        }
        
    }

    public static class IdentifierField extends SerializableField<Identifier> {

        @Override
        protected Identifier doRead(PacketByteBuf buffer) throws Exception {
            return buffer.readIdentifier();
        }

        @Override
        protected void doWrite(Identifier value, PacketByteBuf buffer) {
            buffer.writeIdentifier(value);
        }

    }

}
