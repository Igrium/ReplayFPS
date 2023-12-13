package com.igrium.replayfps.game.networking.fake_packet;

import com.igrium.replayfps.core.networking.FakePacketManager;
import com.igrium.replayfps.core.playback.ClientCapPlayer;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.igrium.replayfps.game.event.ClientJoinedWorldEvent;
import com.igrium.replayfps.game.event.ClientPlayerEvents;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

public record SetGamemodeFakePacket(GameMode gamemode) implements FabricPacket {

    public static final PacketType<SetGamemodeFakePacket> TYPE = PacketType
            .create(new Identifier("replayfps:set_gamemode"), SetGamemodeFakePacket::read);

    public static SetGamemodeFakePacket read(PacketByteBuf buf) {
        return new SetGamemodeFakePacket(buf.readEnumConstant(GameMode.class));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeEnumConstant(gamemode);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
    
    public static void apply(SetGamemodeFakePacket packet, ClientPlaybackModule module,
            ClientCapPlayer clientCap, PlayerEntity localPlayer) {
        module.setHudGamemode(packet.gamemode());
    }

    public static void registerListener() {
        ClientPlayerEvents.SET_GAMEMODE.register((player, oldGamemode, newGamemode) -> {
            FakePacketManager.injectFakePacket(new SetGamemodeFakePacket(newGamemode));
        });

        ClientJoinedWorldEvent.EVENT.register((client, world) -> {
            FakePacketManager.injectFakePacket(new SetGamemodeFakePacket(
                    client.interactionManager.getCurrentGameMode()));
        });
    }
}
