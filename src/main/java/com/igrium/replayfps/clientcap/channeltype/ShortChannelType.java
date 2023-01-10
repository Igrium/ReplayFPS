package com.igrium.replayfps.clientcap.channeltype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ShortChannelType extends NumberChannelType<Short> {

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
    
    @Override
    public Class<? extends Short> getChannelClass() {
        return Short.class;
    }

    @Override
    protected Short valueOf(Number value) {
        return value.shortValue();
    }

}
