package com.igrium.replayfps_viewer;

import com.mojang.logging.LogUtils;

import net.fabricmc.api.ClientModInitializer;

public class ReplayFPSViewer implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        LogUtils.getLogger().info("Hello from the CCap viewer!");
    }
    
}
