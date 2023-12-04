package com.igrium.replayfps.core.screen;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.mojang.logging.LogUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;


public class PlaybackScreenManager {
    private final MinecraftClient client;

    private float mouseX;
    private float mouseY;

    public PlaybackScreenManager(MinecraftClient client) {
        this.client = client;
    }

    private Optional<Screen> screen = Optional.empty();

    public final float getMouseX() {
        return mouseX;
    }

    public void setMouseX(float mouseX) {
        this.mouseX = mouseX;
    }

    public final float getMouseY() {
        return mouseY;
    }

    public void setMouseY(float mouseY) {
        this.mouseY = mouseY;
    }

    public final Optional<Screen> getScreen() {
        return screen;
    }

    public void setScreen(Optional<Screen> screen) {
        this.screen.ifPresent(s -> {
            s.removed();
            prevSizeX = -1;
            prevSizeY = -1;
        });
        this.screen = screen;
        screen.ifPresent(s -> {
            s.onDisplayed();
        });
    }

    public final void setScreen(@Nullable Screen screen) {
        setScreen(Optional.ofNullable(screen));
    }

    public final void clearScreen() {
        setScreen(Optional.empty());
    }

    private int prevSizeX = -1;
    private int prevSizeY = -1;

    public void render(DrawContext drawContext, float tickDelta) {
        if (!screen.isPresent()) return;
        // Don't draw over the game menu.
        if (client.currentScreen instanceof GameMenuScreen) return;

        int sizeX = drawContext.getScaledWindowWidth();
        int sizeY = drawContext.getScaledWindowHeight();

        if (prevSizeX != sizeX || prevSizeY != sizeY) {
            screen.get().init(client, sizeX, sizeY);
            prevSizeX = sizeX;
            prevSizeY = sizeY;
        }

        screen.get().render(drawContext, (int) mouseX, (int) mouseY, tickDelta);
    }

    public void tick() {
        screen.ifPresent(s -> s.tick());
    }

    public MinecraftClient getClient() {
        return client;
    }

    public <T> void openScreen(ScreenSerializer<?, T> serializer, Object serialized) {
        try {
            Screen screen = serializer.create(client, serializer.getSerializedType().cast(serialized));
            setScreen(screen);
        } catch (Exception e) {
            LogUtils.getLogger().error("Error opening screen " + serializer.getScreenType().getSimpleName(), e);
        }
    }
}
