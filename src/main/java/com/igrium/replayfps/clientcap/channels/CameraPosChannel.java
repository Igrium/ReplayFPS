package com.igrium.replayfps.clientcap.channels;

import com.igrium.replayfps.clientcap.ClientCaptureContext;
import com.igrium.replayfps.clientcap.channeltype.ChannelTypes;
import com.igrium.replayfps.clientcap.channeltype.PositionChannelType;

import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;

public class CameraPosChannel implements AnimChannel<Position> {

    @Override
    public Vec3d capture(ClientCaptureContext context) {
        return context.camera().getPos();
    }

    @Override
    public void apply(Position frame) {
        
    }

    @Override
    public PositionChannelType getChannelType() {
        return ChannelTypes.POSITION;
    }
    
    @Override
    public Class<Position> getChannelClass() {
        return Position.class;
    }
}
