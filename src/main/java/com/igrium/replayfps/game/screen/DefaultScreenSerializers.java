package com.igrium.replayfps.game.screen;

import com.igrium.replayfps.core.screen.ScreenSerializers;

import net.minecraft.util.Identifier;

public class DefaultScreenSerializers {
    public static void register() {
        ScreenSerializers.register(new Identifier("minecraft:sign_edit"), new SignEditScreenSerializer());
    }
}
