package com.igrium.replayfps.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.io.CountingInputStream;
import com.igrium.replayfps.channel.handler.ChannelHandlers;
import com.igrium.replayfps.channel.type.ChannelType;

public class TestChannelHandlers {

    private static Stream<? extends ChannelType<?>> provideChannelTypes() {
        return ChannelHandlers.REGISTRY.values().stream()
                .map(handler -> handler.getChannelType())
                .distinct();
    }

    @ParameterizedTest
    @MethodSource("provideChannelTypes")
    public <T> void testChannelWrite(ChannelType<T> channel) throws IOException {
        int size = channel.getSize();
        DataOutputStream dataOut = new DataOutputStream(OutputStream.nullOutputStream());
        
        channel.write(dataOut, channel.defaultValue());
        Assertions.assertEquals(size, dataOut.size(), "Channel's declared size and written size should match.");
    }

    @ParameterizedTest
    @MethodSource("provideChannelTypes")
    public <T> void testChannelRead(ChannelType<T> channel) throws IOException {
        CountingInputStream counter = new CountingInputStream(new BlankInputStream());
        DataInputStream dataIn = new DataInputStream(counter);
        int size = channel.getSize();

        channel.read(dataIn);
        Assertions.assertEquals(size, counter.getCount(), "Channel's declared size and read size should match.");
    }

    @ParameterizedTest
    @MethodSource("provideChannelTypes")
    public <T> void testChannelConsistency(ChannelType<T> channel) throws IOException {
        testChannelConsistency(channel, channel.defaultValue());
    }

    public static <T> void testChannelConsistency(ChannelType<T> channel, T value) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(channel.getSize());
        channel.write(new DataOutputStream(buffer), value);

        ByteArrayInputStream bufferIn = new ByteArrayInputStream(buffer.toByteArray());
        T readValue = channel.read(new DataInputStream(bufferIn));

        Assertions.assertEquals(value, readValue, "The channel's parsing function is not consistent with its writing function.");
    }
}
