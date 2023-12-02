package com.igrium.replayfps.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.replayfps.ReplayFPS;
import com.igrium.replayfps.core.playback.ClientCapPlayer;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameMode;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    private GameMode replayfps$prevGamemode = null;

    // Trick game renderer into thinking we're in survival mode so that it renders the survival mod hud.

    @Inject(method = "render", at = @At("HEAD"))
    void replayfps$onStartRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        ClientCapPlayer playback = ClientPlaybackModule.getInstance().getCurrentPlayer();
        MinecraftClient client = MinecraftClient.getInstance();
        if (playback == null || client.world == null || !ReplayFPS.getInstance().getConfig().shouldDrawHud()) {
            replayfps$prevGamemode = null;
            return;
        }
        
        int localPlayerID = playback.getReader().getHeader().getLocalPlayerID();
        Entity localPlayer = client.world.getEntityById(localPlayerID);
        if (localPlayer == null) {
            replayfps$prevGamemode = null;
            return;
        }
        
        if (localPlayer.equals(client.getCameraEntity()) && localPlayer instanceof PlayerEntity localPlayerEnt) {
            // TODO: Store recording gamemode.
            replayfps$prevGamemode = client.interactionManager.getCurrentGameMode();
            client.interactionManager.setGameMode(GameMode.SURVIVAL);
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    void replayfps$onEndRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        if (replayfps$prevGamemode != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            client.interactionManager.setGameMode(replayfps$prevGamemode);
        }
        replayfps$prevGamemode = null;
    }
}
