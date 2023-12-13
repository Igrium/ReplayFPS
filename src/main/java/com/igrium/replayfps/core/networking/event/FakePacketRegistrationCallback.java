package com.igrium.replayfps.core.networking.event;

import com.igrium.replayfps.core.networking.FakePacketManager;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface FakePacketRegistrationCallback {

    public static final Event<FakePacketRegistrationCallback> EVENT = EventFactory.createArrayBacked(
        FakePacketRegistrationCallback.class, listeners -> manager -> {
            for (var l : listeners) {
                l.register(manager);
            }
        });

    void register(FakePacketManager manager);
}
