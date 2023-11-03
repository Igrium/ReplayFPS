package com.igrium.replayfps.channel;

import com.igrium.replayfps.channel.NumberChannel.DoubleChannel;
import com.igrium.replayfps.channel.NumberChannel.IntegerChannel;
import com.igrium.replayfps.channel.NumberChannel.LongChannel;
import com.igrium.replayfps.channel.NumberChannel.ShortChannel;

public class ChannelTypes {
    public static final ShortChannel SHORT = new ShortChannel();
    public static final IntegerChannel INTEGER = new IntegerChannel();
    public static final LongChannel LONG = new LongChannel();
    public static final DoubleChannel DOUBLE = new DoubleChannel();
}
