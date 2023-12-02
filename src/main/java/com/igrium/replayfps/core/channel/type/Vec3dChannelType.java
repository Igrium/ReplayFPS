package com.igrium.replayfps.core.channel.type;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.util.math.Vec3d;

public class Vec3dChannelType implements ChannelType<Vec3d> {

    @Override
    public Class<Vec3d> getType() {
        return Vec3d.class;
    }

    @Override
    public int getSize() {
        return Double.BYTES * 3;
    }

    @Override
    public Vec3d read(DataInput in) throws IOException {
        double x = in.readDouble();
        double y = in.readDouble();
        double z = in.readDouble();
        return new Vec3d(x, y, z);
    }

    @Override
    public void write(DataOutput out, Vec3d val) throws IOException {
        out.writeDouble(val.getX());
        out.writeDouble(val.getY());
        out.writeDouble(val.getZ());
    }

    @Override
    public Vec3d defaultValue() {
        return new Vec3d(0, 0, 0);
    }
    
    @Override
    public Vec3d interpolate(Vec3d from, Vec3d to, float delta) {
        return from.lerp(to, delta);
    }

    @Override
    public float[] getRawValues(Vec3d value) {
        return new float[] { (float) value.x, (float) value.y, (float) value.z };
    }
}
