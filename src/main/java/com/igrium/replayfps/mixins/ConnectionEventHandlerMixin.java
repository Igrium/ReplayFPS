package com.igrium.replayfps.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.replayfps.events.RecordingEvents;
import com.replaymod.recording.handler.ConnectionEventHandler;
import com.replaymod.recording.packet.PacketListener;

@Mixin(ConnectionEventHandler.class)
public class ConnectionEventHandlerMixin {

    @Shadow(remap = false)
    private PacketListener packetListener;
    
    @Inject(method = "onConnectedToServerEvent", at = @At(value = "NEW", target = "Lcom/replaymod/recording/gui/GuiRecordingControls"), remap = false)
    void finishReplaySetup(CallbackInfo ci) {
        RecordingEvents.STARTED_RECORDING.invoker().onStartRecording(packetListener, ((PacketListenerAccessor) packetListener).getReplayFile());
    }

    @Inject(method = "reset", at = @At("HEAD"), remap = false)
    void reset(CallbackInfo ci) {
        // This is easier than trying to target the middle of the real if statement.
        if (packetListener != null) {
            RecordingEvents.STOPPED_RECORDING.invoker().onStoppedRecording();
        }
    }
}
