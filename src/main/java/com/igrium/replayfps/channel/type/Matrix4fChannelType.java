package com.igrium.replayfps.channel.type;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class Matrix4fChannelType extends ChannelType<Matrix4fc> {

    private static final int BUFFER_SIZE = 4 * 4;
    private float[] buffer = new float[BUFFER_SIZE];

    @Override
    public Class<Matrix4fc> getType() {
        return Matrix4fc.class;
    }

    @Override
    public int getSize() {
        return BUFFER_SIZE * Float.BYTES;
    }

    @Override
    public Matrix4fc read(DataInput in) throws IOException {
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = in.readFloat();
        }
        return new Matrix4f().set(buffer);
    }

    @Override
    public void write(DataOutput out, Matrix4fc val) throws IOException {
        val.get(buffer);
        for (int i = 0; i < buffer.length; i++) {
            out.writeFloat(buffer[i]);
        }
    }
    
}
