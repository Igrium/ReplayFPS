package com.igrium.replayfps.game.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerInventory;

public interface UpdateSelectedSlotEvent {

    public static final Event<UpdateSelectedSlotEvent> EVENT = EventFactory.createArrayBacked(
        UpdateSelectedSlotEvent.class, listeners -> (inv, slot) -> {
            for (var l : listeners) {
                l.onUpdateSlot(inv, slot);
            }
        });

    public void onUpdateSlot(PlayerInventory inventory, int slot);
}
