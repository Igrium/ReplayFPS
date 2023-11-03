package com.igrium.replayfps.channel.type;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A channel type designed to be used as a placeholder when a channel type was not found.
 */
public class PlaceholderChannel extends ChannelType<Object> {

    private final int size;
    private final byte[] buffer;

    public PlaceholderChannel(int size) {
        this.size = size;
        this.buffer = new byte[size];
    }

    @Override
    public Class<Object> getType() {
        return Object.class;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public Object read(InputStream in) throws IOException {
        if (in.read(buffer) < 0) {
            throw new EOFException();
        };
        return null;
    }

    @Override
    public void write(OutputStream out, Object val) throws IOException {
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = 0;
        }
        out.write(buffer);
    }
    
}
