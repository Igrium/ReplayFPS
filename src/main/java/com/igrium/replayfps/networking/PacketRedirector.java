package com.igrium.replayfps.networking;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;

/**
 * Modifies the functionality of a packet being played by a replay. If the
 * packet type is listed in <code>BAD_PACKETS</CODE>, this redirector plays in
 * any case.
 */
public interface PacketRedirector<T extends Packet<?>> {

    /**
     * Get the class of the packet that will be captured.
=     */
    public Class<T> getPacketClass();

    /**
     * Decide whether a given packet should redirect given the current context.
     * 
     * @param packet      Subject packet.
     * @param localPlayer The player who recorded the replay.
     * @param client      The Minecraft client.
     * @return <code>true</code> if this packet should redirect. <code>false</code>
     *         will resort to vanilla behavior.
     */
    public boolean shouldRedirect(T packet, PlayerEntity localPlayer, MinecraftClient client);

    /**
     * Called when the packet is recieved during the replay.
     * 
     * @param packet      The deserialized packet.
     * @param localPlayer The player that recorded the replay.
     * @param client      The Minecraft client.
     */
    public void redirect(T packet, PlayerEntity localPlayer, MinecraftClient client);

    default boolean shouldRedirect(Object packet, PlayerEntity localPlayer, MinecraftClient client) {
        return shouldRedirect(getPacketClass().cast(packet), localPlayer, client);
    }
}
