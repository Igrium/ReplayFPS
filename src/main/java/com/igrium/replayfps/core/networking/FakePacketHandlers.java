package com.igrium.replayfps.core.networking;

import java.util.function.Function;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.util.Identifier;


public class FakePacketHandlers {
    public static final BiMap<Identifier, FakePacketHandler<?>> REGISTRY = HashBiMap.create();

    public static void register(Identifier id, Function<Identifier, FakePacketHandler<?>> factory) {
        var handler = factory.apply(id);
        REGISTRY.put(id, handler);
    }
}
