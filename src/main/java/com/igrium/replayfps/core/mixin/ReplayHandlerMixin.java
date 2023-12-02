package com.igrium.replayfps.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.replayfps.core.events.ReplayEvents;
import com.replaymod.replay.ReplayHandler;

@Mixin(ReplayHandler.class)
public class ReplayHandlerMixin {

    @Inject(method = "setup", at = @At("HEAD"), remap = false)
    private void replayfps$onSetup(CallbackInfo ci) {
        ReplayEvents.REPLAY_SETUP.invoker().onReplaySetup((ReplayHandler) (Object) this);
    }
}
