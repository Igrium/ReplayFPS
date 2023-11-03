package com.igrium.replayfps.channel_handler;

import com.igrium.replayfps.channel.ChannelType;

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
}
