package com.igrium.replayfps.networking;

import com.igrium.replayfps.networking.CustomReplayPacketManager.ReplayPacketConsumer;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class FakePacketHandlerWrapper<T> implements ReplayPacketConsumer {

    private FakePacketHandler<T> handler;
    private Identifier id;

    public FakePacketHandlerWrapper(FakePacketHandler<T> handler, Identifier id) {
        this.handler = handler;
        this.id = id;
        handler.registerListener(this::sendPacket);
    }

    @Override
    public void onPacket(MinecraftClient client, PacketByteBuf packet, PlayerEntity localPlayer) throws Exception {
        T val = handler.parse(packet);
        client.execute(() -> handler.apply(val, client, localPlayer));
    }
    
    private void sendPacket(T value) {
        PacketByteBuf buffer = PacketByteBufs.create();
        handler.write(value, buffer);
        CustomReplayPacketManager.sendReplayPacket(id, buffer);
    }

}
