package com.igrium.replayfps.game;

import com.igrium.replayfps.game.event.ClientJoinedWorldEvent;
import com.igrium.replayfps.game.event.InventoryModifiedEvent;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

/**
 * Until I get screen handlers working properly, this helps with syncing the player hotbar.
 */
public class BullshitPlayerInventoryManager {
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(BullshitPlayerInventoryManager::onEndTick);
        ClientJoinedWorldEvent.EVENT.register((client, world) -> reset());
    }

    private static ItemStack[] prevInventory = new ItemStack[36];
    
    private static void onEndTick(MinecraftClient client) {
        PlayerEntity player = client.player;
        if (player == null) return;

        Int2ObjectMap<ItemStack> updated = new Int2ObjectArrayMap<>(36);

        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < prevInventory.length; i++) {
            ItemStack newStack = inventory.getStack(i);
            if (prevInventory[i] == null || !ItemStack.areEqual(prevInventory[i], newStack)) {
                updated.put(i, newStack);
            }
            
            prevInventory[i] = newStack.copy();
        }

        if (!updated.isEmpty()) {
            InventoryModifiedEvent.EVENT.invoker().onInventoryModified(player.getInventory(), updated);
        }

    }

    private static void reset() {
        for (int i = 0; i < prevInventory.length; i++) {
            prevInventory[i] = null;
        }
    }

}
