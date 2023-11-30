package com.igrium.replayfps.networking;

import com.igrium.replayfps.game_events.UpdateFoodEvent;
import com.igrium.replayfps.util.PlaybackUtils;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.util.Identifier;

public class FakePacketHandlers {
    public static void register() {
        registerVanillaWithType(HealthUpdateS2CPacket.class, (packet, l, player) -> {
            player.getHungerManager().setFoodLevel(packet.getFood());
            player.getHungerManager().setSaturationLevel(packet.getSaturation());
            return false;
        });

        // FOOD
        UpdateFoodEvent.EVENT.register((player, food, saturation) -> {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(food);
            buf.writeFloat(saturation);

            CustomReplayPacketManager.sendReplayPacket(new Identifier("replayfps:update_food"), buf);
        });

        CustomReplayPacketManager.registerReceiver(new Identifier("replayfps:update_food"), (client, buf, player) -> {
            player.getHungerManager().setFoodLevel(buf.readInt());
            player.getHungerManager().setSaturationLevel(buf.readFloat());
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
