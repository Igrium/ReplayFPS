package com.igrium.replayfps.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.igrium.replayfps.ReplayFPS;
import com.igrium.replayfps.core.util.PlaybackUtils;

@Mixin(targets = "com.replaymod.replay.camera.CameraEntity$EventHandler")
public class ShowHotbarDuringRenderMixin {

    @Inject(method = "shouldRenderHotbar", at = @At("HEAD"), remap = false, cancellable = true)
    void replayfps$forceShowHotbar(CallbackInfoReturnable<Boolean> cir) {
        if (PlaybackUtils.isViewingPlaybackPlayer() && ReplayFPS.getConfig().shouldDrawHotbar()) {
            cir.setReturnValue(true);
        }
    }
}
