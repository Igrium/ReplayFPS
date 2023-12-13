package com.igrium.replayfps.game.networking.fake_packet;

import com.igrium.replayfps.core.networking.FakePacketManager;
import com.igrium.replayfps.core.playback.ClientCapPlayer;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.igrium.replayfps.game.event.ClientPlayerEvents;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record UpdateSelectedSlotFakePacket(int slot) implements FabricPacket {

    public static final PacketType<UpdateSelectedSlotFakePacket> TYPE = PacketType
            .create(new Identifier("replayfps:update_slot"), UpdateSelectedSlotFakePacket::read);

    public static UpdateSelectedSlotFakePacket read(PacketByteBuf buf) {
        return new UpdateSelectedSlotFakePacket(buf.readInt());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(slot);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public static void apply(UpdateSelectedSlotFakePacket packet, ClientPlaybackModule module,
            ClientCapPlayer clientCap, PlayerEntity localPlayer) {
        localPlayer.getInventory().selectedSlot = packet.slot();
    }

    @SuppressWarnings("resource")
    public static void registerListener() {
        ClientPlayerEvents.SELECT_SLOT.register((inv, slot) -> {
            if (!inv.player.getWorld().isClient) return;
            FakePacketManager.injectFakePacket(new UpdateSelectedSlotFakePacket(slot));
        });
    }
    
}
