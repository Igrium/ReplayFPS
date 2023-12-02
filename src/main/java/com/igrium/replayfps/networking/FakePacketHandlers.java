package com.igrium.replayfps.networking;

import java.util.function.Function;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.igrium.replayfps.networking.fake_packet.UpdateInventoryFakePacket;

import net.minecraft.util.Identifier;


public class FakePacketHandlers {
    public static final BiMap<Identifier, FakePacketHandler<?>> REGISTRY = HashBiMap.create();

    public static void register(Identifier id, Function<Identifier, FakePacketHandler<?>> factory) {
        var handler = factory.apply(id);
        REGISTRY.put(id, handler);
    }

    public static void registerDefaults() {
        register(new Identifier("replayfps:update_inventory"), UpdateInventoryFakePacket::new);
    }
}
