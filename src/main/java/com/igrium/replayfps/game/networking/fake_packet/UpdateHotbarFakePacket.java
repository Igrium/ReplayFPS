package com.igrium.replayfps.game.networking.fake_packet;

import java.util.function.Consumer;

import com.igrium.replayfps.core.networking.FakePacketHandler;
import com.igrium.replayfps.game.event.InventoryModifiedEvent;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class UpdateHotbarFakePacket extends FakePacketHandler<UpdateHotbarValue> {

    public UpdateHotbarFakePacket(Identifier id) {
        super(id);
    }

    @Override
    public Class<UpdateHotbarValue> getType() {
        return UpdateHotbarValue.class;
    }

    @Override
    @SuppressWarnings("resource")
    public void registerListener(Consumer<UpdateHotbarValue> consumer) {
        InventoryModifiedEvent.EVENT.register((inv, map) -> {
            if (!inv.player.getWorld().isClient) return;

            // Map<Integer, ItemStack> changed = new HashMap<>();
            // for (int i = 0; i < inv.main.size(); i++) {
            //     changed.put(i, inv.main.get(i));
            // }
            Int2ObjectMap<ItemStack> changed = new Int2ObjectArrayMap<>(inv.main.size());
            int i = 0;
            for (ItemStack stack : inv.main) {
                changed.put(i, stack);
                i++;
            }

            consumer.accept(new UpdateHotbarValue(changed));
        });
    }

    @Override
    public void write(UpdateHotbarValue value, PacketByteBuf buf) {
        var map = value.map();
        buf.writeInt(map.size());
        map.forEach((slot, stack) -> {
            buf.writeInt(slot);
            buf.writeItemStack(stack);
        });
    }

    @Override
    public UpdateHotbarValue parse(PacketByteBuf buf) {
        int size = buf.readInt();
        Int2ObjectMap<ItemStack> map = new Int2ObjectArrayMap<>(size);
        for (int i = 0; i < size; i++) {
            int slot = buf.readInt();
            ItemStack stack = buf.readItemStack();
            map.put(slot, stack);
        }

        return new UpdateHotbarValue(map);
    }

    @Override
    public void apply(UpdateHotbarValue value, MinecraftClient client, PlayerEntity player) {
        value.map().forEach((slot, stack) -> {
            player.getInventory().setStack(slot, stack);
        });
    }

    @Override
    public SpectatorBehavior getSpectatorBehavior() {
        return SpectatorBehavior.APPLY;
    }

    
}

record UpdateHotbarValue(Int2ObjectMap<ItemStack> map) {};
