package com.igrium.replayfps.mixins_game;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.replayfps.game_events.SetExperienceEvent;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityListenersMixin {

    @Inject(method = "setExperience", at = @At("RETURN"))
    void replayfps$onSetExperience(float progress, int total, int level, CallbackInfo ci) {
        SetExperienceEvent.EVENT.invoker().onSetExperience(progress, total, level, (PlayerEntity) (Object) this);
    }
}
