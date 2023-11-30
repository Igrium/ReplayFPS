package com.igrium.replayfps.mixins_game;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.replayfps.game_events.UpdateFoodEvent;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;

@Mixin(ClientPlayNetworkHandler.class)
public class NetworkHandlerListenersMixin {

    @Shadow
    private MinecraftClient client;

    @Inject(method = "onHealthUpdate", at = @At("RETURN"))
    void replayfps$onHealthUpdate(HealthUpdateS2CPacket packet, CallbackInfo ci) {
        client.execute(() -> UpdateFoodEvent.EVENT.invoker().onUpdateFood(client.player, packet.getFood(), packet.getSaturation()));
    }
}
