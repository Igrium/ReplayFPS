package com.igrium.replayfps.networking.fake_packet;

import java.util.function.Consumer;

import com.igrium.replayfps.game_events.InventoryModifiedEvent;
import com.igrium.replayfps.networking.FakePacketHandler;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class UpdateInventoryFakePacket extends FakePacketHandler<UpdateInventoryValue> {

    public UpdateInventoryFakePacket(Identifier id) {
        super(id);
    }

    @Override
    public Class<UpdateInventoryValue> getType() {
        return UpdateInventoryValue.class;
    }

    @Override
    @SuppressWarnings("resource")
    public void registerListener(Consumer<UpdateInventoryValue> consumer) {
        InventoryModifiedEvent.EVENT.register((inventory, slot, oldStack, newStack) -> {
            if (oldStack.equals(newStack)) return;
            if (!inventory.player.getWorld().isClient) return;
            consumer.accept(new UpdateInventoryValue(slot, newStack));
        });
    }


    @Override
    public void write(UpdateInventoryValue value, PacketByteBuf buf) {
        buf.writeInt(value.slot());
        buf.writeItemStack(value.newStack());
    }

    @Override
    public UpdateInventoryValue parse(PacketByteBuf buf) {
        return new UpdateInventoryValue(
            buf.readInt(), 
            buf.readItemStack());
    }

    @Override
    public void apply(UpdateInventoryValue value, MinecraftClient client, PlayerEntity player) {
        client.execute(() -> {
            player.getInventory().setStack(value.slot(), value.newStack());
        });
    }

    @Override
    public SpectatorBehavior getSpectatorBehavior() {
        return SpectatorBehavior.APPLY;
    }
    
}

record UpdateInventoryValue(int slot, ItemStack newStack) {};
