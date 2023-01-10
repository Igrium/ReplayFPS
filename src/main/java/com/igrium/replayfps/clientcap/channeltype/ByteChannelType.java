package com.igrium.replayfps.clientcap.channeltype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ByteChannelType extends NumberChannelType<Byte> {

    @Override
    public int getLength() {
        return Byte.BYTES;
    }

    @Override
    public Byte read(DataInputStream in) throws IOException {
        return in.readByte();
    }

    @Override
    public void write(DataOutputStream out, Byte val) throws IOException {
        out.writeByte(val);
    }

    @Override
    public Class<? extends Byte> getChannelClass() {
        return Byte.class;
    }

    @Override
    protected Byte valueOf(Number value) {
        return value.byteValue();
    }
    
}
