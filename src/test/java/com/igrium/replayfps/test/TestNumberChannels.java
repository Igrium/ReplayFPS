package com.igrium.replayfps.test;

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

import com.igrium.replayfps.channel.type.ChannelTypes;
import com.igrium.replayfps.channel.type.NumberChannel.ByteChannel;

public class TestNumberChannels {

    @RepeatedTest(256)
    public void testByte(RepetitionInfo repetitionInfo) throws IOException {
        byte val = (byte) (repetitionInfo.getCurrentRepetition() - 1);

        ByteChannel channel = ChannelTypes.BYTE;
        TestChannelHandlers.testChannelConsistency(channel, val);
    }

    private Random random = new Random();

    @RepeatedTest(256)
    public void testShort() throws IOException {
        short val = (short) random.nextInt(Short.MIN_VALUE, Short.MAX_VALUE);
        TestChannelHandlers.testChannelConsistency(ChannelTypes.SHORT, val);
    }

    @RepeatedTest(256)
    public void testInt() throws IOException {
        int val = random.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
        TestChannelHandlers.testChannelConsistency(ChannelTypes.INTEGER, val);
    }

    @RepeatedTest(256)
    public void testLong() throws IOException {
        long val = random.nextLong(Long.MIN_VALUE, Long.MAX_VALUE);
        TestChannelHandlers.testChannelConsistency(ChannelTypes.LONG, val);
    }

    @RepeatedTest(256)
    public void testFloat() throws IOException {
        float val = Float.intBitsToFloat(random.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE));
        TestChannelHandlers.testChannelConsistency(ChannelTypes.FLOAT, val);
    }

    @RepeatedTest(256)
    public void testDouble() throws IOException {
        double val = Double.longBitsToDouble(random.nextLong(Long.MIN_VALUE, Long.MAX_VALUE));
        TestChannelHandlers.testChannelConsistency(ChannelTypes.DOUBLE, val);
    }

    @RepeatedTest(256)
    public void testUnsignedShort() throws IOException {
        int val = random.nextInt(Short.MAX_VALUE * 2);
        TestChannelHandlers.testChannelConsistency(ChannelTypes.UNSIGNED_SHORT, val);
    }

    @RepeatedTest(256)
    public void testUnsignedByte(RepetitionInfo repetitionInfo) throws IOException {
        int val = repetitionInfo.getCurrentRepetition() - 1;
        TestChannelHandlers.testChannelConsistency(ChannelTypes.UNSIGNED_BYTE, val);
    }
}
