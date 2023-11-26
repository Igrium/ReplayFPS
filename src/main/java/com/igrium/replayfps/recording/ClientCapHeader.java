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

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

public class ClientCapHeader {

    public static class HeaderFormatException extends IOException {
        public HeaderFormatException() {

        }

        public HeaderFormatException(String message) {
            super(message);
        }
    }
    
    private static final Identifier INVALID_IDENTIFIER = new Identifier("replayfps:invalid");
    private Logger logger = LogUtils.getLogger();

    private List<ChannelHandler<?>> channels;
    
    private int framerate = 60;
    private int framerateBase = 1;
    private int localPlayerID = -1;

    public ClientCapHeader(List<? extends ChannelHandler<?>> channels) {
        this.channels = new ArrayList<>(channels);
    }

    public ClientCapHeader() {
        this.channels = new ArrayList<>();
    }

    public final List<ChannelHandler<?>> getChannels() {
        return channels;
    }

    public int numChannels() {
        return channels.size();
    }

    public final int getLocalPlayerID() {
        return localPlayerID;
    }

    public void setLocalPlayerID(int localPlayerID) {
        this.localPlayerID = localPlayerID;
    }

    public final int getFramerate() {
        return framerate;
    }

    public final int getFramerateBase() {
        return framerateBase;
    }

    public void setFramerate(int framerate) {
        if (framerate < 1) {
            throw new IllegalArgumentException("Framerate must be at least 1.");
        }
        this.framerate = framerate;
    }
    
    public void setFramerateBase(int framerateBase) {
        if (framerateBase < 1) {
            throw new IllegalArgumentException("Framerate base must be at least 1.");
        }
        this.framerateBase = framerateBase;
    }

    public final void setFramerate(int framerate, int framerateBase) {
        setFramerate(framerateBase);
        setFramerateBase(framerateBase);
    }

    public float getFramerateFloat() {
        return ((float) framerate) / ((float) framerateBase);
    }

    public float getFrameInterval() {
        return ((float) framerateBase) / ((float) framerate);
    }

    public int getFrameIntervalMillis() {
        return (framerateBase * 1000) / framerate;
    }

    public NbtCompound writeNBT(NbtCompound nbt) {
        if (localPlayerID == -1) {
            throw new IllegalStateException("Local player ID has not been set!");
        }
        nbt.putInt("framerate", framerate);
        nbt.putInt("framerateBase", framerateBase);
        nbt.putInt("localPlayerID", localPlayerID);

        NbtList channels = new NbtList();
        for (ChannelHandler<?> handler : this.channels) {
            channels.add(writeChannelDeclaration(handler, new NbtCompound()));
        }
        nbt.put("channels", channels);

        return nbt;
    }

    private NbtCompound writeChannelDeclaration(ChannelHandler<?> channel, NbtCompound nbt) {
        Identifier id = ChannelHandlers.REGISTRY.inverse().get(channel);
        if (id == null) id = INVALID_IDENTIFIER;

        nbt.putString("id", id.toString());
        nbt.putInt("size", channel.getChannelType().getSize());
        return nbt;
    }

    public void readNBT(NbtCompound nbt) throws HeaderFormatException {
        if (nbt.contains("framerate", NbtElement.INT_TYPE)) {
            setFramerate(nbt.getInt("framerate"));
        }
        
        if (nbt.contains("framerateBase", NbtElement.INT_TYPE)) {
            setFramerateBase(nbt.getInt("framerateBase"));
        }

        if (!nbt.contains("channels", NbtElement.LIST_TYPE)) {
            throw new HeaderFormatException("No channel declaration found.");
        }
        
        NbtList channels = nbt.getList("channels", NbtElement.COMPOUND_TYPE);

        for (NbtElement element : channels) {
            this.channels.add(readChannelDeclaration((NbtCompound) element));
        }

        if (!nbt.contains("localPlayerID", NbtElement.INT_TYPE)) {
            throw new HeaderFormatException("No local player ID found.");
        }
        localPlayerID = nbt.getInt("localPlayerID");
    }

    private ChannelHandler<?> readChannelDeclaration(NbtCompound nbt) throws HeaderFormatException {
        String name = nbt.getString("id");
        Identifier id;
        try {
            id = new Identifier(name);
        } catch (InvalidIdentifierException e) {
            throw new HeaderFormatException("Invalid channel id: " + name);
        }

        if (!nbt.contains("size", NbtElement.INT_TYPE)) {
            throw new HeaderFormatException("Channel must specify a size.");
        }

        int size = nbt.getInt("size");

        ChannelHandler<?> handler = ChannelHandlers.REGISTRY.get(id);
        if (handler == null) {
            logger.warn("Unknown channel type: " + id);
            handler = new PlaceholderChannelHandler(size);
        }

        if (handler.getChannelType().getSize() != size) {
            throw new HeaderFormatException(String.format("Improper channel size for handler type '%s'! (%d != %d)",
                    id, size, handler.getChannelType().getSize()));
        }

        return handler;
    }

    public void writeHeader(OutputStream out) throws IOException {
        NbtIo.write(writeNBT(new NbtCompound()), new DataOutputStream(out));
    }

    public void readHeader(InputStream in) throws IOException {
        NbtCompound nbt = NbtIo.read(new DataInputStream(in));
        readNBT(nbt);
    }
    
    /**
     * Calculate the length of one frame.
     * @return Number of bytes in a frame.
     */
    public int calculateFrameLength() {
        int length = 0;
        for (ChannelHandler<?> handler : channels) {
            length += handler.getChannelType().getSize();
        }
        return length;
    }
}
