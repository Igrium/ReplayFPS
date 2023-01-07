package com.igrium.replayfps.clientcap;

import java.io.IOException;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;

import com.igrium.replayfps.events.RecordingEvents;
import com.igrium.replayfps.mixins.PacketListenerAccessor;
import com.replaymod.core.Module;
import com.replaymod.core.ReplayMod;
import com.replaymod.lib.de.johni0702.minecraft.gui.utils.EventRegistrations;
import com.replaymod.recording.ReplayModRecording;
import com.replaymod.recording.packet.PacketListener;
import com.replaymod.replaystudio.replay.ReplayFile;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class ClientRecordingModule extends EventRegistrations implements Module {
    private static ClientRecordingModule instance;

    public static final String ENTRY = "client.ccap";

    private final ReplayMod replayMod;
    private ClientCapRecorder currentRecording;

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
    protected void onStartedRecording(PacketListener packetListener, ReplayFile file) {
        currentRecording = new ClientCapRecorder(() -> file.write(ENTRY));
        currentRecording.beginCapture();
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

    protected void eachFrame(WorldRenderContext context) {
        if (currentRecording == null) return;
        CaptureContextImpl capContext = new CaptureContextImpl(replayMod.getMinecraft(), context.tickDelta(),
                context.camera(), context.frustum());
        currentRecording.captureFrame(capContext);
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
        private final Frustum frustum;

        public CaptureContextImpl(MinecraftClient client, float tickDelta, Camera camera, Frustum frustum) {
            this.client = client;
            this.tickDelta = tickDelta;
            this.camera = camera;
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
        public Camera camera() {
            return camera;
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
