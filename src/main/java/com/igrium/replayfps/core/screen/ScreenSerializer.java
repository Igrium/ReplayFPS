package com.igrium.replayfps.core.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketByteBuf;

/**
 * Allows a screen's state to be saved and restored during a replay.
 */
public interface ScreenSerializer<S extends Screen, T> {

    /**
     * The class of the screen this represents.
     */
    public Class<S> getScreenType();

    public Class<T> getSerializedType();

    /**
     * Read a serialized screen object from a buffer.
     * @param buffer Buffer to read from.
     * @return Serialized screen object.
     */
    public T readBuffer(PacketByteBuf buffer) throws Exception;

    /**
     * Write a serialized screen object to a buffer.
     * @param value Serialized screen object.
     * @param buffer Buffer to write to.
     */
    public void writeBuffer(T value, PacketByteBuf buffer);

    /**
     * Serialize a screen's current state.
     * @param screen Screen to serialize.
     * @return Serialized screen object.
     */
    public T serialize(S screen);

    /**
     * Apply a serialized screen state to a screen.
     * @param client The local client.
     * @param value Serialized screen state.
     * @param screen Screen to apply to.
     */
    public void apply(MinecraftClient client, T value, S screen);
    
    /**
     * Create a screen of this type from an initial value.
     * @param client The local client.
     * @param value Initial serialized screen object.
     * @return New screen.
     * @throws Exception If the screen cannot be created.
     */
    public S create(MinecraftClient client, T value) throws Exception;

    /**
     * Check if a screen has changed since its last serialization.
     * @param screen Screen to check.
     * @param value Last serialized object.
     * @return Whether it has changed.
     */
    public boolean hasChanged(S screen, T value);
}
