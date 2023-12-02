package com.igrium.replayfps.game.networking.redirector;

import com.igrium.replayfps.core.networking.PacketRedirector;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;

public class UpdateSelectedSlotRedirector implements PacketRedirector<UpdateSelectedSlotS2CPacket> {

    @Override
    public Class<UpdateSelectedSlotS2CPacket> getPacketClass() {
        return UpdateSelectedSlotS2CPacket.class;
    }

    @Override
    public boolean shouldRedirect(UpdateSelectedSlotS2CPacket packet, PlayerEntity localPlayer,
            MinecraftClient client) {
        return true;
    }

    @Override
    public void redirect(UpdateSelectedSlotS2CPacket packet, PlayerEntity localPlayer, MinecraftClient client) {
        client.execute(() -> {
            if (PlayerInventory.isValidHotbarIndex(packet.getSlot())) {
                localPlayer.getInventory().selectedSlot = packet.getSlot();
            }
        });
    }
    
}
