package com.igrium.replayfps.events;

import java.util.function.Consumer;

import com.igrium.replayfps.channel.handler.ChannelHandler;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ChannelRegistrationCallback {
    Event<ChannelRegistrationCallback> EVENT = EventFactory.createArrayBacked(ChannelRegistrationCallback.class,
            listeners -> channels -> {
                for (var l : listeners) l.createChannels(channels);
            });

    void createChannels(Consumer<ChannelHandler<?>> channels);
}
