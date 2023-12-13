package com.igrium.replayfps.game.networking;

import com.igrium.replayfps.core.networking.old.PacketRedirectors;
import com.igrium.replayfps.game.networking.redirector.EntityEquipmentUpdateRedirector;
import com.igrium.replayfps.game.networking.redirector.ExperienceUpdateRedirector;
import com.igrium.replayfps.game.networking.redirector.HealthHungerRedirector;

public class DefaultPacketRedirectors {
    public static void registerDefaults() {
        PacketRedirectors.register(new HealthHungerRedirector());
        PacketRedirectors.register(new ExperienceUpdateRedirector());
        
        PacketRedirectors.register(new EntityEquipmentUpdateRedirector());
    }
}
