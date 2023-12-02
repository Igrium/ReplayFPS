package com.igrium.replayfps.game.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

public interface ClientJoinedWorldEvent {
    public static final Event<ClientJoinedWorldEvent> EVENT = EventFactory.createArrayBacked(
        ClientJoinedWorldEvent.class, listeners -> (client, world) -> {
            for (var l : listeners) {
                l.onJoinedWorld(client, world);
            }
        });

    void onJoinedWorld(MinecraftClient client, ClientWorld world);
}
