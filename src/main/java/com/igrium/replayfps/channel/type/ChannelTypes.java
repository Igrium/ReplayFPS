package com.igrium.replayfps.channel.type;

import com.igrium.replayfps.channel.type.NumberChannel.ByteChannel;
import com.igrium.replayfps.channel.type.NumberChannel.DoubleChannel;
import com.igrium.replayfps.channel.type.NumberChannel.FloatChannel;
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
    public static final FloatChannel FLOAT = new FloatChannel();
    public static final DoubleChannel DOUBLE = new DoubleChannel();
    public static final UnsignedShortChannel UNSIGNED_SHORT = new UnsignedShortChannel();
    public static final UnsignedByteChannel UNSIGNED_BYTE = new UnsignedByteChannel();

    public static final Matrix4fChannelType MATRIX4F = new Matrix4fChannelType();
    public static final Vector2fChannelType VECTOR2F = new Vector2fChannelType();
    
    public static final Vec3dChannelType VEC3D = new Vec3dChannelType();

    public static PlaceholderChannel placeholder(int size) {
        return new PlaceholderChannel(size);
    }
}
