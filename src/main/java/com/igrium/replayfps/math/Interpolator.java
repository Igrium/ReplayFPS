package com.igrium.replayfps.math;

/**
 * Determines how two values interpolate between each other.
 */
public interface Interpolator<T> {

    /**
     * Interpolate between two values.
     * @param a Value to interpolate from.
     * @param b Value to interpolate to.
     * @param delta A value from 0 - 1 indicating the progress of the interpolation.
     * @return The interpolated value.
     */
    public T lerp(T a, T b, float delta);
}
