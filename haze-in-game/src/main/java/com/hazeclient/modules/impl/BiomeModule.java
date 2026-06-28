package com.hazeclient.modules.impl;

import com.hazeclient.modules.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;

public class BiomeModule extends Module {

    public BiomeModule() {
        super("Biome", "Displays the current biome the player is standing in", Category.HUD);
        setX(10);
        setY(130);
        setWidth(100);
        setHeight(10);
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        if (mc.player == null || mc.world == null) return;

        String biomeName = mc.world.getBiome(mc.player.getBlockPos())
                .getKey()
                .map(key -> key.getValue().getPath().replace('_', ' '))
                .orElse("Unknown");

        // Capitalize first letter of each word
        StringBuilder sb = new StringBuilder();
        for (String word : biomeName.split(" ")) {
            if (!sb.isEmpty()) sb.append(' ');
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)));
                sb.append(word.substring(1));
            }
        }

        String text = "Biome: " + sb;
        context.drawTextWithShadow(mc.textRenderer, text, getX(), getY(), getTextColor());
        setWidth(mc.textRenderer.getWidth(text));
    }
}
