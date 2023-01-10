package com.igrium.replayfps.clientcap.animchannels;

import com.igrium.replayfps.clientcap.ClientCaptureContext;
import com.igrium.replayfps.clientcap.ClientPlaybackContext;
import com.igrium.replayfps.clientcap.channeltype.PositionChannelType;

import net.minecraft.util.math.Position;

public class PlayerPosChannelType extends PositionChannelType implements AnimChannelType<Position> {

    @Override
    @SuppressWarnings("resource") // This should NOT be necessary.
    public Position capture(ClientCaptureContext context) {
        return context.client().player.getPos();
    }

    @Override
    public void apply(Position frame, ClientPlaybackContext context) {
        if (context.localPlayer().isEmpty()) return;
        context.localPlayer().get().setPos(frame.getX(), frame.getY(), frame.getZ());
    }

    @Override
    public Class<Position> getChannelClass() {
        return Position.class;
    }
    
}
