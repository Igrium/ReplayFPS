package com.igrium.replayfps.math;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import net.minecraft.util.math.Vec3d;

public final class Interpolators {
    private Interpolators() {
    };

    private static class Discrete<T> implements Interpolator<T> {

        @Override
        public T lerp(T a, T b, float delta) {
            return delta >= 1.0 ? b : a;
        }

    }

    public static final Interpolator<Object> DISCRETE = new Discrete<>();

    @SuppressWarnings("unchecked")
    public static <T> Interpolator<T> discrete() {
        return (Interpolator<T>) DISCRETE;
    }

    private static class FloatInterpolator implements Interpolator<Float> {

        @Override
        public Float lerp(Float a, Float b, float delta) {
            return delta * (b - a) + a;
        }

    }

    public static final Interpolator<Float> FLOAT = new FloatInterpolator();

    private static class DoubleInterpolator implements Interpolator<Double> {

        @Override
        public Double lerp(Double a, Double b, float delta) {
            return delta * (b - a) + a;
        }

    }

    public static final Interpolator<Double> DOUBLE = new DoubleInterpolator();

    private static class ShortInterpolator implements Interpolator<Short> {

        @Override
        public Short lerp(Short a, Short b, float delta) {
            return (short) (delta * (b - a) + a);
        }
    }

    public static final Interpolator<Short> SHORT = new ShortInterpolator();

    private static class IntInterpolator implements Interpolator<Integer> {

        @Override
        public Integer lerp(Integer a, Integer b, float delta) {
            return (int) (delta * (b - a) + a);
        }
        
    }

    public static final Interpolator<Integer> INTEGER = new IntInterpolator();

    private static class LongInterpolator implements Interpolator<Long> {

        @Override
        public Long lerp(Long a, Long b, float delta) {
            throw new UnsupportedOperationException("Unimplemented method 'lerp'");
        }
        
    }

    public static final Interpolator<Long> LONG = new LongInterpolator();

    private static class Vector3dInterpolator implements Interpolator<Vector3dc> {

        @Override
        public Vector3d lerp(Vector3dc a, Vector3dc b, float delta) {
            return a.lerp(b, delta, new Vector3d());
        }
        
    }

    public static final Interpolator<Vector3dc> VECTOR3D = new Vector3dInterpolator();

    private static class Vector3fInterpolator implements Interpolator<Vector3fc> {

        @Override
        public Vector3f lerp(Vector3fc a, Vector3fc b, float delta) {
            return a.lerp(b, delta, new Vector3f());
        }
        
    }

    public static final Interpolator<Vector3fc> VECTOR3F = new Vector3fInterpolator();

    private static class Vec3dInterpolator implements Interpolator<Vec3d> {

        @Override
        public Vec3d lerp(Vec3d a, Vec3d b, float delta) {
            return a.lerp(b, delta);
        }
        
    }

    public static final Interpolator<Vec3d> VEC3D = new Vec3dInterpolator();

    private static class QuaternionfInterpolator implements Interpolator<Quaternionfc> {

        @Override
        public Quaternionf lerp(Quaternionfc a, Quaternionfc b, float delta) {
            return a.slerp(a, delta, new Quaternionf());
        }
        
    }

    public static final Interpolator<Quaternionfc> QUATERNIONF = new QuaternionfInterpolator();
}
