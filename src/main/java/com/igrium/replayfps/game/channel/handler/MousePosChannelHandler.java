package com.igrium.replayfps.game.channel.handler;

import org.joml.Vector2f;
import org.joml.Vector2fc;

import com.igrium.replayfps.core.channel.ChannelHandler;
import com.igrium.replayfps.core.channel.type.ChannelType;
import com.igrium.replayfps.core.channel.type.ChannelTypes;
import com.igrium.replayfps.core.playback.ClientPlaybackContext;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.igrium.replayfps.core.recording.ClientCaptureContext;
import com.igrium.replayfps.core.screen.PlaybackScreenManager;

import net.minecraft.client.MinecraftClient;

public class MousePosChannelHandler implements ChannelHandler<Vector2fc> {

    @Override
    public ChannelType<Vector2fc> getChannelType() {
        return ChannelTypes.VECTOR2F;
    }

    @Override
    public Vector2fc capture(ClientCaptureContext context) throws Exception {
        MinecraftClient client = context.client();
        float x = (float) (client.mouse.getX() * client.getWindow().getScaledWidth() / client.getWindow().getWidth());
        float y = (float) (client.mouse.getY() * client.getWindow().getScaledHeight() / client.getWindow().getHeight());

        return new Vector2f(x, y);
    }

    @Override
    public void apply(Vector2fc val, ClientPlaybackContext context) throws Exception {
        ClientPlaybackModule module = ClientPlaybackModule.getInstance();
        PlaybackScreenManager screenManager = module.getPlaybackScreenManager();
        if (screenManager == null) return;

        screenManager.setMouseX(val.x());
        screenManager.setMouseY(val.y());
    }

    @Override
    public boolean shouldInterpolate() {
        return true;
    }
    
}
