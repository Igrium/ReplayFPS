package com.igrium.replayfps.recording;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.igrium.replayfps.channel.handler.ChannelHandler;
import com.igrium.replayfps.channel.handler.ChannelHandlers;
import com.igrium.replayfps.events.ChannelRegistrationCallback;
import com.igrium.replayfps.events.RecordingEvents;
import com.mojang.logging.LogUtils;
import com.replaymod.core.Module;
import com.replaymod.core.ReplayMod;
import com.replaymod.core.events.PreRenderCallback;
import com.replaymod.lib.de.johni0702.minecraft.gui.utils.EventRegistrations;
import com.replaymod.recording.mixin.IntegratedServerAccessor;
import com.replaymod.recording.packet.PacketListener;
import com.replaymod.replaystudio.replay.ReplayFile;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.server.integrated.IntegratedServer;

@Environment(EnvType.CLIENT)
public class ClientRecordingModule extends EventRegistrations implements Module {

    public static final String ENTRY = "client.ccap";

    private static ClientRecordingModule instance;

    public static ClientRecordingModule getInstance() {
        return instance;
    }
    
    private final ReplayMod replayMod;

    private Optional<ClientCapRecorder> activeRecording = Optional.empty();

    public ClientRecordingModule(ReplayMod replayMod) {
        this.replayMod = replayMod;
    }

    public ReplayMod getReplayMod() {
        return replayMod;
    }

    @Override
    public void initCommon() {
        instance = this;
    }

    @Override
    public void register() {
        super.register();
        WorldRenderEvents.END.register(this::onFrame);
    }

    private ClientCapHeader queuedHeader;

    { on(RecordingEvents.STARTED_RECORDING, this::onStartedRecording); }
    protected void onStartedRecording(PacketListener listener, ReplayFile file) {
        List<ChannelHandler<?>> channels = new LinkedList<>();
        
        ChannelRegistrationCallback.EVENT.invoker().createChannels(handler -> {
            if (!ChannelHandlers.REGISTRY.inverse().containsKey(handler)) {
                throw new IllegalArgumentException("The supplied channel handler has not been registered!");
            }
            channels.add(handler);
        });

        ClientCapHeader header = new ClientCapHeader(channels);
        try {
            OutputStream out = file.write(ENTRY);
            ClientCapRecorder recorder = new ClientCapRecorder(out);
            activeRecording = Optional.of(recorder);
            queuedHeader = header;

        } catch (Exception e) {
            LogUtils.getLogger().error("Unable to initialize client-cap recording.", e);
        }
    }

    { on(RecordingEvents.STOP_RECORDING, this::onStoppingRecording); }
    protected void onStoppingRecording(PacketListener listener, ReplayFile file) {
        if (isRecording()) stopRecording();
    }

    { on(PreRenderCallback.EVENT, this::checkForGamePaused); }
    protected void checkForGamePaused() {
        MinecraftClient client = replayMod.getMinecraft();
        if (activeRecording.isPresent() && client.isIntegratedServerRunning()) {
            IntegratedServer server = client.getServer();
            if (((IntegratedServerAccessor) server).isGamePaused()) {
                activeRecording.get().setServerWasPaused();
            }
        }
    }

    protected void onFrame(WorldRenderContext context) {
        if (activeRecording.isPresent()) {
            ClientCapRecorder recording = activeRecording.get();
            ClientCaptureContext clientContext = new ClientCaptureContextImpl(context, MinecraftClient.getInstance());

            if (recording.getHeader() == null) {
                initRecording(recording, clientContext.localPlayer().getId());
            }
            recording.tick(clientContext);
        }
    }

    private void initRecording(ClientCapRecorder recording, int localPlayerId) {
        queuedHeader.setLocalPlayerID(localPlayerId);
        recording.writeHeader(queuedHeader);
        recording.startRecording();
    }

    public Optional<ClientCapRecorder> getActiveRecording() {
        return activeRecording;
    }

    public boolean isRecording() {
        return activeRecording.isPresent();
    }
    
    /**
     * Stop recording the client-cap.
     * @throws IllegalStateException If we're not currently recording.
     */
    public void stopRecording() throws IllegalStateException {
        if (!isRecording()) {
            throw new IllegalStateException("We are not recording.");
        }

        try {
            activeRecording.get().close();
        } catch (IOException e) {
            LogUtils.getLogger().error("Error closing recording stream.", e);
        }
        activeRecording = Optional.empty();
    }

    private static class ClientCaptureContextImpl implements ClientCaptureContext {

        private final WorldRenderContext renderContext;
        private final MinecraftClient client;

        public ClientCaptureContextImpl(WorldRenderContext renderContext, MinecraftClient client) {
            this.renderContext = renderContext;
            this.client = client;
        }

        @Override
        public MinecraftClient client() {
            return client;
        }

        @Override
        public float tickDelta() {
            return renderContext.tickDelta();
        }

        @Override
        public Entity cameraEntity() {
            return client.cameraEntity;
        }

        @Override
        public Camera camera() {
            return renderContext.camera();
        }

        @Override
        public ClientPlayerEntity localPlayer() {
            return client.player;
        }

        @Override
        public GameRenderer gameRenderer() {
            return renderContext.gameRenderer();
        }

        @Override
        public ClientWorld world() {
            return renderContext.world();
        }

        @Override
        public WorldRenderContext renderContext() {
            return renderContext;
        }
        
    }
}


