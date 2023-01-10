package com.igrium.replayfps.recording;

import java.io.IOException;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;

import com.igrium.replayfps.clientcap.ClientCaptureContext;
import com.igrium.replayfps.events.RecordingEvents;
import com.igrium.replayfps.mixins.PacketListenerAccessor;
import com.replaymod.core.Module;
import com.replaymod.core.ReplayMod;
import com.replaymod.core.events.PreRenderCallback;
import com.replaymod.lib.de.johni0702.minecraft.gui.utils.EventRegistrations;
import com.replaymod.recording.ReplayModRecording;
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
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.server.integrated.IntegratedServer;

@Environment(EnvType.CLIENT)
public class ClientRecordingModule extends EventRegistrations implements Module {
    private static ClientRecordingModule instance;

    public static final String ENTRY = "client.ccap";
    public static final int SAVE_INTERVAL = 10000; // 10 seconds

    private final ReplayMod replayMod;
    private final MinecraftClient client = MinecraftClient.getInstance();
    @Nullable
    private ClientCapRecorder currentRecording;
    private long lastSave;

    public ClientRecordingModule(ReplayMod replayMod) {
        this.replayMod = replayMod;
    }

    public ReplayMod getReplayMod() {
        return replayMod;
    }

    public static ClientRecordingModule getInstance() {
        return instance;
    }
    
    @Override
    public void initCommon() {
        instance = this;
    }

    { on(RecordingEvents.STARTED_RECORDING, this::onStartedRecording); }
    @SuppressWarnings("resource") // Why is the leak detection so bad? This doesn't leak.
    protected void onStartedRecording(PacketListener packetListener, ReplayFile file) {
        currentRecording = new ClientCapRecorder(() -> file.write(ENTRY)).setPlayerSupplier(() -> client.player);
        currentRecording.beginCapture();
        lastSave = System.currentTimeMillis();
    }

    

    { on(RecordingEvents.STOP_RECORDING, this::onStoppingRecording); }
    protected void onStoppingRecording(PacketListener packetListener, ReplayFile file) {
        try {
            currentRecording.close();
        } catch (IOException e) {
            LogManager.getLogger().error("Error finalizing client capture: ", e);
        }
        currentRecording = null;
    }

    { on(PreRenderCallback.EVENT, this::checkForGamePaused); }
    protected void checkForGamePaused() {
        MinecraftClient client = replayMod.getMinecraft();
        if (currentRecording != null && client.isIntegratedServerRunning()) {
            IntegratedServer server = client.getServer();
            if (((IntegratedServerAccessor) server).isGamePaused()) {
                currentRecording.setServerWasPaused();
            }
        }
    }

    @Override
    public void register() {
        super.register();
        WorldRenderEvents.END.register(this::eachFrame);
    }

    protected void eachFrame(WorldRenderContext context) {
        if (currentRecording == null) return;
        // double fov = ((GameRendererAccessor) context.gameRenderer())
        //         .getFov(context.camera(), context.tickDelta(), true);
        double fov = 70;

        CaptureContextImpl capContext = new CaptureContextImpl(client, context.tickDelta(),
                context.camera(), fov, context.frustum());
        
        currentRecording.captureFrame(capContext);

        long now = System.currentTimeMillis();
        if (now > lastSave + SAVE_INTERVAL) {
            try {
                currentRecording.saveChunks();
            } catch (IOException e) {
                LogManager.getLogger().error("Error saving client capture: ", e);
            }
            lastSave = now;
        }
    }

    /**
     * Get the replay file we're currently recording to.
     * @return The current file, or <code>null</code> if we're not currently recording.
     */
    @Nullable
    public ReplayFile getCurrentReplayFile() {
        PacketListener packetListener = ReplayModRecording.instance.getConnectionEventHandler().getPacketListener();
        if (packetListener == null) return null;
        return ((PacketListenerAccessor) packetListener).getReplayFile();
    }

    private static class CaptureContextImpl implements ClientCaptureContext {

        private final MinecraftClient client;
        private final float tickDelta;
        private final Camera camera;
        private final double fov;
        private final Frustum frustum;

        public CaptureContextImpl(MinecraftClient client, float tickDelta, Camera camera, double fov, Frustum frustum) {
            this.client = client;
            this.tickDelta = tickDelta;
            this.camera = camera;
            this.fov = fov;
            this.frustum = frustum;
        }

        @Override
        public MinecraftClient client() {
            return client;
        }

        @Override
        public float tickDelta() {
            return tickDelta;
        }

        @Override
        public Entity cameraEntity() {
            return client.cameraEntity;
        }

        @Override
        public ClientPlayerEntity localPlayer() {
            return client.player;
        }

        @Override
        public Camera camera() {
            return camera;
        }
        
        @Override
        public double fov() {
            return fov;
        }

        @Override
        public GameRenderer gameRenderer() {
            return client.gameRenderer;
        }

        @Override
        public ClientWorld world() {
            return client.world;
        }

        @Override
        public Frustum frustum() {
            return frustum;
        }

        
    }
}
