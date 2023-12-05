package com.igrium.replayfps.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.PacketByteBuf;

/**
 * A mutable field that may optionally contain a value and can be serialized.
 */
public abstract class SerializableField<T> {
    private Optional<T> value = Optional.empty();

    /**
     * Get this the current value stored in this field.
     * @return The optional value.
     */
    public final Optional<T> optional() {
        return value;
    }
    
    /**
     * Get the current value stored in this field.
     * @return The value.
     * @throws NoSuchElementException If the field is empty.
     */
    public T get() throws NoSuchElementException {
        return value.get();
    }

    /**
     * Set the value in this field.
     * @param value New optional value.
     */
    public void set(Optional<T> value) {
        this.value = Objects.requireNonNull(value);
    }

    /**
     * Set the value in this field.
     * @param value New value. May be <code>null</code>.
     */
    public final void set(@Nullable T value) {
        set(Optional.ofNullable(value));
    }

    /**
     * Clear the value in this field, making empty.
     */
    public final void clear() {
        set(Optional.empty());
    }

    /**
     * If there is currently a value in this field.
     */
    public final boolean isPresent() {
        return value.isPresent();
    }

    /**
     * Read a value of this type from a buffer.
     * 
     * @param buffer Buffer to read from.
     * @return Parsed value.
     * @throws Exception If an exception occurs parsing the value.
     */
    protected abstract T doRead(PacketByteBuf buffer) throws Exception;

    /**
     * Write a value of this type to a buffer.
     * 
     * @param value  Value to write.
     * @param buffer Buffer to write to.
     */
    protected abstract void doWrite(T value, PacketByteBuf buffer);

    /**
     * Load a serialized value from a buffer and store it in this field.
     * 
     * @param buffer Buffer to read from.
     * @return The parsed value.
     * @throws Exception If an exception occurs parsing the value.
     */
    public T read(PacketByteBuf buffer) throws Exception {
        T val = doRead(buffer);
        set(Optional.of(val));
        return val;
    }

    /**
     * Serialize the current value to a buffer.
     * @param buffer Buffer to write to.
     */
    public void write(PacketByteBuf buffer) {
        doWrite(value.get(), buffer);
    }

    public static <T> SerializableField<T> create(Function<PacketByteBuf, T> reader, BiConsumer<T, PacketByteBuf> writer) {
        return new SimpleSerializableField<>(reader, writer);
    }

    private static class SimpleSerializableField<T> extends SerializableField<T> {
        Function<PacketByteBuf, T> reader;
        BiConsumer<T, PacketByteBuf> writer;

        public SimpleSerializableField(Function<PacketByteBuf, T> reader, BiConsumer<T, PacketByteBuf> writer) {
            this.reader = reader;
            this.writer = writer;
        }

        @Override
        protected T doRead(PacketByteBuf buffer) {
            return reader.apply(buffer);
        }

        @Override
        protected void doWrite(T value, PacketByteBuf buffer) {
            writer.accept(value, buffer);
        }

    }
}
