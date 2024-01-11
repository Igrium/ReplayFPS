package com.igrium.replayfps.core.networking;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.igrium.replayfps.core.mixin.ClientConnectionAccessor;
import com.igrium.replayfps.core.networking.event.FakePacketRegistrationCallback;
import com.igrium.replayfps.core.playback.ClientCapPlayer;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.igrium.replayfps.core.util.PlaybackUtils;
import com.mojang.logging.LogUtils;

import io.netty.channel.Channel;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.impl.networking.payload.ResolvablePayload;
import net.fabricmc.fabric.impl.networking.payload.ResolvedPayload;
import net.fabricmc.fabric.impl.networking.payload.TypedPayload;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.impl.networking.payload.ResolvablePayload.Handler;

public class FakePacketManager {

    public static final String PREFIX = "rp_";

    public static enum SpectatorRule { APPLY, SKIP }

    private final MinecraftClient client;
    private final ClientPlaybackModule module;
    private final ClientCapPlayer clientCap;

    private final Map<Identifier, Handler<FakePacketHandlerInternal>> handlers = Collections.synchronizedMap(new HashMap<>());
    private final Map<Identifier, SpectatorRule> spectatorRules = Collections.synchronizedMap(new HashMap<>());

    public Map<Identifier, SpectatorRule> getSpectatorRules() {
        return spectatorRules;
    }

    public FakePacketManager(MinecraftClient client, ClientPlaybackModule module, ClientCapPlayer clientCap) {
        this.client = client;
        this.module = module;
        this.clientCap = clientCap;
    }

    /**
     * Register all listeners after an instance has been created.
     */
    public void initReceivers() {
        FakePacketRegistrationCallback.EVENT.invoker().register(this);
    }
    
    /**
     * Determine if a given packet should be parsed as a fake packet.
     * @param rawId Unprocessed packet ID.
     * @return If this is a fake packet.
     */
    public static boolean isFakePacket(Identifier rawId) {
        return rawId.getNamespace().startsWith(PREFIX);
    }

    /**
     * Process an incoming custom packet.
     * @param payload Payload to process.
     * @return If this packet was consumed as a fake packet.
     */
    public boolean processPacket(ResolvablePayload payload) {
        Identifier rawId = payload.id();
        if (!rawId.getNamespace().startsWith(PREFIX)) return false;

        String namespace = rawId.getNamespace().substring(PREFIX.length());
        Identifier id = new Identifier(namespace, rawId.getPath());
        
        this.handle(id, payload);
        return true;
    }

    private void handle(Identifier id, ResolvablePayload payload) {
        @Nullable
        var handler = handlers.get(id);
        if (handler == null)
            return;

        // TODO: Do we want to keep some of this on the netty thread?
        client.execute(() -> {
            Optional<PlayerEntity> playerOpt = module.getLocalPlayer();
            if (playerOpt.isEmpty())
                return;
            PlayerEntity player = playerOpt.get();

            SpectatorRule rule = spectatorRules.getOrDefault(player, SpectatorRule.APPLY);
            if (client.getCameraEntity() != player && rule != SpectatorRule.APPLY)
                return;

            try {
                ResolvedPayload resolved = payload.resolve(handler.type());
                handler.internal().handle(resolved, player);
            } catch (Throwable ex) {
                LogUtils.getLogger().error("Error handling fake packet: " + id, ex);
            }
        });

    }

    public <T extends FabricPacket> void registerReceiver(PacketType<T> type, FakePacketHandler<T> handler) {
        handlers.put(type.getId(), wrapTyped(type, handler));
    }

    /**
     * Set the behavior for when a fake packet is received while not spectating the player.
     * @param id ID of the packet to apply to.
     * @param spectatorRule Spectator rule.
     */
    public <T extends FabricPacket> void addSpectatorRule(Identifier id, SpectatorRule spectatorRule) {
        spectatorRules.put(id, Objects.requireNonNull(spectatorRule));
    }

    @SuppressWarnings("unchecked")
    private <T extends FabricPacket> Handler<FakePacketHandlerInternal> wrapTyped(PacketType<T> type, FakePacketHandler<T> actual) {
        return new Handler<>(type, actual, (payload, localPlayer) -> {
            T packet = (T) ((TypedPayload) payload).packet();

            client.execute(() -> actual.handle(packet, module, clientCap, localPlayer));
        });
    }

    public static interface FakePacketHandler<T extends FabricPacket> {
        public void handle(T packet, ClientPlaybackModule module, ClientCapPlayer clientCap, PlayerEntity localPlayer);
    }

    private static interface FakePacketHandlerInternal {
        void handle(ResolvedPayload payload, PlayerEntity localPlayer);
    }

    /**
     * Inject a fake packet into the replay packet stream.
     * @param packet Fake packet.
     */
    public static void injectFakePacket(FabricPacket packet) {
        injectPacket(new CustomPayloadS2CPacket(createPayload(packet)));
    }

    /**
     * Trick the client into thinking a packet has been received from the server,
     * therefore injecting it into the replay mod packet stream.
     * 
     * @param packet Packet to inject.
     */
    public static void injectPacket(Packet<?> packet) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayNetworkHandler netHandler = client.getNetworkHandler();
        if (netHandler == null) {
            throw new IllegalStateException("Packets can only be injected while a game is active.");
        }

        // Don't want to re-send packets during replay playback.
        if (PlaybackUtils.isPlayingReplay()) return;

        Channel channel = ((ClientConnectionAccessor) netHandler.getConnection()).replayfps$getChannel();
        if (channel.eventLoop().inEventLoop()) {
            injectPacketInternal(channel, packet);
        } else {
            channel.eventLoop().execute(() -> injectPacketInternal(channel, packet));
        }
    }

    private static void injectPacketInternal(Channel channel, Packet<?> packet) {
        if (packet instanceof CustomPayloadS2CPacket customPayload && customPayload.payload() instanceof FabricPacketModifiedPayload) {
            // Force re-create the packet if it has a modified payload so recieving code works properly.
            PacketByteBuf buf = PacketByteBufs.create();
            packet.write(buf);
            packet = new CustomPayloadS2CPacket(buf);
        }
        channel.pipeline().fireChannelRead(packet);
    }

    public static CustomPayload createPayload(FabricPacket packet) {
        return new FabricPacketModifiedPayload(packet);
    }
    
    /**
     * A simple re-implementation of TypedPayload that provides an ID with the prefix.
     */
    private static record FabricPacketModifiedPayload(FabricPacket packet) implements CustomPayload {

        @Override
        public void write(PacketByteBuf buffer) {
            packet.write(buffer);
        }

        @Override
        public Identifier id() {
            Identifier nativeId = packet.getType().getId();
            return new Identifier(PREFIX + nativeId.getNamespace(), nativeId.getPath());
        }

    }
}
