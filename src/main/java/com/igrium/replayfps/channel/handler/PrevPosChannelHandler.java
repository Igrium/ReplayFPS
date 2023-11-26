package com.igrium.replayfps.channel.handler;

import com.igrium.replayfps.channel.type.ChannelType;
import com.igrium.replayfps.channel.type.ChannelTypes;
import com.igrium.replayfps.playback.ClientPlaybackContext;
import com.igrium.replayfps.recording.ClientCaptureContext;

import net.minecraft.util.math.Vec3d;

public class PrevPosChannelHandler implements ChannelHandler<Vec3d> {

    @Override
    public ChannelType<Vec3d> getChannelType() {
        return ChannelTypes.VEC3D;
    }

    @Override
    public Vec3d capture(ClientCaptureContext context) throws Exception {
        double x = context.localPlayer().prevX;
        double y = context.localPlayer().prevY;
        double z = context.localPlayer().prevZ;

        return new Vec3d(x, y, z);
    }

    @Override
    public void apply(Vec3d val, ClientPlaybackContext context) throws Exception {
        context.localPlayer().ifPresent(player -> {
            player.prevX = val.x;
            player.prevY = val.y;
            player.prevZ = val.z;
        });
    }
    
}
