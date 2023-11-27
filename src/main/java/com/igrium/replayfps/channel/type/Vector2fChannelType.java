package com.igrium.replayfps.channel.type;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.joml.Vector2f;
import org.joml.Vector2fc;

public class Vector2fChannelType implements ChannelType<Vector2fc> {

    @Override
    public Class<Vector2fc> getType() {
        return Vector2fc.class;
    }

    @Override
    public int getSize() {
        return Float.BYTES * 2;
    }

    @Override
    public Vector2fc read(DataInput in) throws IOException {
        float x = in.readFloat();
        float y = in.readFloat();
        return new Vector2f(x, y);
    }

    @Override
    public void write(DataOutput out, Vector2fc val) throws IOException {
        out.writeFloat(val.x());
        out.writeFloat(val.y());
    }

    @Override
    public Vector2fc defaultValue() {
        return new Vector2f();
    }
    
    @Override
    public Vector2fc interpolate(Vector2fc from, Vector2fc to, float delta) {
        return from.lerp(to, delta, new Vector2f());
    }

    @Override
    public String getName() {
        return "Vector2f";
    }
}
