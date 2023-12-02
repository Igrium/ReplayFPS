package com.igrium.replayfps.game.events;

import java.util.Map;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public interface HotbarModifiedEvent {

    public Event<HotbarModifiedEvent> EVENT = EventFactory.createArrayBacked(
        HotbarModifiedEvent.class, listeners -> (inv, updates) -> {
            for (var l : listeners) {
                l.onInventoryModified(inv, updates);
            }
        });
    
    public void onInventoryModified(PlayerInventory inventory, Map<Integer, ItemStack> updates);
}
