package com.igrium.replayfps.game.networking.fake_packet;

import java.util.function.Consumer;

import com.igrium.replayfps.core.networking.FakePacketHandler;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.igrium.replayfps.game.event.ClientJoinedWorldEvent;
import com.igrium.replayfps.game.event.ClientPlayerEvents;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

public class SetGamemodeFakePacket extends FakePacketHandler<GameMode> {

    public SetGamemodeFakePacket(Identifier id) {
        super(id);
    }

    @Override
    public Class<GameMode> getType() {
        return GameMode.class;
    }

    @Override
    public void registerListener(Consumer<GameMode> consumer) {
        ClientPlayerEvents.SET_GAMEMODE.register((player, oldGamemode, newGamemode) -> {
            consumer.accept(newGamemode);
        });

        // We also send the gamemode packet on world join.
        ClientJoinedWorldEvent.EVENT.register((client, world) -> {
            consumer.accept(client.interactionManager.getCurrentGameMode());
        });
    }

    @Override
    public void write(GameMode value, PacketByteBuf buf) {
        buf.writeEnumConstant(value);
    }

    @Override
    public GameMode parse(PacketByteBuf buf) {
        return buf.readEnumConstant(GameMode.class);
    }

    @Override
    public void apply(GameMode value, MinecraftClient client, PlayerEntity player) {
        ClientPlaybackModule.getInstance().setHudGamemode(value);
    }
    
}
