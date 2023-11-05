package com.igrium.replayfps.channel.type;

import com.igrium.replayfps.channel.type.NumberChannel.ByteChannel;
import com.igrium.replayfps.channel.type.NumberChannel.DoubleChannel;
import com.igrium.replayfps.channel.type.NumberChannel.IntegerChannel;
import com.igrium.replayfps.channel.type.NumberChannel.LongChannel;
import com.igrium.replayfps.channel.type.NumberChannel.ShortChannel;
import com.igrium.replayfps.channel.type.NumberChannel.UnsignedByteChannel;
import com.igrium.replayfps.channel.type.NumberChannel.UnsignedShortChannel;

public class ChannelTypes {
    public static final ByteChannel BYTE = new ByteChannel();
    public static final ShortChannel SHORT = new ShortChannel();
    public static final IntegerChannel INTEGER = new IntegerChannel();
    public static final LongChannel LONG = new LongChannel();
    public static final DoubleChannel DOUBLE = new DoubleChannel();
    public static final UnsignedShortChannel UNSIGNED_SHORT = new UnsignedShortChannel();
    public static final UnsignedByteChannel UNSIGNED_BYTE = new UnsignedByteChannel();

    public static PlaceholderChannel placeholder(int size) {
        return new PlaceholderChannel(size);
    }
}
