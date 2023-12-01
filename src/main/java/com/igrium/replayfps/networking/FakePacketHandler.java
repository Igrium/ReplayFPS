package com.igrium.replayfps.networking;

import java.util.function.Consumer;

import com.igrium.replayfps.networking.CustomReplayPacketManager.ReplayPacketConsumer;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public abstract class FakePacketHandler<T> implements ReplayPacketConsumer {
    private final Identifier id;

    public FakePacketHandler(Identifier id) {
        this.id = id;
        registerListener(this::sendPacket);
    }

    public Identifier getId() {
        return id;
    }

    public abstract Class<T> getType();

    public abstract void registerListener(Consumer<T> consumer);

    public abstract void write(T value, PacketByteBuf buf);

    public abstract T parse(PacketByteBuf buf);

    public abstract void apply(T value, MinecraftClient client, PlayerEntity player);

    public final void castAndApply(Object value, MinecraftClient client, PlayerEntity player) throws ClassCastException {
        apply(getType().cast(value), client, player);
    }

    @Override
    public final void onPacket(MinecraftClient client, PacketByteBuf packet, PlayerEntity localPlayer) {
        T val = parse(packet);
        client.execute(() -> apply(val, client, localPlayer));
    }

    private void sendPacket(T value) {
        PacketByteBuf buffer = PacketByteBufs.create();
        write(value, buffer);
        CustomReplayPacketManager.sendReplayPacket(id, buffer);
    }

    public abstract boolean waitForFirstPerson();
}
