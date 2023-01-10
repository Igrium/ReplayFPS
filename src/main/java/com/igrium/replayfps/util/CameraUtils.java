package com.igrium.replayfps.util;

import org.joml.Quaternionfc;

import com.igrium.replayfps.mixins.CameraAccessor;

import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;

/**
 * Utility class to access private members of {@link Camera}
 */
public final class CameraUtils {
    private CameraUtils() {}

    public static void setPos(Camera camera, Vec3d pos) {
        ((CameraAccessor) camera).setPosInvk(pos);
    }

    @SuppressWarnings("deprecation")
    public static void setRotation(Camera camera, Quaternionfc rotation) {
        CameraAccessor accessor = (CameraAccessor) camera;
        accessor.getRotation().set(rotation);
        accessor.getHorizontalPlane().set(0, 0, 1).rotate(rotation);
        accessor.getVerticalPlane().set(0, 1, 0).rotate(rotation);
        accessor.getDiagonalPlane().set(1, 0, 0).rotate(rotation);
    }
}
