package com.igrium.replayfps.core.networking;

import java.util.Objects;
import java.util.function.Consumer;

import com.igrium.replayfps.core.networking.CustomReplayPacketManager.ReplayPacketConsumer;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * A fake packet allows the client to add an "instruction" during recording that
 * gets executed during playback.
 */
public abstract class FakePacketHandler<T> implements ReplayPacketConsumer {
    private final Identifier id;

    /**
     * Defines the behavior a packet should take if it's encountered while the
     * camera is <em>not</em> spectating them.
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
         * effects, etc.) <b> Do not use for packets that could cause desyncs if missed!</b>
         */
        SKIP,

        /**
         * Queue this packet to be executed as soon as the camera begins spectating the
         * player. Try to avoid if possible because it can be buggy.
         */
        QUEUE
    }

    /**
     * Create a fake packet handler.
     * @param id ID to register under.
     */
    public FakePacketHandler(Identifier id) {
        this.id = Objects.requireNonNull(id);
        registerListener(this::sendPacket);
    }

    /**
     * Get the ID of this packet handler.
     */
    public Identifier getId() {
        return id;
    }

    /**
     * Get the unserialized data type that this packet handler will use.
     */
    public abstract Class<T> getType();

    /**
     * Use fabric events and whatnot to hook into the game to register this fake
     * packet to be sent at the appropriate time.
     * 
     * @param consumer This consumer shall be called with the packet data when it's
     *                 time to send it.
     */
    public abstract void registerListener(Consumer<T> consumer);

    /**
     * Serialize an instance of this packet's data.
     * @param value Value to write.
     * @param buf Buffer to write to.
     */
    public abstract void write(T value, PacketByteBuf buf);

    /**
     * Deserialize an instance of this packet's data from a buffer.
     * @param buf Buffer to read from.
     * @return Deserialized value.
     */
    public abstract T parse(PacketByteBuf buf);

    /**
     * Called during replay playback to apply this packet to the game.
     * 
     * @param value  Packet value.
     * @param client The local client.
     * @param player The entity representing the player that recorded the replay.
     *               This is the player executing the client-cap file.
     */
    public abstract void apply(T value, MinecraftClient client, PlayerEntity player);

    /**
     * Called during replay playback to apply this packet to the game.
     * 
     * @param value  Packet value.
     * @param client The local client.
     * @param player The entity representing the player that recorded the replay.
     *               This is the player executing the client-cap file.
     * @throws ClassCastException If the supplied data is of the wrong type.
     */
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

    /**
     * Declare what this packet should do if it is recieved during a replay and
     * we're <em>not</em> spectating the local player.
     */
    public SpectatorBehavior getSpectatorBehavior() {
        return SpectatorBehavior.APPLY;
    }
}
