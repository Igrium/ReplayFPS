package com.igrium.replayfps.game.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;

@Mixin(AbstractSignEditScreen.class)
public interface AbstractSignEditScreenAccessor {

    @Accessor("blockEntity")
    public SignBlockEntity getBlockEntity();
    
    @Accessor("messages")
    public String[] getMessages();

    @Accessor("front")
    public boolean isFront();

    @Accessor("currentRow")
    public int getCurrentRow();

    @Accessor("currentRow")
    public void setCurrentRow(int currentRow);
}
