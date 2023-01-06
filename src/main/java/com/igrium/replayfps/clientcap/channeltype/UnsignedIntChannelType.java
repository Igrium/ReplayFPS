package com.igrium.replayfps.clientcap.channeltype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UnsignedIntChannelType implements NumberChannelType<Long> {

    @Override
    public int getLength() {
        return Integer.BYTES;
    }

    @Override
    public Long read(DataInputStream in) throws IOException {
        return Integer.toUnsignedLong(in.readInt());
    }

    @Override
    public void write(DataOutputStream out, Long val) throws IOException {
        out.writeInt(val.intValue());
    }
    
}
