package com.igrium.replayfps;

import net.fabricmc.api.ModInitializer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igrium.replayfps.playback.ClientPlaybackModule;
import com.igrium.replayfps.recording.ClientRecordingModule;
import com.igrium.replayfps.util.ReplayModHooks;

public class ReplayFPS implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "replayfps";
    public static final String MOD_NAME = "Replay FPS";

    private static ReplayFPS instance;

    public static ReplayFPS getInstance() {
        return instance;
    }

    private ClientRecordingModule clientRecordingModule;
    private ClientPlaybackModule clientPlaybackModule;

    public ClientRecordingModule getClientRecordingModule() {
        return clientRecordingModule;
    }

    @Override
    public void onInitialize() {
        instance = this;

        ReplayModHooks.onReplayModInit(mod -> {
            clientRecordingModule = new ClientRecordingModule(mod);
            clientRecordingModule.initCommon();
            clientRecordingModule.initClient();
            clientRecordingModule.register();

            clientPlaybackModule = new ClientPlaybackModule();
            clientPlaybackModule.initCommon();
            clientPlaybackModule.initClient();
            clientPlaybackModule.register();
        });
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }

}