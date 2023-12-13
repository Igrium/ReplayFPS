package com.igrium.replayfps.game.networking;

import com.igrium.replayfps.core.networking.old.FakePacketHandlers;
import com.igrium.replayfps.game.networking.fake_packet.old.SetGamemodeFakePacket;
import com.igrium.replayfps.game.networking.fake_packet.old.UpdateHotbarFakePacket;
import com.igrium.replayfps.game.networking.fake_packet.old.UpdateSelectedSlotFakePacket;

import net.minecraft.util.Identifier;

public class DefaultFakePackets {
    public static void registerDefaults() {
        // FakePacketHandlers.register(new Identifier("replayfps:update_hotbar"), UpdateHotbarFakePacket::new);
        // FakePacketHandlers.register(new Identifier("replayfps:update_slot"), UpdateSelectedSlotFakePacket::new);
        // FakePacketHandlers.register(new Identifier("replayfps:set_gamemode"), SetGamemodeFakePacket::new);
    }
}
