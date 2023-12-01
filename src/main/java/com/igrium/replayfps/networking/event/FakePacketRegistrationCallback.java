package com.igrium.replayfps.networking.event;

import java.util.function.Consumer;

import com.igrium.replayfps.networking.FakePacketHandler;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface FakePacketRegistrationCallback {

    public static final Event<FakePacketRegistrationCallback> EVENT = EventFactory.createArrayBacked(
        FakePacketRegistrationCallback.class, listeners -> consumer -> {
            for (var l : listeners) {
                l.register(consumer);
            }
        });

    void register(Consumer<FakePacketHandler<?>> consumer);
}