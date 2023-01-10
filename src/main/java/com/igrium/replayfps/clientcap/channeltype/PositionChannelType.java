package com.igrium.replayfps.clientcap.channeltype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.util.math.Vec3d;

public class PositionChannelType implements ChannelType<Vec3d> {

    @Override
    public int getLength() {
        return Double.BYTES * 3;
    }

    @Override
    public Vec3d read(DataInputStream in) throws IOException {
        return new Vec3d(in.readDouble(), in.readDouble(), in.readDouble());
    }

    @Override
    public void write(DataOutputStream out, Vec3d val) throws IOException {
        out.writeDouble(val.getX());
        out.writeDouble(val.getY());
        out.writeDouble(val.getZ());
    }
    
}
