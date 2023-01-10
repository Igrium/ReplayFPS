package com.igrium.replayfps.mixins;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;

@Mixin(Camera.class)
public interface CameraAccessor {

    @Invoker("setPos")
    void setPosInvk(Vec3d pos);

    @Deprecated
    @Accessor
    Quaternionf getRotation();

    @Accessor
    Vector3f getHorizontalPlane();

    @Accessor 
    Vector3f getVerticalPlane();

    @Accessor
    Vector3f getDiagonalPlane();
}
