package com.igrium.replayfps.channel.handler;

import org.joml.Vector2f;
import org.joml.Vector2fc;

import com.igrium.replayfps.channel.type.ChannelType;
import com.igrium.replayfps.channel.type.ChannelTypes;
import com.igrium.replayfps.playback.ClientPlaybackContext;
import com.igrium.replayfps.recording.ClientCaptureContext;

import net.minecraft.client.network.ClientPlayerEntity;

public class PlayerRotChannelHandler implements ChannelHandler<Vector2fc> {

    @Override
    public ChannelType<Vector2fc> getChannelType() {
        return ChannelTypes.VECTOR2F;
    }

    @Override
    public Vector2fc capture(ClientCaptureContext context) {
        ClientPlayerEntity player = context.localPlayer();
        return new Vector2f(player.getPitch(), player.getYaw());
    }

    @Override
    public void apply(Vector2fc val, ClientPlaybackContext context) {
        context.localPlayer().ifPresent(player -> {
            player.setPitch(val.x());
            player.setYaw(val.y());

            player.prevPitch = val.x();
            player.prevYaw = val.y();

            // For some reason, yaw doesn't render properly if we don't do this.
            player.setHeadYaw(val.y());
            player.prevHeadYaw = val.y();
        });
    }
    
    @Override
    public boolean shouldInterpolate() {
        return true;
    }
}
