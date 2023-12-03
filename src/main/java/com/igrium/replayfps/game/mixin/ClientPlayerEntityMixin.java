package com.igrium.replayfps.game.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.replayfps.game.event.ClientPlayerEvents;
import com.igrium.replayfps.game.event.SetExperienceEvent;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameMode;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "setExperience", at = @At("RETURN"))
    void replayfps$onSetExperience(float progress, int total, int level, CallbackInfo ci) {
        SetExperienceEvent.EVENT.invoker().onSetExperience(progress, total, level, (PlayerEntity) (Object) this);
    }

    @Shadow
    private MinecraftClient client;

    @Inject(method = "onGameModeChanged", at = @At("HEAD"))
    void replayfps$gamemodeChanged(GameMode gameMode, CallbackInfo ci) {
        ClientPlayerEvents.SET_GAMEMODE.invoker().onSetGamemode((ClientPlayerEntity) (Object) this, client.interactionManager.getCurrentGameMode(), gameMode);
    }
}
