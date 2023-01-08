package com.igrium.replayfps.clientcap.channeltype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.joml.Vector2f;
import org.joml.Vector2fc;

public class Vec2fChannelType implements ChannelType<Vector2fc> {

    @Override
    public int getLength() {
        return Float.BYTES * 2;
    }

    @Override
    public Vector2f read(DataInputStream in) throws IOException {
        return new Vector2f(
            in.readFloat(),
            in.readFloat()
        );
    }

    @Override
    public void write(DataOutputStream out, Vector2fc val) throws IOException {
        out.writeFloat(val.x());
        out.writeFloat(val.y());
    }
    
}
