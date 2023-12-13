package com.igrium.replayfps.core.networking.old;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.igrium.replayfps.core.util.PlaybackUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;

public class PacketRedirectors {

    private static final Map<Class<? extends Packet<?>>, PacketRedirector<?>> REGISTRY = new ConcurrentHashMap<>();

    /**
     * During replay playback, packets get marked for redirect by getting added to
     * this list. I would have used a mixin to add a 'willRedirect' field, but
     * <code>Packet</code> is an interface, so that's out of the question.
     */
    public static final Set<Packet<?>> REDIRECT_QUEUED = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));

    public static boolean shouldRedirect(Packet<?> packet) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity localPlayer = PlaybackUtils.getCurrentPlaybackPlayer();
        return shouldRedirect(packet, localPlayer, client);
    }

    public static boolean shouldRedirect(Packet<?> packet, PlayerEntity localPlayer, MinecraftClient client) {
        PacketRedirector<?> redirector = REGISTRY.get(packet.getClass());
        if (redirector != null && redirector.getPacketClass().isInstance(packet)) {
            return redirector.shouldRedirect(packet, localPlayer, client);
        }
        return false;
    }

    public static void applyRedirect(Packet<?> packet, PlayerEntity localPlayer, MinecraftClient client) {
        PacketRedirector<?> redirector = REGISTRY.get(packet.getClass());
        if (redirector == null)
            return;
        tryHandle(packet, redirector, localPlayer, client);
    }

    private static <T extends Packet<?>> void tryHandle(Packet<?> packet, PacketRedirector<T> redirector,
            PlayerEntity localPlayer, MinecraftClient client) {
        redirector.redirect(redirector.getPacketClass().cast(packet), localPlayer, client);
    }

    public static void register(PacketRedirector<?> redirector) {
        REGISTRY.put(redirector.getPacketClass(), redirector);
    }

    
}
