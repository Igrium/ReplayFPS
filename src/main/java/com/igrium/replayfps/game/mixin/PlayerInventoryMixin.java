package com.igrium.replayfps.game.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.replayfps.game.event.ClientPlayerEvents;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {

    @Shadow
    public abstract ItemStack getStack(int slot);

    @Shadow
    int selectedSlot;
    
    @Unique
    private int prevSelectedSlot = -1;
    

    @Inject(method = "updateItems", at = @At("RETURN"))
    void replayfps$onUpdateItems(CallbackInfo ci) {
        if (selectedSlot != prevSelectedSlot) {
            ClientPlayerEvents.SELECT_SLOT.invoker().onSelectSlot((PlayerInventory) (Object) this, selectedSlot);
        }
        prevSelectedSlot = selectedSlot;
    }
}
