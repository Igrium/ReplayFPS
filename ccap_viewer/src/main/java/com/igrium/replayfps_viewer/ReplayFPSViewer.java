package com.igrium.replayfps_viewer;

import com.igrium.craftfx.application.ApplicationManager;
import com.igrium.craftfx.application.ApplicationType;
import com.mojang.logging.LogUtils;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;

public class ReplayFPSViewer implements ClientModInitializer {

    public static final ApplicationType<ClientCapViewer> VIEWER = ApplicationType
            .register(new Identifier("replayfps_viewer"), new ApplicationType<>(ClientCapViewer::new));

    @Override
    public void onInitializeClient() {
        LogUtils.getLogger().info("Hello from the CCap viewer!");
    }
    
    public static void launchViewer() {
        try {
            ApplicationManager.getInstance().launch(VIEWER, null);
        } catch (Exception e) {
            LogUtils.getLogger().error("Error launching CraftFX.", e);
        }
    }
}
