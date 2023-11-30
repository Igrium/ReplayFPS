package com.igrium.replayfps.networking.handler;

import java.util.function.Consumer;

import com.igrium.replayfps.game_events.UpdateFoodEvent;
import com.igrium.replayfps.networking.FakePacketHandler;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

public class UpdateFoodFakePacket implements FakePacketHandler<UpdateFoodValue> {

    @Override
    public void registerListener(Consumer<UpdateFoodValue> consumer) {
        UpdateFoodEvent.EVENT.register((player, food, saturation) -> {
            consumer.accept(new UpdateFoodValue(food, saturation));
        });
    }

    @Override
    public void write(UpdateFoodValue value, PacketByteBuf buf) {
        buf.writeInt(value.foodLevel());
        buf.writeFloat(value.saturationLevel());
    }

    @Override
    public UpdateFoodValue parse(PacketByteBuf buf) {
        int foodLevel = buf.readInt();
        float saturationLevel = buf.readFloat();
        return new UpdateFoodValue(foodLevel, saturationLevel);
    }

    @Override
    public void apply(UpdateFoodValue value, MinecraftClient client, PlayerEntity player) {
        player.getHungerManager().setFoodLevel(value.foodLevel());
        player.getHungerManager().setSaturationLevel(value.saturationLevel());
    }


}

record UpdateFoodValue(int foodLevel, float saturationLevel) {}