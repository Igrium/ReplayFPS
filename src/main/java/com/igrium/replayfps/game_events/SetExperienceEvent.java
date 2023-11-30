package com.igrium.replayfps.game_events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface SetExperienceEvent {

    public static Event<SetExperienceEvent> EVENT = EventFactory.createArrayBacked(SetExperienceEvent.class, 
            listeners -> (progress, total, level, player) -> {
                for (var l : listeners) {
                    l.onSetExperience(progress, total, level, player);
                }
            });

    public void onSetExperience(float progress, int total, int level, PlayerEntity player);
}
