package com.igrium.replayfps.channel.type;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.joml.Vector4f;
import org.joml.Vector4fc;

public class Vector4fChannelType implements ChannelType<Vector4fc> {

    @Override
    public Class<Vector4fc> getType() {
        return Vector4fc.class;
    }

    @Override
    public int getSize() {
        return Float.BYTES * 4;
    }

    @Override
    public Vector4fc read(DataInput in) throws IOException {
        float x = in.readFloat();
        float y = in.readFloat();
        float z = in.readFloat();
        float w = in.readFloat();

        return new Vector4f(x, y, z, w);
    }

    @Override
    public void write(DataOutput out, Vector4fc val) throws IOException {
        out.writeFloat(val.x());
        out.writeFloat(val.y());
        out.writeFloat(val.z());
        out.writeFloat(val.w());
    }

    @Override
    public Vector4fc defaultValue() {
        return new Vector4f();
    }
    
    @Override
    public String getName() {
        return "Vector4f";
    }

    @Override
    public float[] getRawValues(Vector4fc value) {
        return new float[] { value.x(), value.y(), value.z(), value.w() };
    }
}
