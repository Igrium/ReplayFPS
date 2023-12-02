package com.igrium.replayfps.game.networking;

import com.igrium.replayfps.core.networking.FakePacketHandlers;
import com.igrium.replayfps.game.networking.fake_packet.UpdateHotbarFakePacket;

import net.minecraft.util.Identifier;

public class DefaultFakePackets {
    public static void registerDefaults() {
        FakePacketHandlers.register(new Identifier("replayfps:update_hotbar"), UpdateHotbarFakePacket::new);
    }
}
