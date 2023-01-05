package com.igrium.replayfps.clientcap.channel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ShortChannelType implements NumberChannelType<Short> {

    @Override
    public int getLength() {
        return Short.BYTES;
    }

    @Override
    public Short read(DataInputStream in) throws IOException {
        return in.readShort();
    }

    @Override
    public void write(DataOutputStream out, Short val) throws IOException {
        out.writeShort(val);
    }
    
}
