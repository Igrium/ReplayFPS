package com.igrium.replayfps.channel.handler;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.igrium.replayfps.channel.type.ChannelType;
import com.igrium.replayfps.math.Interpolator;
import com.igrium.replayfps.math.Interpolators;
import com.igrium.replayfps.playback.ClientPlaybackContext;
import com.igrium.replayfps.recording.ClientCaptureContext;

/**
 * Handles the application and capturing of a specific animation channel.
 * Handlers are <i>not<i> tied to any given recording instance; they are
 * registered globally.
 */
public interface ChannelHandler<T> {
    public ChannelType<T> getChannelType();

    public T capture(ClientCaptureContext context);

    public void apply(T val, ClientPlaybackContext context);

    public default Class<T> getType() {
        return getChannelType().getType();
    }

    public default Interpolator<T> getInterpolator() {
        return Interpolators.discrete();
    }

    public static <T> void writeChannel(ClientCaptureContext context, DataOutput out, ChannelHandler<T> handler) throws IOException {
        T val = handler.capture(context);
        handler.getChannelType().write(out, val);
    }

    public static <T> void readChannel(DataInput in, ChannelHandler<T> handler, ClientPlaybackContext context) throws Exception {
        T val = handler.getChannelType().read(in);
        handler.apply(val, context);
    }
}
