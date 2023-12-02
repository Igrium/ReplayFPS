package com.igrium.replayfps.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.replayfps.core.networking.event.PacketReceivedEvent;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    
    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static void replayfps$onHandlePacket(Packet<?> packet, PacketListener listener, CallbackInfo ci) {
        if (PacketReceivedEvent.EVENT.invoker().onPacketReceived(packet, listener)) {
            ci.cancel();
        }
    }
}
