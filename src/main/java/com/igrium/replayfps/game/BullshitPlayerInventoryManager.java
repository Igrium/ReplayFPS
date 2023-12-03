package com.igrium.replayfps.game;

import java.util.HashMap;
import java.util.Map;

import com.igrium.replayfps.game.event.ClientJoinedWorldEvent;
import com.igrium.replayfps.game.event.HotbarModifiedEvent;

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

        Map<Integer, ItemStack> updated = new HashMap<>();

        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < prevInventory.length; i++) {
            ItemStack newStack = inventory.getStack(i);
            if (!stackEquals(prevInventory[i], newStack)) {
                updated.put(i, newStack);
            }
            
            prevInventory[i] = newStack;
        }
        // if (inventory.getChangeCount() > lastChangeCount)
        //             HotbarModifiedEvent.EVENT.invoker().onInventoryModified(player.getInventory(), updated);

        // lastChangeCount = inventory.getChangeCount();

        // if (updated.isEmpty()) return;
        if (!updated.isEmpty()) {
            HotbarModifiedEvent.EVENT.invoker().onInventoryModified(player.getInventory(), updated);
        }

    }

    private static void reset() {
        for (int i = 0; i < prevInventory.length; i++) {
            prevInventory[i] = null;
        }
    }

    private static boolean stackEquals(ItemStack first, ItemStack second) {
        if (first == null || second == null) {
            return first == second;
        }
        return (ItemStack.canCombine(first, second)) && (first.getCount() == second.getCount());
        
    }
}
