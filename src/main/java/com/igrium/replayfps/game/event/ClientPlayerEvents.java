package com.igrium.replayfps.game.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.world.GameMode;

public class ClientPlayerEvents {
    public static final Event<SetGamemodeEvent> SET_GAMEMODE = EventFactory.createArrayBacked(
        SetGamemodeEvent.class, listeners -> (player, oldGamemode, newGamemode) -> {
            for (var l : listeners) {
                l.onSetGamemode(player, oldGamemode, newGamemode);
            }
        });

    public static interface SetGamemodeEvent {
        void onSetGamemode(ClientPlayerEntity player, GameMode oldGamemode, GameMode newGamemode);
    }
}
