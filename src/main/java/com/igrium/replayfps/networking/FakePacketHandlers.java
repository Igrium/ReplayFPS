package com.igrium.replayfps.networking;

import com.igrium.replayfps.networking.handler.UpdateFoodFakePacket;

import net.minecraft.util.Identifier;


public class FakePacketHandlers {
    public static void register() {
        // registerVanillaWithType(HealthUpdateS2CPacket.class, (packet, l, player) -> {
        //     player.getHungerManager().setFoodLevel(packet.getFood());
        //     player.getHungerManager().setSaturationLevel(packet.getSaturation());
        //     return false;
        // });

        // // FOOD
        // UpdateFoodEvent.EVENT.register((player, food, saturation) -> {
        //     PacketByteBuf buf = PacketByteBufs.create();
        //     buf.writeInt(food);
        //     buf.writeFloat(saturation);

        //     CustomReplayPacketManager.sendReplayPacket(new Identifier("replayfps:update_food"), buf);
        // });

        // CustomReplayPacketManager.registerReceiver(new Identifier("replayfps:update_food"), (client, buf, player) -> {
        //     int foodLevel = buf.readInt();
        //     float saturationLevel = buf.readFloat();
        //     client.execute(() -> {
        //         player.getHungerManager().setFoodLevel(foodLevel);
        //         player.getHungerManager().setSaturationLevel(saturationLevel);
        //     });
        // });

        // // XP
        // SetExperienceEvent.EVENT.register((progress, total, level, player) -> {
        //     PacketByteBuf buf = PacketByteBufs.create();
        //     buf.writeFloat(progress);
        //     buf.writeInt(total);
        //     buf.writeInt(level);

        //     CustomReplayPacketManager.sendReplayPacket(new Identifier("replayfps:set_xp"), buf);
        // });

        // CustomReplayPacketManager.registerReceiver(new Identifier("replayfps:update_xp"), (client, buf, player) -> {
        //     float progress = buf.readFloat();
        //     int total = buf.readInt();
        //     int level = buf.readInt();
        //     client.execute(() -> {
        //         player.experienceProgress = progress;
        //         player.totalExperience = total;
        //         player.experienceLevel = level;
        //     });
        // });

        register(new Identifier("replayfps:update_food"), new UpdateFoodFakePacket());
    }

    public static void register(Identifier id, FakePacketHandler<?> handler) {
        FakePacketHandlerWrapper<?> wrapper = new FakePacketHandlerWrapper<>(handler, id);
        CustomReplayPacketManager.registerReceiver(id, wrapper);
    }
}
