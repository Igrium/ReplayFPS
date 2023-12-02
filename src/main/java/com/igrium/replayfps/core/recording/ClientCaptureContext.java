package com.igrium.replayfps.core.recording;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

/**
 * The context in which a frame is captured.
 */
public interface ClientCaptureContext {
    MinecraftClient client();

    float tickDelta();

    Entity cameraEntity();

    Camera camera();

    ClientPlayerEntity localPlayer();

    GameRenderer gameRenderer();

    ClientWorld world();

    WorldRenderContext renderContext();
}
