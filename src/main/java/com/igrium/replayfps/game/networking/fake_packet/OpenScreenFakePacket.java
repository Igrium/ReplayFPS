package com.igrium.replayfps.game.networking.fake_packet;

import java.util.function.Consumer;

import com.igrium.replayfps.core.networking.FakePacketHandler;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.igrium.replayfps.core.screen.PlaybackScreenManager;
import com.igrium.replayfps.core.screen.ScreenSerializer;
import com.igrium.replayfps.core.screen.ScreenSerializers;
import com.igrium.replayfps.game.event.ClientScreenEvents;
import com.mojang.logging.LogUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class OpenScreenFakePacket extends FakePacketHandler<OpenScreenPacketData> {

    public OpenScreenFakePacket(Identifier id) {
        super(id);
    }

    @Override
    public Class<OpenScreenPacketData> getType() {
        return OpenScreenPacketData.class;
    }

    private static final OpenScreenPacketData EMPTY = new OpenScreenPacketData(null, null);

    @Override
    public void registerListener(Consumer<OpenScreenPacketData> consumer) {
        ClientScreenEvents.SCREEN_CHANGED.register((client, oldScreen, newScreen) -> {
            if (client.world == null) return;
            consumer.accept(readScreen(newScreen));
        });
    }

    private OpenScreenPacketData readScreen(Screen screen) {
        if (screen == null) return EMPTY;

        ScreenSerializer<?, ?> serializer = ScreenSerializers.get(screen.getClass());
        if (serializer == null) return EMPTY;

        Object value = readScreenData(serializer, screen);
        return new OpenScreenPacketData(serializer, value);
    }

    private <S extends Screen, T> T readScreenData(ScreenSerializer<S, T> serializer, Screen screen) {
        return serializer.parse(serializer.getScreenType().cast(screen));
    }

    @Override
    public void write(OpenScreenPacketData value, PacketByteBuf buf) {
        boolean stayOpen = value.serializer() != null;
        buf.writeBoolean(stayOpen);
        if (!stayOpen) return;

        Identifier id = ScreenSerializers.getId(value.serializer());
        if (id == null) {
            throw new IllegalStateException("Screen serializer has not been registered!");
        }
        
        buf.writeIdentifier(id);
        serializeAndWrite(value.serializer(), value.value(), buf);
    }

    private <T> void serializeAndWrite(ScreenSerializer<?, T> serializer, Object value, PacketByteBuf buffer) {
        serializer.write(serializer.getSerializedType().cast(value), buffer);
    }

    @Override
    public OpenScreenPacketData parse(PacketByteBuf buf) {
        boolean stayOpen = buf.readBoolean();
        if (!stayOpen) return EMPTY;

        Identifier id = buf.readIdentifier();
        ScreenSerializer<?, ?> serializer = ScreenSerializers.get(id);

        if (serializer == null) {
            LogUtils.getLogger().error("Unknown screen serializer: " + id);
            return EMPTY;
        }

        Object val = serializer.read(buf);
        return new OpenScreenPacketData(serializer, val);
    }



    @Override
    public void apply(OpenScreenPacketData value, MinecraftClient client, PlayerEntity player) {
        PlaybackScreenManager screenManager = ClientPlaybackModule.getInstance().getPlaybackScreenManager();
        if (screenManager == null) return;

        if (value.serializer() == null || value.value() == null) {
            screenManager.clearScreen();
        } else {
            screenManager.openScreen(value.serializer(), value.value());
        }
        
    }
    
}

record OpenScreenPacketData(ScreenSerializer<?, ?> serializer, Object value) {};