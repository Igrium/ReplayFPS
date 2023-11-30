package com.igrium.replayfps.test;

import com.igrium.replayfps.networking.CustomReplayPacketManager;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

/**
 * The dumbest fucking class to test fake packets
 */
public class FakePacketTest {

    private static long lastMoo;
    public static void doPlaySound() {
        long now = Util.getMeasuringTimeMs();

        if (now - lastMoo < 500) return;

        MinecraftClient client = MinecraftClient.getInstance();
        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvent.of(new Identifier("minecraft:entity.cow.death")), 1, 1));

        lastMoo = now;
    }

    public static void register() {
        ItemTooltipCallback.EVENT.register((stack, context, text) -> {
            if (stack.getItem().equals(Items.WOODEN_HOE)) {
                FakePacketTest.doPlaySound();
                    
                PacketByteBuf packet = PacketByteBufs.create();
                packet.writeInt(0);
                CustomReplayPacketManager.sendReplayPacket(new Identifier("replayfps", "testpacket"), packet);
            }

        });

        CustomReplayPacketManager.registerReceiver(new Identifier("replayfps", "testpacket"), (client, packet, player) -> {
            doPlaySound();
        });
    }
}
