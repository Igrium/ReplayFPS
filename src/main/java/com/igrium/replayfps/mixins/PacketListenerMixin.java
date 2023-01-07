package com.igrium.replayfps.mixins;

import java.util.concurrent.ExecutorService;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.replayfps.events.RecordingEvents;
import com.replaymod.recording.packet.PacketListener;
import com.replaymod.replaystudio.replay.ReplayFile;

import io.netty.channel.ChannelHandlerContext;

@Mixin(PacketListener.class)
public class PacketListenerMixin {

    @Shadow(remap = false)
    private ReplayFile replayFile;

    @Shadow(remap = false)
    private ExecutorService saveService;

    @Inject(method = "channelInactive", at = @At("HEAD"), remap = false)
    void channelInactive(ChannelHandlerContext ctx, CallbackInfo ci) {
        saveService.submit(() -> {
            RecordingEvents.STOP_RECORDING.invoker().onStopRecording((PacketListener) (Object) this, replayFile);
        });
    }
}
