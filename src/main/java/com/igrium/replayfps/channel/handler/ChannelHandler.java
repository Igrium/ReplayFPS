package com.igrium.replayfps.channel.handler;

import java.io.IOException;
import java.io.OutputStream;

import com.igrium.replayfps.channel.type.ChannelType;
import com.igrium.replayfps.math.Interpolator;
import com.igrium.replayfps.math.Interpolators;

/**
 * Handles the application and capturing of a specific animation channel.
 * Handlers are <i>not<i> tied to any given recording instance; they are
 * registered globally.
 */
public interface ChannelHandler<T> {
    public ChannelType<T> getChannelType();

    public T capture();

    public void apply(T val);

    public default Class<T> getType() {
        return getChannelType().getType();
    }

    public default Interpolator<T> getInterpolator() {
        return Interpolators.discrete();
    }

    public static <T> void writeChannel(OutputStream out, ChannelHandler<T> handler) throws IOException {
        T val = handler.capture();
        handler.getChannelType().write(out, val);
    }
}
