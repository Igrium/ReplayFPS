package com.igrium.replayfps.clientcap.channeltype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FloatChannelType implements NumberChannelType<Float> {

    @Override
    public int getLength() {
        return Float.BYTES;
    }

    @Override
    public Float read(DataInputStream in) throws IOException {
        return in.readFloat();
    }

    @Override
    public void write(DataOutputStream out, Float val) throws IOException {
        out.writeFloat(val);
    }
    
}
