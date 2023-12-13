package com.igrium.replayfps.game.networking.fake_packet;

import com.igrium.replayfps.core.networking.FakePacketManager;
import com.igrium.replayfps.core.playback.ClientCapPlayer;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.igrium.replayfps.game.event.InventoryModifiedEvent;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class UpdateHotbarFakePacket implements FabricPacket {

    public static final PacketType<UpdateHotbarFakePacket> TYPE = PacketType
            .create(new Identifier("replayfps:update_hotbar"), UpdateHotbarFakePacket::new);

    public final Int2ObjectMap<ItemStack> map;

    public UpdateHotbarFakePacket(Int2ObjectMap<ItemStack> map) {
        this.map = map;
    }

    public UpdateHotbarFakePacket(PacketByteBuf buf) {
        int size = buf.readInt();
        map = new Int2ObjectArrayMap<>(size);
        for (int i = 0; i < size; i++) {
            int slot = buf.readInt();
            ItemStack stack = buf.readItemStack();
            map.put(slot, stack);
        }
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(map.size());
        map.forEach((slot, stack) -> {
            buf.writeInt(slot);
            buf.writeItemStack(stack);
        });
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
    
    public static void apply(UpdateHotbarFakePacket packet, ClientPlaybackModule module,
            ClientCapPlayer clientCap, PlayerEntity localPlayer) {
        packet.map.forEach((slot, stack) -> {
            localPlayer.getInventory().setStack(slot, stack);
        });
    }

    @SuppressWarnings("resource")
    public static void registerListener() {
        InventoryModifiedEvent.EVENT.register((inv, map) -> {
            if (!inv.player.getWorld().isClient) return;

            // TODO: Remember why I needed to reconstruct the map
            Int2ObjectMap<ItemStack> changed = new Int2ObjectArrayMap<>(inv.main.size());
            int i = 0;
            for (ItemStack stack : inv.main) {
                changed.put(i, stack);
                i++;
            }
            FakePacketManager.injectFakePacket(new UpdateHotbarFakePacket(map));
        });
    }
}
