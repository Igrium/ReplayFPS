package com.igrium.replayfps.clientcap;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

/**
 * The context in which a ClientCap frame is captured.
 */
public interface ClientCaptureContext {
    /**
     * The minecraft client.
     */
    MinecraftClient client();

    /**
     * The current tick delta.
     */
    float tickDelta();

    /**
     * The client's camera entity.
     */
    Entity cameraEntity();

    /**
     * The active camera.
     */
    Camera camera();

    /**
     * The camera's FOV.
     */
    double fov();

    /**
     * The client's game renderer.
     */
    GameRenderer gameRenderer();

    /**
     * The current world.
     */
    ClientWorld world();

    Frustum frustum();

}
