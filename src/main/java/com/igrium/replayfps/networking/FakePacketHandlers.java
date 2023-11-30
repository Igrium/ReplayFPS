package com.igrium.replayfps.networking;

import com.igrium.replayfps.util.PlaybackUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;

public class FakePacketHandlers {
    public static void register() {
        registerVanillaWithType(HealthUpdateS2CPacket.class, (packet, l, player) -> {
            player.getHungerManager().setFoodLevel(packet.getFood());
            player.getHungerManager().setSaturationLevel(packet.getSaturation());
            return false;
        });

        
    }

    private static <T extends Packet<?>> void registerVanillaWithType(Class<T> type, PacketListenerTyped<T> listener) {
        PacketReceivedEvent.EVENT.register((uncasted, l) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            
            if (type.isInstance(uncasted) && PlaybackUtils.isViewingPlaybackPlayer() && client.cameraEntity instanceof PlayerEntity player) {
                return listener.onPacketReceived(type.cast(uncasted), l, player);
            }
            return false;
        });
    }

    private static interface PacketListenerTyped<T extends Packet<?>> {
        boolean onPacketReceived(T packet, PacketListener listener, PlayerEntity player);
    }
}
