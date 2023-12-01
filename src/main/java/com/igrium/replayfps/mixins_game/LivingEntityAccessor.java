package com.igrium.replayfps.mixins_game;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {

    @Accessor("lastDamageTaken")
    public float getLastDamageTaken();

    @Accessor("lastDamageTaken")
    public void setLastDamageTaken(float lastDamageTaken);
}
