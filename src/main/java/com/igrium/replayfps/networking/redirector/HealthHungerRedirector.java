package com.igrium.replayfps.networking.redirector;

import com.igrium.replayfps.mixins_game.LivingEntityAccessor;
import com.igrium.replayfps.networking.PacketRedirector;

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
            doUpdateHealth(packet.getHealth(), localPlayer);
            localPlayer.getHungerManager().setFoodLevel(packet.getFood());
            localPlayer.getHungerManager().setSaturationLevel(packet.getSaturation());
        });
    }

    private void doUpdateHealth(float health, PlayerEntity player) {
        if (health == player.getHealth()) return;
        float f = player.getHealth() - health;
        if (f < 0) {
            player.setHealth(health);
            player.timeUntilRegen = 10;
        } else {
            ((LivingEntityAccessor) player).setLastDamageTaken(f);
            player.timeUntilRegen = 20;
            player.setHealth(health);
            player.maxHurtTime = 10;
            player.hurtTime = player.maxHurtTime;
        }
    }
    
}
