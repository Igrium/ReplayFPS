package com.igrium.replayfps.core.events;

import java.util.function.Consumer;

import com.igrium.replayfps.core.channel.ChannelHandler;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;


/**
 * Called when a client-capture starts recording and needs to decide which
 * channels it's going to capture.
 */
public interface ChannelRegistrationCallback {

    Event<ChannelRegistrationCallback> EVENT = EventFactory.createArrayBacked(ChannelRegistrationCallback.class,
            listeners -> channels -> {
                for (var l : listeners) l.createChannels(channels);
            });

    void createChannels(Consumer<ChannelHandler<?>> channels);
}
