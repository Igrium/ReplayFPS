package com.igrium.replayfps.game.networking;

import com.igrium.replayfps.core.networking.event.FakePacketRegistrationCallback;
import com.igrium.replayfps.game.networking.fake_packet.SetGamemodeFakePacket;
import com.igrium.replayfps.game.networking.fake_packet.UpdateHotbarFakePacket;
import com.igrium.replayfps.game.networking.fake_packet.UpdateSelectedSlotFakePacket;

public class DefaultFakePackets {
    public static void registerDefaults() {
        FakePacketRegistrationCallback.EVENT.register(manager -> {
            manager.registerReceiver(UpdateHotbarFakePacket.TYPE, UpdateHotbarFakePacket::apply);
            manager.registerReceiver(UpdateSelectedSlotFakePacket.TYPE, UpdateSelectedSlotFakePacket::apply);
            manager.registerReceiver(SetGamemodeFakePacket.TYPE, SetGamemodeFakePacket::apply);
        });

        UpdateHotbarFakePacket.registerListener();
        UpdateSelectedSlotFakePacket.registerListener();
        SetGamemodeFakePacket.registerListener();

        // FakePacketHandlers.register(new Identifier("replayfps:update_hotbar"), UpdateHotbarFakePacket::new);
        // FakePacketHandlers.register(new Identifier("replayfps:update_slot"), UpdateSelectedSlotFakePacket::new);
        // FakePacketHandlers.register(new Identifier("replayfps:set_gamemode"), SetGamemodeFakePacket::new);
    }
}
