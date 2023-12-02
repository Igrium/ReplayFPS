package com.igrium.replayfps.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.replaymod.lib.de.johni0702.minecraft.gui.GuiRenderer;
import com.replaymod.lib.de.johni0702.minecraft.gui.RenderInfo;
import com.replaymod.lib.de.johni0702.minecraft.gui.utils.lwjgl.ReadableDimension;
import com.replaymod.render.hooks.EntityRendererHandler;
import com.replaymod.replay.gui.overlay.GuiReplayOverlay;

import net.minecraft.client.MinecraftClient;

@Mixin(GuiReplayOverlay.class)
public class SkipOverlayDuringRenderMixin {

    @Inject(method = "draw", at = @At("HEAD"), cancellable = true, remap = false)
    @SuppressWarnings("resource")
    void replayfps$dontDrawIfRendering(GuiRenderer renderer, ReadableDimension size, RenderInfo renderInfo,CallbackInfo ci) {
        if (((EntityRendererHandler.IEntityRenderer) MinecraftClient.getInstance().gameRenderer)
                .replayModRender_getHandler() != null) {
            ci.cancel();
        }
    }
}
