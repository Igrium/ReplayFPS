package com.igrium.replayfps.clientcap;

import com.replaymod.core.Module;
import com.replaymod.lib.de.johni0702.minecraft.gui.utils.EventRegistrations;

public class ClientCapModule extends EventRegistrations implements Module {
    private static ClientCapModule instance;

    public static ClientCapModule getInstance() {
        return instance;
    }
    
    @Override
    public void initCommon() {
        instance = this;
    }
}
