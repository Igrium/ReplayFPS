package com.igrium.replayfps.channel.handler;

import com.igrium.replayfps.channel.type.ChannelType;
import com.igrium.replayfps.channel.type.ChannelTypes;
import com.igrium.replayfps.playback.ClientPlaybackContext;
import com.igrium.replayfps.recording.ClientCaptureContext;

public class HorizontalSpeedHandler implements ChannelHandler<Float> {

    @Override
    public ChannelType<Float> getChannelType() {
        return ChannelTypes.FLOAT;
    }

    @Override
    public Float capture(ClientCaptureContext context) throws Exception {
        return context.localPlayer().horizontalSpeed;
    }

    @Override
    public void apply(Float val, ClientPlaybackContext context) throws Exception {
        context.localPlayer().ifPresent(player -> {
            player.prevHorizontalSpeed = player.horizontalSpeed;
            player.horizontalSpeed = val;
        });
    }
    
    @Override
    public boolean applyPerTick() {
        return true;
    }
}
