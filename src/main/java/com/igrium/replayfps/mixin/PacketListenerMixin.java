package com.igrium.replayfps.mixin;

import java.util.concurrent.ExecutorService;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.replayfps.events.RecordingEvents;
import com.igrium.replayfps.util.TimecodeProvider;
import com.replaymod.recording.packet.PacketListener;
import com.replaymod.replaystudio.replay.ReplayFile;

import io.netty.channel.ChannelHandlerContext;

@Mixin(PacketListener.class)
public class PacketListenerMixin implements TimecodeProvider {

    @Shadow(remap = false)
    private ReplayFile replayFile;

    @Shadow(remap = false)
    private ExecutorService saveService;

    @Shadow(remap = false)
    private long startTime;

    @Shadow(remap = false)
    private long timePassedWhilePaused;

    @Shadow(remap = false)
    private volatile boolean serverWasPaused;

    @Inject(method = "channelInactive", at = @At("HEAD"), remap = false)
    void channelInactive(ChannelHandlerContext ctx, CallbackInfo ci) {
        saveService.submit(() -> {
            RecordingEvents.STOP_RECORDING.invoker().onStopRecording((PacketListener) (Object) this, replayFile);
        });
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public long getTimePassedWhilePaused() {
        return timePassedWhilePaused;
    }

    @Override
    public boolean getServerWasPaused() {
        return serverWasPaused;
    }
}
