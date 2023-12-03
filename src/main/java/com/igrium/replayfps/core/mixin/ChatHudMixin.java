package com.igrium.replayfps.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.replaymod.replay.FullReplaySender;
import com.replaymod.replay.ReplayHandler;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    
    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V",
            at = @At("HEAD"), cancellable = true)
    void replayfps$onAddMessage(Text message, MessageSignatureData signature, int ticks, MessageIndicator indicator, boolean refresh, CallbackInfo ci) {
        // Do not display chat messages if we're currently hurrying.
        // TODO: implement a system where the hud actually ticks properly in this situation rather than supressing chat.
        ReplayHandler handler = ClientPlaybackModule.getInstance().getCurrentReplay();
        if (handler != null && handler.getReplaySender() instanceof FullReplaySender sender) {
            if (sender.isHurrying()) ci.cancel();
        }
    }
}
