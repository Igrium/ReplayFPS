package com.igrium.replayfps.game.networking.redirector;

import com.igrium.replayfps.core.networking.old.PacketRedirector;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.PlayerScreenHandler;

public class ScreenHandlerSlotUpdateRedirector implements PacketRedirector<ScreenHandlerSlotUpdateS2CPacket> {

    @Override
    public Class<ScreenHandlerSlotUpdateS2CPacket> getPacketClass() {
        return ScreenHandlerSlotUpdateS2CPacket.class;
    }

    @Override
    public boolean shouldRedirect(ScreenHandlerSlotUpdateS2CPacket packet, PlayerEntity localPlayer,
            MinecraftClient client) {
        return true;
    }

    @Override
    public void redirect(ScreenHandlerSlotUpdateS2CPacket packet, PlayerEntity localPlayer, MinecraftClient client) {
        client.execute(() -> doRedirect(packet, localPlayer, client));
    }
    
    private void doRedirect(ScreenHandlerSlotUpdateS2CPacket packet, PlayerEntity localPlayer, MinecraftClient client) {
        ItemStack itemStack = packet.getStack();
        int slot = packet.getSlot();

        if (packet.getSyncId() == ScreenHandlerSlotUpdateS2CPacket.UPDATE_PLAYER_INVENTORY_SYNC_ID) {
            localPlayer.getInventory().setStack(slot, itemStack);
        } else {
            if (packet.getSyncId() == 0 && PlayerScreenHandler.isInHotbar(slot)) {
                if (!itemStack.isEmpty()) {
                    ItemStack prevStack = localPlayer.playerScreenHandler.getSlot(slot).getStack();
                    if (prevStack.isEmpty() || prevStack.getCount() < itemStack.getCount()) {
                        itemStack.setBobbingAnimationTime(5);
                    }
                }

                localPlayer.playerScreenHandler.setStackInSlot(slot, packet.getRevision(), itemStack);
                
                // TODO: find a cleaner way to do this.
                localPlayer.getInventory().setStack(slot, itemStack);
            }
        }
    }
}
