package com.igrium.replayfps.channel.handler;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.igrium.replayfps.channel.type.ChannelType;
import com.igrium.replayfps.channel.type.PlaceholderChannel;

import net.minecraft.util.Identifier;

public class ChannelHandlers {
    
    public static final BiMap<Identifier, ChannelHandler<?>> REGISTRY = HashBiMap.create();

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
}
