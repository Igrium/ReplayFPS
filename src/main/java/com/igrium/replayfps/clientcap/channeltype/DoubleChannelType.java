package com.igrium.replayfps.clientcap.channeltype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DoubleChannelType implements NumberChannelType<Double> {

    @Override
    public int getLength() {
        return Double.BYTES;
    }

    @Override
    public Double read(DataInputStream in) throws IOException {
        return in.readDouble();
    }

    @Override
    public void write(DataOutputStream out, Double val) throws IOException {
        out.writeDouble(val);
    }
    
}
