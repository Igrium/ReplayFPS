package com.igrium.replayfps.game.channel.handler;

import com.igrium.replayfps.core.channel.ChannelHandler;
import com.igrium.replayfps.core.channel.type.ChannelType;
import com.igrium.replayfps.core.channel.type.ChannelTypes;
import com.igrium.replayfps.core.playback.ClientPlaybackContext;
import com.igrium.replayfps.core.recording.ClientCaptureContext;

import net.minecraft.util.math.Vec3d;

public class PlayerVelocityChannelHandler implements ChannelHandler<Vec3d> {

    @Override
    public ChannelType<Vec3d> getChannelType() {
        return ChannelTypes.VEC3D;
    }

    @Override
    public Vec3d capture(ClientCaptureContext context) throws Exception {
        return context.localPlayer().getVelocity();
    }

    @Override
    public void apply(Vec3d val, ClientPlaybackContext context) throws Exception {
        context.localPlayer().ifPresent(player -> player.setVelocity(val));
    }
    
    @Override
    public boolean shouldInterpolate() {
        return true;
    }
}
