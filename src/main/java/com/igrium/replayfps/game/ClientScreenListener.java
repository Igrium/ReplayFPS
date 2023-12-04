package com.igrium.replayfps.game;

import com.igrium.replayfps.core.screen.ScreenSerializer;
import com.igrium.replayfps.core.screen.ScreenSerializers;
import com.igrium.replayfps.game.event.ClientScreenEvents;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class ClientScreenListener {
    private static Object lastSerializedScreen;
    private static Screen lastScreen;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientScreenListener::onTick);
    }

    public static void onTick(MinecraftClient client) {
        Screen currentScreen = client.currentScreen;

        boolean skipScreenUpdate = false;

        if (currentScreen != lastScreen) {
            ClientScreenEvents.SCREEN_CHANGED.invoker().onScreenChanged(client, lastScreen, currentScreen);

            lastSerializedScreen = null;
            lastScreen = currentScreen;
            skipScreenUpdate = true;
        }

        if (currentScreen == null) return;
        
        ScreenSerializer<?, ?> serializer = ScreenSerializers.get(currentScreen.getClass());
        if (serializer == null) return;

        // If screen has a serializer and (it has changed OR we just set this screen)
        if (lastSerializedScreen == null || hasChanged(serializer, currentScreen, lastSerializedScreen)) {
            Object newVal = serializeScreen(serializer, currentScreen);

            // No need to run screen updated if we just set this screen.
            if (!skipScreenUpdate) {
                ClientScreenEvents.SCREEN_UPDATED.invoker().onScreenUpdated(client, currentScreen, lastSerializedScreen, newVal);
            }

            lastSerializedScreen = newVal;
        }
    }

    private static <S extends Screen, T> boolean hasChanged(ScreenSerializer<S, T> serializer, Screen screen, Object prev) {
        if (!serializer.getScreenType().isInstance(screen) || !serializer.getSerializedType().isInstance(prev)) return true;
        S screenCasted = serializer.getScreenType().cast(screen);
        T prevCasted = serializer.getSerializedType().cast(prev);

        return serializer.hasChanged(screenCasted, prevCasted);
    }

    private static <S extends Screen, T> T serializeScreen(ScreenSerializer<S, T> serializer, Screen screen) {
        return serializer.serialize(serializer.getScreenType().cast(screen));
    }
}
