package com.igrium.replayfps.util;

public final class AnimationUtils {
    private AnimationUtils() {}

    /**
     * Count the number of frames that have elapsed in a given amount of time.
     * @param time Time in milliseconds.
     * @param frameInterval Milliseconds between frames.
     * @return Number of frames.
     */
    public static long countFrames(long time, long frameInterval) {
        return Math.floorDiv(time, frameInterval);
    }

    /**
     * Count the number of frames that have elapsed in a given amount of time.
     * @param time Time in milliseconds.
     * @param frameInterval Milliseconds between frames.
     * @return Number of frames.
     */
    public static int countFrames(long time, int frameInterval) {
        return (int) Math.floorDiv(time, frameInterval);
    }

    /**
     * Count the number of frames that have elapsed in a given amount of time.
     * 
     * @param time          Time in milliseconds.
     * @param framerate     Framerate numerator.
     * @param framerateBase Framerate denominator.
     * @return Number of frames.
     */
    public static int countFrames(long time, int framerate, int framerateBase) {
        // Technically the equation is (time / 1000) * (framerate / framerateBase), but
        // this form is equivalent and it avoids needing to use floats.
        return (int) ((time * framerate) / (framerateBase * 1000));
    }

    /**
     * Count the number of frames that have elapsed in a given amount of time.
     * 
     * @param time          Time in milliseconds.
     * @param framerate     Framerate numerator.
     * @param framerateBase Framerate denominator.
     * @return Number of frames.
     */
    public static int countFrames(int time, int framerate, int framerateBase) {
        // Technically the equation is (time / 1000) * (framerate / framerateBase), but
        // this form is equivalent and it avoids needing to use floats.
        return (time * framerate) / (framerateBase * 1000);
    }

    /**
     * Count the number of frames that have elapsed in a given amount of time.
     * @param time Time in milliseconds.
     * @param framerate The framerate.
     * @return Number of frames.
     */
    public static int countFrames(long time, float framerate) {
        return (int) ((time * framerate) / 1000);
    }

    /**
     * Count the number of frames that have elapsed in a given amount of time.
     * @param time Time in seconds.
     * @param framerate Framerate numerator.
     * @param framerateBase Framerate denominator.
     * @return
     */
    public static int countFrames(float time, int framerate, int framerateBase) {
        return (int) (time * framerate / framerateBase);
    }

    /**
     * Count the number of frames that have elapsed in a given amount of time.
     * @param time Time in seconds.
     * @param framerate Framerate.
     * @return Number of frames.
     */
    public static int countFrames(float time, float framerate) {
        return (int) (time * framerate);
    }

    /**
     * Calculate the amount of time it should take for a given amount of frames to run.
     * @param numFrames Number of frames.
     * @param framerate Framerate numerator.
     * @param framerateBase Framerate denominator.
     * @return Time in milliseconds.
     */
    public static long getDuration(int numFrames, int framerate, int framerateBase) {
        return (numFrames * framerateBase * 1000) / framerate;
    }

    /**
     * Calculate the amount of time it should take for a given amount of frames to run.
     * @param numFrames Number of frames.
     * @param framerate Framerate.
     * @return Time in milliseconds.
     */
    public static long getDuration(int numFrames, float framerate) {
        return (long) ((1000 * numFrames) / framerate);
    }

    /**
     * Calculate the amount of time it should take for a given amount of frames to run.
     * @param numFrames Number of frames.
     * @param framerate Framerate.
     * @return Time in seconds.
     */
    public static float getDurationSeconds(int numFrames, float framerate) {
        return numFrames / framerate;
    }
}
