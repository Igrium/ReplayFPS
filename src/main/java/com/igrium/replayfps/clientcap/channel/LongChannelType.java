package com.igrium.replayfps.clientcap.channel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LongChannelType implements NumberChannelType<Long> {

    @Override
    public int getLength() {
        return Long.BYTES;
    }

    @Override
    public Long read(DataInputStream in) throws IOException {
        return in.readLong();
    }

    @Override
    public void write(DataOutputStream out, Long val) throws IOException {
        out.writeLong(val);
    }
    
}
