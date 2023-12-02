package com.igrium.replayfps.game.game_events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public interface InventoryModifiedEvent {

    public Event<InventoryModifiedEvent> EVENT = EventFactory.createArrayBacked(
        InventoryModifiedEvent.class, listeners -> (inv, slot, oldStack, newStack) -> {
            for (var l : listeners) {
                l.onInventoryModified(inv, 0, oldStack, newStack);
            }
        });
    
    public void onInventoryModified(PlayerInventory inventory, int slot, ItemStack oldStack, ItemStack newStack);
}
