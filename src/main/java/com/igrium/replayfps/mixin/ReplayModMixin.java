package com.igrium.replayfps.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.replayfps.util.ReplayModHooks;
import com.replaymod.core.ReplayMod;

@Mixin(ReplayMod.class)
public class ReplayModMixin {
    @Inject(method = "initModules", at = @At("RETURN"), remap = false)
    void afterInit(CallbackInfo ci) {
        ReplayModHooks.waitForInit().complete((ReplayMod) (Object) this);
    }
}
