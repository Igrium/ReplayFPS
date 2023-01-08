package com.igrium.replayfps.clientcap.animchannels;

import com.igrium.replayfps.clientcap.ClientCaptureContext;
import com.igrium.replayfps.clientcap.ClientPlaybackContext;
import com.igrium.replayfps.clientcap.channeltype.PositionChannelType;

import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;

public class CameraPosChannelType extends PositionChannelType implements AnimChannelType<Position> {

    @Override
    public Vec3d capture(ClientCaptureContext context) {
        return context.camera().getPos();
    }

    @Override
    public void apply(Position frame, ClientPlaybackContext context) {
        
    }
    
    @Override
    public Class<Position> getChannelClass() {
        return Position.class;
    }
}
