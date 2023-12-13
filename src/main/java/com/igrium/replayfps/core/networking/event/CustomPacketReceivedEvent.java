package com.igrium.replayfps.core.networking.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.impl.networking.payload.ResolvablePayload;

/**
 * Called when a custom packet of any kind is recieved on the client.
 */
public interface CustomPacketReceivedEvent {

    public static final Event<CustomPacketReceivedEvent> EVENT = EventFactory.createArrayBacked(
            CustomPacketReceivedEvent.class,
            listeners -> payload -> {

                for (CustomPacketReceivedEvent listener : listeners) {
                    if (listener.onPacketReceived(payload))
                        return true;
                }

                return false;
            });

    /**
     * Called whenever a custom packet of any kind is recieved on the client.
     * 
     * @param payload The packet's payload.
     * @return If this packet should be "consumed". If <code>true</code> no other
     *         recievers (including the registered one) will recieve the packet.
     */
    public boolean onPacketReceived(ResolvablePayload payload);
}
