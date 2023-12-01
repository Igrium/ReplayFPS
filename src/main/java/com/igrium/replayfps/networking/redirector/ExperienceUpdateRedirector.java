package com.igrium.replayfps.networking.redirector;

import com.igrium.replayfps.networking.PacketRedirector;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;

public class ExperienceUpdateRedirector implements PacketRedirector<ExperienceBarUpdateS2CPacket> {

    @Override
    public Class<ExperienceBarUpdateS2CPacket> getPacketClass() {
        return ExperienceBarUpdateS2CPacket.class;
    }

    @Override
    public boolean shouldRedirect(ExperienceBarUpdateS2CPacket packet, PlayerEntity localPlayer,
            MinecraftClient client) {
        return true;
    }

    @Override
    public void redirect(ExperienceBarUpdateS2CPacket packet, PlayerEntity localPlayer, MinecraftClient client) {
        // What's up with these mappings, Yarn?
        client.execute(() -> {
            localPlayer.experienceProgress = packet.getBarProgress();
            localPlayer.totalExperience = packet.getExperienceLevel();
            localPlayer.experienceLevel = packet.getExperience();

            // The hud looks for the client's local entity instead of the camera entity.
            client.player.setExperience(packet.getBarProgress(), packet.getExperienceLevel(), packet.getExperience());
        });
    }
    
}
