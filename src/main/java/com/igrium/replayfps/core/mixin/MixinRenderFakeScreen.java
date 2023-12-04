package com.igrium.replayfps.core.mixin;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.igrium.replayfps.core.events.CustomScreenRenderCallback;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(GameRenderer.class)
public class MixinRenderFakeScreen {

    @Shadow
    private MinecraftClient client;

    @Inject(method = "render", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;",
        ordinal = 0),
        locals = LocalCapture.CAPTURE_FAILHARD)
    void replayfps$beforeOverlay(float tickDelta, long startTime, boolean tick, CallbackInfo ci, int mouseX, int mouseY,
            Window window, Matrix4f matrix4f, MatrixStack matrixStack, DrawContext drawContext) {

        CustomScreenRenderCallback.EVENT.invoker().onRenderCustomScreen(
                (GameRenderer) (Object) this, drawContext, mouseX, mouseY, client.getLastFrameDuration());
    }
}
