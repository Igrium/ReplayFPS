package com.igrium.replayfps.core.screen;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.mojang.logging.LogUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
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
        });
        this.screen = screen;
        screen.ifPresent(s -> {
            s.onDisplayed();
            s.init(client, client.getWindow().getScaledHeight(), client.getWindow().getScaledHeight());
        });
    }

    public final void setScreen(@Nullable Screen screen) {
        setScreen(Optional.ofNullable(screen));
    }

    public final void clearScreen() {
        setScreen(Optional.empty());
    }

    public void render(DrawContext drawContext, float tickDelta) {
        if (!screen.isPresent()) return;
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

    // public void onOpenScreenPacket(PacketByteBuf buffer) {
    //     Identifier id = buffer.readIdentifier();
    //     ScreenSerializer<?, ?> serializer = ScreenSerializers.get(id);

    //     if (serializer == null) {
    //         LogUtils.getLogger().error("Unknown serialized screen type: " + id);
    //         clearScreen();
    //         return;
    //     }

    //     try {
    //         setScreen(serializerParse(serializer, buffer));
    //     } catch (Exception e) {
    //         LogUtils.getLogger().error("Error spawning screen: " + id, e);
    //         clearScreen();
    //     }
    // }

    // private <T> Screen serializerParse(ScreenSerializer<?, T> serializer, PacketByteBuf buf) throws Exception {
    //     T serialized = serializer.read(buf);
    //     return serializer.create(client, serialized);
    // }
}
