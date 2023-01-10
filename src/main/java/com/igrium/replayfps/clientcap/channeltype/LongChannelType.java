package com.igrium.replayfps.clientcap.channeltype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LongChannelType extends NumberChannelType<Long> {

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

    @Override
    public Class<? extends Long> getChannelClass() {
        return Long.class;
    }

    @Override
    protected Long valueOf(Number value) {
        return value.longValue();
    }
    
}
