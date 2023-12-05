package com.igrium.replayfps.game.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.text.Text;

@Mixin(AbstractSignEditScreen.class)
public abstract class AbstractSignEditScreenMixin extends Screen {


    protected AbstractSignEditScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "finishEditing", at = @At("HEAD"), cancellable = true)
    private void replayfps$onFinishEditing(CallbackInfo ci) {
        // Insert null check so that it doesn't crash during replay playback.
        if (client == null) ci.cancel();
    }
}
