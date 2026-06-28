package com.hazeclient.render.cosmetics;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages player cosmetics (Capes, Wings, Halos, Bandanas).
 * In a real implementation, this would fetch from a backend API.
 */
public class CosmeticsManager {
    
    private static final Map<UUID, PlayerCosmetics> cosmeticsMap = new HashMap<>();

    public static class PlayerCosmetics {
        public Identifier capeTexture;
        public Identifier wingsTexture;
        public boolean hasHalo;
        public int haloColor = 0xFFFFFF;
    }

    public static void loadPlayer(UUID uuid) {
        // Stub: In real life, fetch from api.haze.net/cosmetics/{uuid}
        if (!cosmeticsMap.containsKey(uuid)) {
            PlayerCosmetics pc = new PlayerCosmetics();
            // Just for testing, give everyone a stub cape
            pc.capeTexture = new Identifier("hazeclient", "textures/cosmetics/cape_default.png");
            cosmeticsMap.put(uuid, pc);
        }
    }

    public static PlayerCosmetics getCosmetics(AbstractClientPlayerEntity player) {
        return cosmeticsMap.get(player.getUuid());
    }
}
