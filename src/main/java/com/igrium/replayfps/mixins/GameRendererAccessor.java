package com.igrium.replayfps.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;

@Mixin(GameRenderer.class)
@Deprecated
public interface GameRendererAccessor {

    @Invoker("getFov")
    @SuppressWarnings("visibility")
    public double getFov(Camera camera, float tickDelta, boolean changingFov);
}
