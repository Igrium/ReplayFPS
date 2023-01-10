package com.igrium.replayfps.clientcap.animchannels;

import com.igrium.replayfps.clientcap.ClientCaptureContext;
import com.igrium.replayfps.clientcap.ClientPlaybackContext;
import com.igrium.replayfps.clientcap.channeltype.PositionChannelType;
import com.igrium.replayfps.util.CameraUtils;

import net.minecraft.util.math.Vec3d;

public class CameraPosChannelType extends PositionChannelType implements AnimChannelType<Vec3d> {

    @Override
    public Vec3d capture(ClientCaptureContext context) {
        return context.camera().getPos();
    }

    @Override
    public void apply(Vec3d frame, ClientPlaybackContext context) {
        CameraUtils.setPos(context.camera(), frame);
    }
    
    @Override
    public Class<Vec3d> getChannelClass() {
        return Vec3d.class;
    }
}
