package com.igrium.replayfps.game.event;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public interface InventoryModifiedEvent {

    public Event<InventoryModifiedEvent> EVENT = EventFactory.createArrayBacked(
        InventoryModifiedEvent.class, listeners -> (inv, updates) -> {
            for (var l : listeners) {
                l.onInventoryModified(inv, updates);
            }
        });
    
    public void onInventoryModified(PlayerInventory inventory, Int2ObjectMap<ItemStack> updates);
}
