package com.igrium.replayfps.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bawnorton.mixinsquared.TargetHandler;
import com.igrium.replayfps.playback.ClientCapPlayer;
import com.igrium.replayfps.playback.ClientPlaybackModule;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;

@Mixin(value = InGameHud.class, priority = 1500)
public class SkipHudDuringRenderMixinSquared {
    @TargetHandler(
        mixin = "com.replaymod.render.mixin.Mixin_SkipHudDuringRender",
        name = "replayModRender_skipHudDuringRender"
    )
    @Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true)
    void replayfps$dontSkipHudDuringRender(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientCapPlayer currentPlayer = ClientPlaybackModule.getInstance().getCurrentPlayer();
        if (client.world == null || currentPlayer == null || client.cameraEntity == null) return;
        if (currentPlayer.getReader().getHeader().getLocalPlayerID() == client.cameraEntity.getId()) {
            ci.cancel();
        }
    }
}
