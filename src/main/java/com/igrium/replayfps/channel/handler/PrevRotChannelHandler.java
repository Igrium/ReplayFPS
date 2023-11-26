package com.igrium.replayfps.channel.handler;

import org.joml.Vector2f;
import org.joml.Vector2fc;

import com.igrium.replayfps.channel.type.ChannelType;
import com.igrium.replayfps.channel.type.ChannelTypes;
import com.igrium.replayfps.playback.ClientPlaybackContext;
import com.igrium.replayfps.recording.ClientCaptureContext;

public class PrevRotChannelHandler implements ChannelHandler<Vector2fc> {

    @Override
    public ChannelType<Vector2fc> getChannelType() {
        return ChannelTypes.VECTOR2F;
    }

    @Override
    public Vector2fc capture(ClientCaptureContext context) throws Exception {
        float x = context.localPlayer().prevPitch;
        float y = context.localPlayer().prevYaw;

        return new Vector2f(x, y);
    }

    @Override
    public void apply(Vector2fc val, ClientPlaybackContext context) throws Exception {
        context.localPlayer().ifPresent(player -> {
            player.prevPitch = val.x();
            player.prevYaw = val.y();
        });
    }
    
}
