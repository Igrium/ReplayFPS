package com.igrium.replayfps.game.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.replayfps.game.event.InventoryModifiedEvent;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {

    @Shadow
    public abstract ItemStack getStack(int slot);
    
    @Inject(method = "setStack", at = @At("HEAD"))
    void replayfps$onSetStack(int slot, ItemStack stack, CallbackInfo ci) {
        InventoryModifiedEvent.EVENT.invoker().onInventoryModified(
            (PlayerInventory) (Object) this, slot, getStack(slot), stack);
    }
}
