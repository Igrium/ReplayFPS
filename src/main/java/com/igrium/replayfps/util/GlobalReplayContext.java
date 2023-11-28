package com.igrium.replayfps.util;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class GlobalReplayContext {
    /**
     * Because entity positions are updated every client tick (rather than frame),
     * they may be cached here for the next tick.
     */
    public static final Map<Entity, Vec3d> ENTITY_POS_OVERRIDES = new WeakHashMap<>();
}
