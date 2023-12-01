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

    /**
     * Defines the behavior a packet should take if it's encountered while the
     * camera is <em>not</code> spectating them.
     */
    public static enum SpectatorBehavior {
        /**
         * Apply the packet anyway. Useful for non-destructive packets such as health, which
         * you wouldn't be able to see anyway as another player.
         */
        APPLY,

        /**
         * Skip handling this packet all-together. Useful for packets that must only be
         * applied only for the local player, but do not have a lasting effect (sound
         * effects, etc.) <b> Do not use for packets that could cause desyncs if missed!</code>
         */
        SKIP,

        /**
         * Queue this packet to be executed as soon as the camera begins spectating the
         * player. Try to avoid if possible because it can be buggy.
         */
        QUEUE
    }

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

    public abstract SpectatorBehavior getSpectatorBehavior();
}
