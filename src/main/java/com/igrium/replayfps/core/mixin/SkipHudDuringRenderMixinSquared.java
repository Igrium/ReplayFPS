package com.igrium.replayfps.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bawnorton.mixinsquared.TargetHandler;
import com.igrium.replayfps.core.util.PlaybackUtils;

import net.minecraft.client.gui.hud.InGameHud;

@Mixin(value = InGameHud.class, priority = 1500)
public class SkipHudDuringRenderMixinSquared {
    @TargetHandler(
        mixin = "com.replaymod.render.mixin.Mixin_SkipHudDuringRender",
        name = "replayModRender_skipHudDuringRender"
    )
    @Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true)
    void replayfps$dontSkipHudDuringRender(CallbackInfo ci) {
        if (PlaybackUtils.isViewingPlaybackPlayer()) {
            ci.cancel();
        }
    }
}
