package com.igrium.replayfps.core.screen;

import java.util.Optional;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

public final class ScreenSerializers {
    private static final BiMap<Identifier, ScreenSerializer<?, ?>> REGISTRY = HashBiMap.create();
    private static final BiMap<Class<? extends Screen>, ScreenSerializer<?, ?>> CLASS_REGISTRY = HashBiMap.create();

    public static void register(Identifier id, ScreenSerializer<?, ?> serializer) {
        REGISTRY.put(id, serializer);
        CLASS_REGISTRY.put(serializer.getScreenType(), serializer);
    }

    public static ScreenSerializer<?, ?> get(Identifier id) {
        return REGISTRY.get(id);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Screen> ScreenSerializer<T, ?> get(Class<T> screenType) {
        return (ScreenSerializer<T, ?>) CLASS_REGISTRY.get(screenType);
    }

    public static Identifier getId(ScreenSerializer<?, ?> serializer) {
        return REGISTRY.inverse().get(serializer);
    }

    public static Optional<ScreenSerializer<?, ?>> getCurrentSerializer(MinecraftClient client) {
        if (client.currentScreen == null) return Optional.empty();
        return Optional.ofNullable(get(client.currentScreen.getClass()));
    }
}
