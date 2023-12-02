package com.igrium.replayfps.game;

import java.util.HashMap;
import java.util.Map;

import com.igrium.replayfps.game.events.ClientJoinedWorldEvent;
import com.igrium.replayfps.game.events.HotbarModifiedEvent;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * Until I get screen handlers working properly, this helps with syncing the player hotbar.
 */
public class BullshitPlayerInventoryManager {
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(BullshitPlayerInventoryManager::onEndTick);
        ClientJoinedWorldEvent.EVENT.register((client, world) -> reset());
    }

    public static final int HOTBAR_SLOT_OFFSET = 36;

    private static ItemStack[] prevInventory = new ItemStack[9];

    private static void onEndTick(MinecraftClient client) {
        PlayerEntity player = client.player;
        if (player == null) return;

        Map<Integer, ItemStack> updated = new HashMap<>();

        for (int i = 0; i < prevInventory.length; i++) {
            ItemStack stack = player.getInventory().getStack(i + HOTBAR_SLOT_OFFSET);

            if (!stackEquals(stack, prevInventory[i])) {
                updated.put(i + HOTBAR_SLOT_OFFSET, stack);
                prevInventory[i] = stack;
            }
        }

        if (updated.isEmpty()) return;
        HotbarModifiedEvent.EVENT.invoker().onInventoryModified(player.getInventory(), updated);

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
