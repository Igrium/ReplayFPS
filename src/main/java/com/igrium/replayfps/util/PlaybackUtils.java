package com.igrium.replayfps.util;

import com.igrium.replayfps.playback.ClientCapPlayer;
import com.igrium.replayfps.playback.ClientPlaybackContext;
import com.igrium.replayfps.playback.ClientPlaybackModule;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public final class PlaybackUtils {
    private PlaybackUtils() {};

    /**
     * If there is a client-capture playing, get the ID of the local player it was
     * captured on. Should only be used when you don't have access to a
     * {@link ClientPlaybackContext}.
     * 
     * @return ID of the player who captured the replay, or <code>null</code> if there is no
     *         client-capture playing.
     */
    public static Integer getCurrentPlaybackPlayerID() {
        ClientCapPlayer player = ClientPlaybackModule.getInstance().getCurrentPlayer();
        if (player == null) return null;
        if (player.getReader().getHeader() == null) return null;
        return player.getReader().getHeader().getLocalPlayerID();
    }

    /**
     * If there is a client-capture playing, get the local player that it was
     * captured on. Should only be called when you don't have access to a
     * {@link ClientPlaybackContext}.
     * 
     * @return The player who captured the replay. <code>null</code> if there is no
     *         client-capture playing or the player could not be found.
     */
    @SuppressWarnings("resource")
    public static PlayerEntity getCurrentPlaybackPlayer() {
        World world = MinecraftClient.getInstance().world;
        if (world == null) return null;

        Integer id = getCurrentPlaybackPlayerID();
        if (id == null) return null;
        
        if (world.getEntityById(id) instanceof PlayerEntity player) {
            return player;
        }
        return null;
    }

    /**
     * Determine if the current camera entity is the player that recorded the
     * current client-capture.
     * 
     * @return <code>true</code> if we're viewing from the perspective from the
     *         original capture player. <code>false</code> if we're not playing a
     *         client-capture or we're not looking through their perspective.
     */
    @SuppressWarnings("resource")
    public static boolean isViewingPlaybackPlayer() {
        Entity camera = MinecraftClient.getInstance().cameraEntity;
        if (camera == null) return false;
        return Integer.valueOf(camera.getId()).equals(getCurrentPlaybackPlayerID());
    }
    
}
