package com.igrium.replayfps.playback;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import com.igrium.replayfps.events.ReplayEvents;
import com.igrium.replayfps.networking.CustomReplayPacketManager;
import com.igrium.replayfps.networking.FakePacketHandlers;
import com.igrium.replayfps.networking.PacketRedirectors;
import com.igrium.replayfps.networking.event.CustomPacketReceivedEvent;
import com.igrium.replayfps.networking.event.PacketReceivedEvent;
import com.igrium.replayfps.recording.ClientRecordingModule;
import com.igrium.replayfps.util.GlobalReplayContext;
import com.mojang.logging.LogUtils;
import com.replaymod.core.Module;
import com.replaymod.core.events.PreRenderCallback;
import com.replaymod.lib.de.johni0702.minecraft.gui.utils.EventRegistrations;
import com.replaymod.replay.ReplayHandler;
import com.replaymod.replay.events.ReplayClosingCallback;
import com.replaymod.replay.events.ReplayOpenedCallback;
import com.replaymod.replaystudio.replay.ReplayFile;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Identifier;

public class ClientPlaybackModule extends EventRegistrations implements Module {
    private static ClientPlaybackModule instance;

    private ReplayHandler currentReplay;
    private ClientCapPlayer currentPlayer;
    private CustomReplayPacketManager customPacketManager;

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

    public CustomReplayPacketManager getCustomPacketManager() {
        return customPacketManager;
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
        
        customPacketManager = new CustomReplayPacketManager();
        for (var h : FakePacketHandlers.REGISTRY.values()) {
            customPacketManager.registerReceiver(h.getId(), h);
        }
    }

    { on(ReplayEvents.REPLAY_SETUP, this::onReplaySetup); }
    private void onReplaySetup(ReplayHandler handler) {
        // Upon rewinding the replay, we need to clear any queued fake packets.
        if (customPacketManager != null) {
            customPacketManager.clearQueue();
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

        customPacketManager = null;
    }

    { on(PreRenderCallback.EVENT, this::tickRender); }
    private void tickRender() {
        if (currentPlayer == null || client.world == null || client.getCameraEntity() == null) return;

        int timestamp = currentReplay.getReplaySender().currentTimeStamp();
        ClientPlaybackContext context = genContext(timestamp);

        if (!Objects.equals(client.getCameraEntity(), context.localPlayer().orElse(null))) return;
        currentPlayer.tickPlayer(genContext(timestamp));
    }
    
    { ClientTickEvents.END_WORLD_TICK.register(this::tickClient); }
    private void tickClient(ClientWorld world) {
        if (currentPlayer == null) {
            GlobalReplayContext.ENTITY_POS_OVERRIDES.clear();
            return;
        }

        ClientPlaybackContext context = genContext(currentReplay.getReplaySender().currentTimeStamp());
        currentPlayer.tickClient(context);

        // If we're viewing from the camera, attempt to flush the packet queue.
        if (client.cameraEntity.equals(context.localPlayer().orElse(null)) && customPacketManager != null) {
            customPacketManager.flushQueue(client, context.localPlayer().get());
        }
    }
    
    { CustomPacketReceivedEvent.EVENT.register(this::onCustomPacketReceived); }
    private boolean onCustomPacketReceived(Identifier channel, PacketByteBuf payload) {
        if (customPacketManager != null) {
            return customPacketManager.onPacketReceived(channel, payload);
        }
        return false;
    }

    { PacketReceivedEvent.EVENT.register(this::onPacketReceived); }
    private boolean onPacketReceived(Packet<?> packet, PacketListener l) {
        if (currentPlayer == null || client.world == null) return false;
        PlayerEntity localPlayer = (PlayerEntity) client.world.getEntityById(currentPlayer.getReader().getHeader().getLocalPlayerID());
        if (localPlayer == null) return false;

        if (PacketRedirectors.REDIRECT_QUEUED.contains(packet)) {
            PacketRedirectors.applyRedirect(packet, localPlayer, client);
            return true;
        }
        return false;
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
