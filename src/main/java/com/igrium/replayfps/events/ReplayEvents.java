package com.igrium.replayfps.events;

import com.replaymod.lib.de.johni0702.minecraft.gui.utils.Event;
import com.replaymod.replay.ReplayHandler;

public class ReplayEvents {
    public static final Event<ReplaySetup> REPLAY_SETUP = Event.create(
        listeners -> handler -> {
            for (var l : listeners) {
                l.onReplaySetup(handler);
            }
        }
    );

    /**
     * Called before the replay is loaded for the first time <em>and</em> every time
     * it is restarted due to backwards seeking.
     */
    public static interface ReplaySetup {
        void onReplaySetup(ReplayHandler handler);
    }
}
