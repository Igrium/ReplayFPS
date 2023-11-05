package com.igrium.replayfps.recording;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.igrium.replayfps.channel.handler.ChannelHandler;
import com.igrium.replayfps.events.ChannelRegistrationCallback;
import com.igrium.replayfps.events.RecordingEvents;
import com.mojang.logging.LogUtils;
import com.replaymod.core.Module;
import com.replaymod.core.ReplayMod;
import com.replaymod.lib.de.johni0702.minecraft.gui.utils.EventRegistrations;
import com.replaymod.recording.packet.PacketListener;
import com.replaymod.replaystudio.replay.ReplayFile;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

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

    { on(RecordingEvents.STARTED_RECORDING, this::onStartedRecording); }
    protected void onStartedRecording(PacketListener listener, ReplayFile file) {
        List<ChannelHandler<?>> channels = new LinkedList<>();
        ChannelRegistrationCallback.EVENT.invoker().createChannels(channels::add);

        ClientCapHeader header = new ClientCapHeader(channels);
        try {
            OutputStream out = file.write(ENTRY);
            startRecording(header, out);
        } catch (Exception e) {
            LogUtils.getLogger().error("Unable to initialize client-cap recording.", e);
        }
    }

    { on(RecordingEvents.STOP_RECORDING, this::onStoppingRecording); }
    protected void onStoppingRecording(PacketListener listener, ReplayFile file) {
        if (isRecording()) stopRecording();
    }

    protected void onFrame(WorldRenderContext context) {
        if (activeRecording.isPresent()) {
            activeRecording.get().tick(context);
        }
    }

    public Optional<ClientCapRecorder> getActiveRecording() {
        return activeRecording;
    }

    public boolean isRecording() {
        return activeRecording.isPresent();
    }

    /**
     * Start recording a client-cap.
     * @param header Client-cap header.
     * @param out Output stream to write to.
     * @throws IOException If an IO exception occurs while writing the header.
     * @throws IllegalStateException If we're already recording.
     */
    public void startRecording(ClientCapHeader header, OutputStream out) throws IOException, IllegalStateException {
        if (isRecording()) {
            throw new IllegalStateException("We are already recording.");
        }

        ClientCapRecorder recorder = new ClientCapRecorder(out);
        recorder.writeHeader(header);
        recorder.beginCapture();
        activeRecording = Optional.of(recorder);
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
}


