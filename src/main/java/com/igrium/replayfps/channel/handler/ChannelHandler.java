package com.igrium.replayfps.channel.handler;

import java.io.DataInput;
import java.io.DataOutput;

import com.igrium.replayfps.channel.type.ChannelType;
import com.igrium.replayfps.playback.ClientPlaybackContext;
import com.igrium.replayfps.recording.ClientCaptureContext;

/**
 * Handles the application and capturing of a specific animation channel.
 * Handlers are <i>not<i> tied to any given recording instance; they are
 * registered globally.
 */
public interface ChannelHandler<T> {
    public ChannelType<T> getChannelType();

    public T capture(ClientCaptureContext context) throws Exception;

    public void apply(T val, ClientPlaybackContext context) throws Exception;

    public default Class<T> getType() {
        return getChannelType().getType();
    }

    public default boolean shouldInterpolate() {
        return false;
    }

    /**
     * If true, this channel applies every client tick instead of every frame.
     */
    public default boolean applyPerTick() {
        return false;
    }

    public static <T> void writeChannel(ClientCaptureContext context, DataOutput out, ChannelHandler<T> handler) throws Exception {
        T val = handler.capture(context);
        handler.getChannelType().write(out, val);
    }

    public static <T> void readChannel(DataInput in, ChannelHandler<T> handler, ClientPlaybackContext context) throws Exception {
        T val = handler.getChannelType().read(in);
        handler.apply(val, context);
    }

}
