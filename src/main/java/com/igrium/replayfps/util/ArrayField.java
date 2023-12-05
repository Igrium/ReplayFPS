package com.igrium.replayfps.util;

import net.minecraft.network.PacketByteBuf;

public abstract class ArrayField<T> extends SerializableField<T[]> {

    @Override
    @SuppressWarnings("unchecked")
    protected T[] doRead(PacketByteBuf buffer) throws Exception {
        int amount = buffer.readUnsignedShort();
        T[] array = (T[]) new Object[amount];
        for (int i = 0; i < amount; i++) {
            array[i] = readValue(buffer);
        }
        return array;
    }

    @Override
    protected void doWrite(T[] value, PacketByteBuf buffer) {
        int amount = value.length;
        buffer.writeShort(amount);

        for (T val : value) {
            writeValue(val, buffer);
        }
    }
    
    protected abstract T readValue(PacketByteBuf buffer) throws Exception;
    protected abstract void writeValue(T value, PacketByteBuf buffer);

    public static class StringArrayField extends ArrayField<String> {

        @Override
        protected String readValue(PacketByteBuf buffer) throws Exception {
            return buffer.readString();
        }

        @Override
        protected void writeValue(String value, PacketByteBuf buffer) {
            buffer.writeString(value);
        }
        
    }
}
