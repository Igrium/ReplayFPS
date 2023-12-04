package com.igrium.replayfps.game.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class ClientScreenEvents {

    public static final Event<ScreenChangedEvent> SCREEN_CHANGED = EventFactory.createArrayBacked(
            ScreenChangedEvent.class, listeners -> (client, oldScreen, newScreen) -> {
                for (var l : listeners) {
                    l.onScreenChanged(client, oldScreen, newScreen);
                }
            });

    public static final Event<ScreenUpdatedEvent> SCREEN_UPDATED = EventFactory.createArrayBacked(
            ScreenUpdatedEvent.class, listeners -> (client, screen, oldVal, newVal) -> {
                for (var l : listeners) {
                    l.onScreenUpdated(client, screen, oldVal, newVal);
                }
            });

    public static interface ScreenChangedEvent {
        void onScreenChanged(MinecraftClient client, Screen oldScreen, Screen newScreen);
    }

    public static interface ScreenUpdatedEvent {
        void onScreenUpdated(MinecraftClient client, Screen screen, Object oldVal, Object newVal);
    }
}
