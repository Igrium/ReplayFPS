package com.igrium.replayfps.recording;

import com.replaymod.core.Module;
import com.replaymod.core.ReplayMod;
import com.replaymod.lib.de.johni0702.minecraft.gui.utils.EventRegistrations;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientRecordingModule extends EventRegistrations implements Module {

    private static ClientRecordingModule instance;

    public static ClientRecordingModule getInstance() {
        return instance;
    }
    
    private final ReplayMod replayMod;

    public ClientRecordingModule(ReplayMod replayMod) {
        this.replayMod = replayMod;
    }

    public ReplayMod getReplayMod() {
        return replayMod;
    }

    @Override
    public void initCommon() {
        instance = this;
    }
}


