package com.igrium.replayfps.game.networking.fake_packet;

import java.util.function.Consumer;

import com.igrium.replayfps.core.networking.FakePacketHandler;
import com.igrium.replayfps.game.event.ClientPlayerEvents;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class UpdateSelectedSlotFakePacket extends FakePacketHandler<Integer> {

    public UpdateSelectedSlotFakePacket(Identifier id) {
        super(id);
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    @SuppressWarnings("resource")
    public void registerListener(Consumer<Integer> consumer) {
        ClientPlayerEvents.SELECT_SLOT.register((inv, slot) -> {
            if (!inv.player.getWorld().isClient) return;
            consumer.accept(slot);
        });
    }

    @Override
    public void write(Integer value, PacketByteBuf buf) {
        buf.writeInt(value);
    }

    @Override
    public Integer parse(PacketByteBuf buf) {
        return buf.readInt();
    }

    @Override
    public void apply(Integer value, MinecraftClient client, PlayerEntity player) {
        player.getInventory().selectedSlot = value;
    }

    @Override
    public SpectatorBehavior getSpectatorBehavior() {
        return SpectatorBehavior.APPLY;
    }
    
}
