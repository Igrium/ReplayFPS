package com.igrium.replayfps.core.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;

public interface CustomScreenRenderCallback {

    public static final Event<CustomScreenRenderCallback> EVENT = EventFactory.createArrayBacked(
            CustomScreenRenderCallback.class,
            listeners -> (gameRenderer, drawContext, mouseX, mouseY, tickDelta) -> {
                for (var l : listeners) {
                    l.onRenderCustomScreen(gameRenderer, drawContext, mouseX, mouseY, tickDelta);
                }
            });

    public void onRenderCustomScreen(GameRenderer gameRenderer, DrawContext drawContext, int mouseX, int mouseY,
            float tickDelta);
}
