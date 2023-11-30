package com.igrium.replayfps.networking;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * Called when a custom packet of any kind is recieved on the client.
 */
public interface ClientPacketReceivedEvent {

    public static final Event<ClientPacketReceivedEvent> EVENT = EventFactory.createArrayBacked(
            ClientPacketReceivedEvent.class,
            listeners -> (channel, payload) -> {

                for (ClientPacketReceivedEvent listener : listeners) {
                    if (listener.onPacketReceived(channel, PacketByteBufs.slice(payload)))
                        return true;
                }

                return false;
            });

    /**
     * Called whenever a custom packet of any kind is recieved on the client.
     * 
     * @param channel The packet channel.
     * @param payload The packet's payload.
     * @return If this packet should be "consumed". If <code>true</code> no other
     *         recievers (including the registered one) will recieve the packet.
     */
    public boolean onPacketReceived(Identifier channel, PacketByteBuf payload);
}
