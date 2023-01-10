package com.igrium.replayfps.playback;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;

import net.minecraft.util.math.Vec3d;

/**
 * A container for various Camera values that need to be applied later in rendering.
 */
public class VirtualCamera {
    private Vec3d position = new Vec3d(0, 0, 0);

    public void setPosition(Vec3d position) {
        this.position = position;
    }

    public Vec3d getPosition() {
        return position;
    }

    private Quaternionfc rotation = new Quaternionf();

    public void setRotation(Quaternionfc rotation) {
        this.rotation = rotation;
    }

    public Quaternionfc getRotation() {
        return rotation;
    }

    private double fov;

    public double getFov() {
        return fov;
    }

    public void setFov(double fov) {
        this.fov = fov;
    }
}
