package com.igrium.replayfps.clientcap.channeltype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Vec3fChannelType implements ChannelType<Vector3fc> {

    @Override
    public int getLength() {
        return Float.BYTES * 3;
    }

    @Override
    public Vector3f read(DataInputStream in) throws IOException {
        return read(in, new Vector3f());
    }
    
    public Vector3f read(DataInputStream in, Vector3f dest) throws IOException {
        dest.x = in.readFloat();
        dest.y = in.readFloat();
        dest.z = in.readFloat();
        return dest;
    }

    @Override
    public void write(DataOutputStream out, Vector3fc val) throws IOException {
        out.writeFloat(val.x());
        out.writeFloat(val.y());
        out.writeFloat(val.z());
    }

    @Override
    public Class<? extends Vector3fc> getChannelClass() {
        return Vector3fc.class;
    }
    
    @Override
    public Vector3f lerp(Vector3fc from, Vector3fc to, float fac) {
        return from.lerp(to, fac, new Vector3f());
    }
}
