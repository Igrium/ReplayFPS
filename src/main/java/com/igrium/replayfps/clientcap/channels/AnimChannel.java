package com.igrium.replayfps.clientcap.channels;

import com.igrium.replayfps.clientcap.ClientCaptureContext;
import com.igrium.replayfps.clientcap.channeltype.ChannelType;

/**
 * <p>A single channel within the ClientCap.</p>
 * 
 * <p>In contrast to {@link ChannelType}, this manages interaction with the game
 * during recording and playback rather than serialization.</p>
 */
public interface AnimChannel<T> {

    /**
     * Capture a frame right now.
     * @param context The capture context.
     * @return The frame data.
     */
    T capture(ClientCaptureContext context);

    /**
     * Apply a frame during playback.
     * @param frame Frame to apply.
     */
    void apply(T frame);

    ChannelType<T> getChannelType();

    /**
     * Get the class of the objects managed by this channel.
     */
    Class<T> getChannelClass();

    @SuppressWarnings("unchecked")
    default T cast(Object val) {
        if (!getChannelClass().isAssignableFrom(val.getClass())) {
            throw new ClassCastException();
        }
        return (T) val;
    }
}
