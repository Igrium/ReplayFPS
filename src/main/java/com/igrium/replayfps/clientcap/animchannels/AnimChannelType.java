package com.igrium.replayfps.clientcap.animchannels;

import com.igrium.replayfps.clientcap.ClientCaptureContext;
import com.igrium.replayfps.clientcap.ClientPlaybackContext;
import com.igrium.replayfps.clientcap.channeltype.ChannelType;

/**
 * <p>A single channel within the ClientCap.</p>
 * 
 * <p>In contrast to {@link ChannelType}, this manages interaction with the game
 * during recording and playback rather than serialization.</p>
 */
/**
 * A single channel within the ClientCap that knows how to interact with the game.
 */
public interface AnimChannelType<T> extends ChannelType<T> {

    /**
     * Capture a frame right now.
     * @param context The capture context.
     * @return The frame data.
     */
    T capture(ClientCaptureContext context);

    /**
     * Apply a frame during playback.
     * @param frame The frame data.
     * @param context The playback context.
     */
    void apply(T frame, ClientPlaybackContext context);

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
