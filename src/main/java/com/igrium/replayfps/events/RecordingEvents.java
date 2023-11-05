package com.igrium.replayfps.events;

import com.replaymod.lib.de.johni0702.minecraft.gui.utils.Event;
import com.replaymod.recording.packet.PacketListener;
import com.replaymod.replaystudio.replay.ReplayFile;

public final class RecordingEvents {

    /**
     * Called after the Replay Mod starts recording.
     */
    public static final Event<StartedRecording> STARTED_RECORDING = Event.create((listeners) -> (packetListener, replayFile) -> {
        for (StartedRecording listener : listeners) {
            listener.onStartRecording(packetListener, replayFile);
        }
    });

    /**
     * Called when the Replay Mod stops recording but before it saves. Execution happens on the Save Service.
     */
    public static final Event<StopRecording> STOP_RECORDING = Event.create((listeners) -> (packetListener, replayFile) -> {
        for (StopRecording listener : listeners) {
            listener.onStopRecording(packetListener, replayFile);
        }
    });

    /**
     * Called after the Replay mod has stopped recording. Use to clean up any excess
     * resources. Note that the replay might still be saving.
     */
    public static final Event<StoppedRecording> STOPPED_RECORDING = Event.create(listeners -> () -> {
        for (StoppedRecording listener : listeners) {
            listener.onStoppedRecording();
        }
    });

    public interface StartedRecording {
        void onStartRecording(PacketListener packetListener, ReplayFile replayFile);
    }

    public interface StopRecording {
        void onStopRecording(PacketListener packetListener, ReplayFile replayFile);
    }

    public interface StoppedRecording {
        void onStoppedRecording();
    }
}
