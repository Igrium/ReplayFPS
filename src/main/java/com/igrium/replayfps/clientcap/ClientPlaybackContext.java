package com.igrium.replayfps.clientcap;

import java.util.Optional;

import com.replaymod.replay.ReplayHandler;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

/**
 * The context in which a ClientCap frame is played back.
 */
public interface ClientPlaybackContext {

    /**
     * The Minecraft client.
     */
    MinecraftClient client();

    /**
     * The handler for the replay being played back.
     */
    ReplayHandler replayHandler();

    /**
     * The current camera entity.
     */
    Optional<Entity> cameraEntity();

    /**
     * The player that the client was controlling during recording.
     */
    Optional<AbstractClientPlayerEntity> localPlayer();

    /**
     * The active camera.
     */
    Camera camera();
    
    /**
     * The current timestamp in the replay (milliseconds).
     */
    int timestamp();

    /**
     * The current world.
     */
    Optional<ClientWorld> world();
}
