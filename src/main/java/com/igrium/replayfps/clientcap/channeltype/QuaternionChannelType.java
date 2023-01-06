package com.igrium.replayfps.clientcap.channeltype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class QuaternionChannelType implements ChannelType<Quaternionfc> {

    @Override
    public int getLength() {
        return Float.BYTES * 4;
    }

    @Override
    public Quaternionfc read(DataInputStream in) throws IOException {
        return read(in, new Quaternionf());
    }

    public Quaternionf read(DataInputStream in, Quaternionf dest) throws IOException {
        dest.w = in.readFloat();
        dest.x = in.readFloat();
        dest.y = in.readFloat();
        dest.z = in.readFloat();
        return dest;
    }

    @Override
    public void write(DataOutputStream out, Quaternionfc val) throws IOException {
        out.writeFloat(val.w());
        out.writeFloat(val.x());
        out.writeFloat(val.y());
        out.writeFloat(val.z());
    }
    
}
