package com.igrium.replayfps_viewer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.replayfps_viewer.ReplayFPSViewer;
import com.mojang.logging.LogUtils;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init()V", at = @At("RETURN"))
    protected void replayfps$onInit(CallbackInfo ci) {
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Debug replays"), (button) -> {
            ReplayFPSViewer.launchViewer();
        }).build());
        LogUtils.getLogger().info("Hello from the title screen mixin!");
    }
    
}
