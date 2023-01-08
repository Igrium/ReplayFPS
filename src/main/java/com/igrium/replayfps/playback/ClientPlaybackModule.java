package com.igrium.replayfps.playback;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;

import com.igrium.replayfps.recording.ClientRecordingModule;
import com.replaymod.core.Module;
import com.replaymod.lib.de.johni0702.minecraft.gui.utils.EventRegistrations;
import com.replaymod.replay.ReplayHandler;
import com.replaymod.replay.events.ReplayClosingCallback;
import com.replaymod.replay.events.ReplayOpenedCallback;
import com.replaymod.replaystudio.lib.guava.base.Optional;
import com.replaymod.replaystudio.replay.ReplayFile;

public class ClientPlaybackModule extends EventRegistrations implements Module {
    private static ClientPlaybackModule instance;

    private ReplayHandler currentReplay;
    private ClientCapPlayer currentPlayer;

    public static ClientPlaybackModule getInstance() {
        return instance;
    }

    @Override
    public void initCommon() {
        instance = this;
    }

    public ReplayHandler getCurrentReplay() {
        return currentReplay;
    }

    public ClientCapPlayer getCurrentPlayer() {
        return currentPlayer;
    }

    private InputStream openStream(ReplayFile file) throws IOException {
        Optional<InputStream> stream = file.get(ClientRecordingModule.ENTRY);
        if (stream.isPresent()) {
            return stream.get();
        } else {
            throw new FileNotFoundException(ClientRecordingModule.ENTRY);
        }
    }

    { on(ReplayOpenedCallback.EVENT, this::onReplayOpened); }
    private void onReplayOpened(ReplayHandler handler) throws IOException {
        currentReplay = handler;
        ReplayFile file = handler.getReplayFile();
        Optional<InputStream> dummyStream = Optional.absent();
        try {
            dummyStream = file.get(ClientRecordingModule.ENTRY);
        } catch (IOException e) {
            LogManager.getLogger().error("Error loading client capture: ", e);
        }
        if (dummyStream.isPresent()) {
            dummyStream.get().close();
            currentPlayer = new ClientCapPlayer(() -> openStream(file));
            currentPlayer.beginPlayback();
            currentPlayer.precache(0, 20);
        }
    }

    { on(ReplayClosingCallback.EVENT, this::onReplayClosing); }
    private void onReplayClosing(ReplayHandler handler) {
        if (this.currentReplay != handler) return;
        currentReplay = null;
        if (currentPlayer != null) {
            try {
                currentPlayer.close();
            } catch (IOException e) {
                LogManager.getLogger().error("Error closing ClientCap player:", e);
            }
            currentPlayer = null;
        }
    }
}
