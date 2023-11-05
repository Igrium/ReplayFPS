package com.igrium.replayfps.channel.handler;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.igrium.replayfps.channel.type.ChannelType;
import com.igrium.replayfps.channel.type.ChannelTypes;
import com.igrium.replayfps.channel.type.PlaceholderChannel;

import net.minecraft.util.Identifier;

public class ChannelHandlers {
    
    public static final BiMap<Identifier, ChannelHandler<?>> REGISTRY = HashBiMap.create();

    public static final ChannelHandler<Byte> DUMMY = register(new DummyChannelHandler(), new Identifier("replayfps:dummy"));

    public static class PlaceholderChannelHandler implements ChannelHandler<Object> {
        private final ChannelType<Object> type;

        public PlaceholderChannelHandler(int size) {
            this.type = new PlaceholderChannel(size);
        }

        @Override
        public ChannelType<Object> getChannelType() {
            return type;
        }

        @Override
        public Object capture() {
            return null;
        }

        @Override
        public void apply(Object val) {
        }
    }

    public static <T> ChannelHandler<T> register(ChannelHandler<T> handler, Identifier id) {
        REGISTRY.put(id, handler);
        return handler;
    }

    private static class DummyChannelHandler implements ChannelHandler<Byte> {

        @Override
        public ChannelType<Byte> getChannelType() {
            return ChannelTypes.BYTE;
        }

        @Override
        public Byte capture() {
            return 'N';
        }

        @Override
        public void apply(Byte val) {
        }
        
    }
}
