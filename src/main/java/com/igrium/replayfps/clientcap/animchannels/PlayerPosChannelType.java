package com.igrium.replayfps.clientcap.animchannels;

import com.igrium.replayfps.clientcap.ClientCaptureContext;
import com.igrium.replayfps.clientcap.ClientPlaybackContext;
import com.igrium.replayfps.clientcap.channeltype.PositionChannelType;

import net.minecraft.util.math.Vec3d;

public class PlayerPosChannelType extends PositionChannelType implements AnimChannelType<Vec3d> {

    @Override
    public Vec3d capture(ClientCaptureContext context) {
        return context.localPlayer().getPos();
    }

    @Override
    public void apply(Vec3d frame, ClientPlaybackContext context) {
        if (context.localPlayer().isEmpty()) return;
        context.localPlayer().get().setPosition(frame);
    }

}
