package com.igrium.replayfps.networking.handler;

import java.util.function.Consumer;

import com.igrium.replayfps.game_events.SetExperienceEvent;
import com.igrium.replayfps.networking.FakePacketHandler;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ExperienceFakePacket extends FakePacketHandler<ExperienceData> {

    public ExperienceFakePacket(Identifier id) {
		super(id);
	}

	@Override
    public Class<ExperienceData> getType() {
        return ExperienceData.class;
    }

    @Override
    public void registerListener(Consumer<ExperienceData> consumer) {
        SetExperienceEvent.EVENT.register((progress, total, level, player) -> {
            consumer.accept(new ExperienceData(progress, total, level));
        });
    }

    @Override
    public void write(ExperienceData value, PacketByteBuf buf) {
        buf.writeFloat(value.progress());
        buf.writeInt(value.total());
        buf.writeInt(value.level());
    }

    @Override
    public ExperienceData parse(PacketByteBuf buf) {
        return new ExperienceData(
                buf.readFloat(),
                buf.readInt(),
                buf.readInt());
    }

    @Override
    public void apply(ExperienceData value, MinecraftClient client, PlayerEntity player) {
        player.experienceProgress = value.progress();
        player.totalExperience = value.total();
        player.experienceLevel = value.level();

        // The hud looks for the client's local entity instead of the camera entity.
        client.player.setExperience(value.progress(), value.total(), value.level());
    }

    @Override
    public SpectatorBehavior getSpectatorBehavior() {
        return SpectatorBehavior.APPLY;
    }
    
}

record ExperienceData(float progress, int total, int level) {};