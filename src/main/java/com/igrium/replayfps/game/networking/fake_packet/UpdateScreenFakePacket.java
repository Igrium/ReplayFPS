package com.igrium.replayfps.game.networking.fake_packet;

import java.util.function.Consumer;

import com.igrium.replayfps.core.networking.FakePacketHandler;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.igrium.replayfps.core.screen.PlaybackScreenManager;
import com.igrium.replayfps.core.screen.ScreenSerializer;
import com.igrium.replayfps.core.screen.ScreenSerializers;
import com.igrium.replayfps.game.event.ClientScreenEvents;
import com.mojang.logging.LogUtils;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

// It's easier to parse and apply in the same method.
public class UpdateScreenFakePacket extends FakePacketHandler<PacketByteBuf> {

    
    public UpdateScreenFakePacket(Identifier id) {
        super(id);
    }

    @Override
    public Class<PacketByteBuf> getType() {
        return PacketByteBuf.class;
    }

    @Override
    public void registerListener(Consumer<PacketByteBuf> consumer) {
        ClientScreenEvents.SCREEN_UPDATED.register((client, screen, oldVal, newVal) -> {
            ScreenSerializer<?, ?> serializer = ScreenSerializers.get(screen.getClass());
            if (serializer == null) return;

            PacketByteBuf buffer = PacketByteBufs.create();
            castAndWriteBuffer(serializer, newVal, buffer);
            buffer.resetReaderIndex();
            consumer.accept(buffer);
        });
    }
    

    @Override
    public void write(PacketByteBuf value, PacketByteBuf buf) {
        value.readBytes(buf, value.readableBytes());
    }

    @Override
    public PacketByteBuf parse(PacketByteBuf buf) {
        return PacketByteBufs.copy(buf);
    }

    @Override
    public void apply(PacketByteBuf value, MinecraftClient client, PlayerEntity player) {
        ClientPlaybackModule module = ClientPlaybackModule.getInstance();
        PlaybackScreenManager screenManager = module.getPlaybackScreenManager();
        if (screenManager == null) return;

        Screen screen = screenManager.getScreen().orElse(null);
        if (screen == null) return;

        ScreenSerializer<?, ?> serializer = ScreenSerializers.get(screen.getClass());
        if (serializer == null) return;

        try {
            Object serialized = serializer.readBuffer(value);
            castScreenAndApply(client, serializer, screen, serialized);
        } catch (Exception e) {
            LogUtils.getLogger().error("Error loading screen " + screen.getClass().getSimpleName(), e);
        }
    }

    private <T> void castAndWriteBuffer(ScreenSerializer<?, T> serializer, Object value, PacketByteBuf buffer) {
        serializer.writeBuffer(serializer.getSerializedType().cast(value), buffer);
    }
    
    private <S extends Screen, T> void castScreenAndApply(MinecraftClient client, ScreenSerializer<S, T> serializer, Screen screen, Object value) {
        serializer.apply(client, serializer.getSerializedType().cast(value), serializer.getScreenType().cast(screen));
    }
}
