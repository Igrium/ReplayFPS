package com.igrium.replayfps.clientcap.channeltype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UnsignedByteChannelType implements NumberChannelType<Integer> {

    @Override
    public int getLength() {
        return Byte.BYTES;
    }

    @Override
    public Integer read(DataInputStream in) throws IOException {
        return in.readUnsignedByte();
    }

    @Override
    public void write(DataOutputStream out, Integer val) throws IOException {
        out.writeByte(val.byteValue());
    }
    
}
