package com.igrium.replayfps.game.networking.redirector;

import com.igrium.replayfps.core.networking.PacketRedirector;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;

public class HealthHungerRedirector implements PacketRedirector<HealthUpdateS2CPacket> {

    @Override
    public Class<HealthUpdateS2CPacket> getPacketClass() {
        return HealthUpdateS2CPacket.class;
    }

    @Override
    public boolean shouldRedirect(HealthUpdateS2CPacket packet, PlayerEntity localPlayer, MinecraftClient client) {
        return true;
    }

    @Override
    public void redirect(HealthUpdateS2CPacket packet, PlayerEntity localPlayer, MinecraftClient client) {
        client.execute(() -> {
            localPlayer.getHungerManager().setFoodLevel(packet.getFood());
            localPlayer.getHungerManager().setSaturationLevel(packet.getSaturation());
        });
    }
}
