package com.igrium.replayfps.game.networking.redirector;

import com.igrium.replayfps.core.networking.PacketRedirector;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;

public class EntityEquipmentUpdateRedirector implements PacketRedirector<EntityEquipmentUpdateS2CPacket> {

    @Override
    public Class<EntityEquipmentUpdateS2CPacket> getPacketClass() {
        return EntityEquipmentUpdateS2CPacket.class;
    }

    @Override
    public boolean shouldRedirect(EntityEquipmentUpdateS2CPacket packet, PlayerEntity localPlayer,
            MinecraftClient client) {
        return localPlayer != null && packet.getId() == localPlayer.getId();
    }

    @Override
    public void redirect(EntityEquipmentUpdateS2CPacket packet, PlayerEntity localPlayer, MinecraftClient client) {
        client.execute(() -> doRedirect(packet, localPlayer, client));
    }

    private void doRedirect(EntityEquipmentUpdateS2CPacket packet, PlayerEntity localPlayer, MinecraftClient client) {
        if (packet.getId() != localPlayer.getId())
            throw new IllegalStateException("This packet should not redirect for entities other than the local player.");

        // Supress update of main hand equipment slot.
        for (var pair : packet.getEquipmentList()) {
            if (pair.getFirst() != EquipmentSlot.MAINHAND) {
                localPlayer.equipStack(pair.getFirst(), pair.getSecond());
            }
        }
    }
    
}
