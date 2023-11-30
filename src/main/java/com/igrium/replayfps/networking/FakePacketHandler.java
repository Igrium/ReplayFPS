package com.igrium.replayfps.networking;

import java.util.function.Consumer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

public interface FakePacketHandler<T> {
    public void registerListener(Consumer<T> consumer);

    public void write(T value, PacketByteBuf buf);

    public T parse(PacketByteBuf buf);

    public void apply(T value, MinecraftClient client, PlayerEntity player);

}
