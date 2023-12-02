package com.igrium.replayfps.game.networking.fake_packet;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.igrium.replayfps.core.networking.FakePacketHandler;
import com.igrium.replayfps.game.events.HotbarModifiedEvent;

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
        HotbarModifiedEvent.EVENT.register((inv, map) -> {
            if (!inv.player.getWorld().isClient) return;
            Map<Integer, ItemStack> changed = new HashMap<>();
            for (int i = 0; i < inv.main.size(); i++) {
                changed.put(i, inv.main.get(i));
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
        Map<Integer, ItemStack> map = new HashMap<>();
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

record UpdateHotbarValue(Map<Integer, ItemStack> map) {};
