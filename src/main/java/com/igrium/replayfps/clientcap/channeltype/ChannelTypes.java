package com.igrium.replayfps.clientcap.channeltype;

public final class ChannelTypes {
    private ChannelTypes() {};

    public static final ByteChannelType BYTE = new ByteChannelType();
    public static final DoubleChannelType DOUBLE = new DoubleChannelType();
    public static final FloatChannelType FLOAT = new FloatChannelType();
    public static final IntChannelType INT = new IntChannelType();
    public static final LongChannelType LONG = new LongChannelType();
    public static final QuaternionChannelType QUATERNION = new QuaternionChannelType();
    public static final ShortChannelType SHORT = new ShortChannelType();

    public static final UnsignedByteChannelType UNSIGNED_BYTE = new UnsignedByteChannelType();
    public static final UnsignedIntChannelType UNSIGNED_INT = new UnsignedIntChannelType();
    
    public static final Vec3dChannelType VEC3D = new Vec3dChannelType();
    public static final Vec3fChannelType VEC3F = new Vec3fChannelType();
    public static final PositionChannelType POSITION = new PositionChannelType();

}
