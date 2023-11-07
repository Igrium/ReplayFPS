package com.igrium.replayfps.playback;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.igrium.replayfps.recording.ClientRecordingModule;
import com.mojang.logging.LogUtils;
import com.replaymod.core.Module;
import com.replaymod.core.events.PreRenderCallback;
import com.replaymod.lib.de.johni0702.minecraft.gui.utils.EventRegistrations;
import com.replaymod.replay.ReplayHandler;
import com.replaymod.replay.events.ReplayClosingCallback;
import com.replaymod.replay.events.ReplayOpenedCallback;
import com.replaymod.replaystudio.replay.ReplayFile;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

public class ClientPlaybackModule extends EventRegistrations implements Module {
    private static ClientPlaybackModule instance;

    private ReplayHandler currentReplay;
    private ClientCapPlayer currentPlayer;

    private final MinecraftClient client = MinecraftClient.getInstance();

    public static ClientPlaybackModule getInstance() {
        return instance;
    }

    @Override
    public void initCommon() {
        instance = this;
    }

    public ReplayHandler getCurrentReplay() {
        return currentReplay;
    }

    public ClientCapPlayer getCurrentPlayer() {
        return currentPlayer;
    }

    { on(ReplayOpenedCallback.EVENT, this::onReplayOpened); }
    private void onReplayOpened(ReplayHandler handler) {
        currentReplay = handler;
        ReplayFile file = handler.getReplayFile();

        try {
            var opt = file.get(ClientRecordingModule.ENTRY);
            if (!opt.isPresent()) return;

            InputStream stream = new BufferedInputStream(opt.get());
            currentPlayer = new ClientCapPlayer(new ClientCapReader(opt.get()));

            stream.close();
        } catch (IOException e) {
            LogUtils.getLogger().error("Error loading client capture.", e);
        }
    }

    { on(ReplayClosingCallback.EVENT, this::onReplayClosing); }
    private void onReplayClosing(ReplayHandler handler) {
        if (this.currentReplay != handler) {
            LogUtils.getLogger().warn(
                    "Client playback module had the wrong replay when close event was fired. This should not happen.");
            return;
        }

        currentReplay = null;
        if (currentPlayer != null) {
            try {
                currentPlayer.close();
            } catch (IOException e) {
                LogUtils.getLogger().error("Error closing client capture.", e);
            }
            currentPlayer = null;
        }
    }

    { on(PreRenderCallback.EVENT, this::tick); }
    private void tick() {
        if (currentPlayer == null || client.world == null || client.getCameraEntity() == null) return;

        int timestamp = currentReplay.getReplaySender().currentTimeStamp();
        currentPlayer.tickPlayer(genContext(timestamp));
    }

    private ClientPlaybackContext genContext(int timestamp) {
        return new ClientPlaybackContextImpl(client, currentReplay, timestamp, currentPlayer.getReader().getHeader().getLocalPlayerID());
    }

    private static class ClientPlaybackContextImpl implements ClientPlaybackContext {

        final MinecraftClient client;
        final ReplayHandler handler;
        final int timestamp;
        final AbstractClientPlayerEntity localPlayer;

        public ClientPlaybackContextImpl(MinecraftClient client, ReplayHandler handler, int timestamp,
                int localPlayerId) {
            this.client = client;
            this.handler = handler;
            this.timestamp = timestamp;
            if (client.world != null) {
                localPlayer = (AbstractClientPlayerEntity) client.world.getEntityById(localPlayerId);
            } else {
                localPlayer = null;
            }
        }

        @Override
        public MinecraftClient client() {
            return client;
        }

        @Override
        public ReplayHandler replayHandler() {
            return handler;
        }

        @Override
        public java.util.Optional<Entity> cameraEntity() {
            return java.util.Optional.ofNullable(client.cameraEntity);
        }

        @Override
        public java.util.Optional<AbstractClientPlayerEntity> localPlayer() {
            return java.util.Optional.ofNullable(localPlayer);
        }

        @Override
        public Camera camera() {
            return client.gameRenderer.getCamera();
        }

        @Override
        public int timestamp() {
            return timestamp;
        }

        @Override
        public java.util.Optional<ClientWorld> world() {
            return java.util.Optional.ofNullable(client.world);
        }
        
    }
}
