package com.igrium.replayfps.recording;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.igrium.replayfps.channel.handler.ChannelHandler;
import com.igrium.replayfps.channel.handler.ChannelHandlers;
import com.igrium.replayfps.channel.handler.ChannelHandlers.PlaceholderChannelHandler;
import com.mojang.logging.LogUtils;

import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

public class ClientCapHeader {

    
    private static final Identifier INVALID_IDENTIFIER = new Identifier("replayfps:invalid");
    private Logger logger = LogUtils.getLogger();

    private List<ChannelHandler<?>> channels;

    public ClientCapHeader(List<? extends ChannelHandler<?>> channels) {
        channels = new ArrayList<>(channels);
    }

    public List<ChannelHandler<?>> getChannels() {
        return channels;
    }

    public void writeHeader(OutputStream out) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(out);
        dataOut.writeShort(channels.size());

        for (ChannelHandler<?> handler : channels) {
            Identifier id = ChannelHandlers.REGISTRY.inverse().get(handler);
            if (id == null) id = INVALID_IDENTIFIER;

            dataOut.writeUTF(id.toString());
            dataOut.writeInt(handler.getChannelType().getSize());
        }
    }

    public void readHeader(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(in);
        int numChannels = dataIn.readUnsignedShort();

        channels = new ArrayList<>(numChannels);
        for (int i = 0; i < numChannels; i++) {
            String name = dataIn.readUTF();
            Identifier id;
            try {
                id = new Identifier(name);
            } catch (InvalidIdentifierException e) {
                throw new IOException("Invalid channel name: " + name, e);
            }

            int size = dataIn.readInt();
            if (size < 0) {
                throw new IOException("Channel size may not be negative.");
            }

            ChannelHandler<?> handler = ChannelHandlers.REGISTRY.get(id);
            if (handler == null) {
                logger.warn("Unknown channel type: " + id);
                handler = new PlaceholderChannelHandler(size);
            }

            if (handler.getChannelType().getSize() != size) {
                throw new IOException(String.format("Improper channel size for handler type '%s'! (%d != %d)", size, handler.getChannelType().getSize()));
            }

            channels.add(handler);
        }
    }
}
