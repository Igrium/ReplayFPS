package com.igrium.replayfps.clientcap.animchannels;

import org.joml.Vector2f;
import org.joml.Vector2fc;

import com.igrium.replayfps.clientcap.ClientCaptureContext;
import com.igrium.replayfps.clientcap.ClientPlaybackContext;
import com.igrium.replayfps.clientcap.channeltype.Vec2fChannelType;

import net.minecraft.client.network.AbstractClientPlayerEntity;

public class PlayerRotChannelType extends Vec2fChannelType implements AnimChannelType<Vector2fc> {

    @Override
    public Vector2f capture(ClientCaptureContext context) {
        return new Vector2f(
            context.localPlayer().getYaw(context.tickDelta()),
            context.localPlayer().getPitch(context.tickDelta())
        );
    }

    @Override
    public void apply(Vector2fc frame, ClientPlaybackContext context) {
        if (context.localPlayer().isEmpty()) return;
        AbstractClientPlayerEntity player = context.localPlayer().get();
        player.setPitch(frame.y());
        player.prevPitch = frame.y();
        player.setYaw(frame.x());
        player.prevYaw = frame.x();
    }

    @Override
    public Class<Vector2fc> getChannelClass() {
        return Vector2fc.class;
    }
    
}
