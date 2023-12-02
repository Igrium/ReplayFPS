package com.igrium.replayfps.game.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Called on the client whenever the server has told it its food level has updated.
 */
public interface UpdateFoodEvent {

    public static final Event<UpdateFoodEvent> EVENT = EventFactory.createArrayBacked(UpdateFoodEvent.class, 
            listeners -> (player, food, saturation) -> {
                for (var l : listeners) {
                    l.onUpdateFood(player, food, saturation);
                }
            });

    void onUpdateFood(PlayerEntity player, int food, float saturation);
}
