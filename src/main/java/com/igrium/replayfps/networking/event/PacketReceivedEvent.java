package com.igrium.replayfps.networking.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;

/**
 * Called when a packet of any kind is received.
 */
public interface PacketReceivedEvent {

    public static Event<PacketReceivedEvent> EVENT = EventFactory.createArrayBacked(PacketReceivedEvent.class,
            listeners -> (packet, listener) -> {
                for (var l : listeners) {
                    if (l.onPacketReceived(packet, listener)) return true;
                }
                return false;
            });

    /**
     * Called when a packet of any kind is received.
     * @param packet The packet.
     * @param listener Relevant packet listener.
     * @return If this packet should be "consumed". If <code>true</code> no other
     *         recievers (including the default one) will recieve the packet.
     */
    public boolean onPacketReceived(Packet<?> packet, PacketListener listener);

}
