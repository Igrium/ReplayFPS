package com.igrium.replayfps.clientcap.channeltype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class IntChannelType implements NumberChannelType<Integer> {

    @Override
    public int getLength() {
        return Integer.BYTES;
    }

    @Override
    public Integer read(DataInputStream in) throws IOException {
        return in.readInt();
    }

    @Override
    public void write(DataOutputStream out, Integer val) throws IOException {
        out.writeInt(val);
    }
    
}
