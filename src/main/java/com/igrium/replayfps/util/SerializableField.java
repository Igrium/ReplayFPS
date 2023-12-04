package com.igrium.replayfps.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.PacketByteBuf;

public abstract class SerializableField<T> {
    private Optional<T> value = Optional.empty();

    public final Optional<T> getValue() {
        return value;
    }

    @Nullable
    public T get() {
        return value.orElse(null);
    }

    public void set(Optional<T> value) {
        this.value = Objects.requireNonNull(value);
    }

    public final void set(T value) {
        set(Optional.ofNullable(value));
    }

    protected abstract T doRead(PacketByteBuf buffer) throws Exception;

    protected abstract void doWrite(T value, PacketByteBuf buffer);

    public T read(PacketByteBuf buffer) throws Exception {
        T val = doRead(buffer);
        set(Optional.of(val));
        return val;
    }

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
