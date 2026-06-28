package com.hazeclient.modules.impl;

import com.hazeclient.modules.Module;
import com.hazeclient.modules.settings.BooleanSetting;
import com.hazeclient.render.HazeRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class CoordinatesModule extends Module {

    private final BooleanSetting showDecimals;

    public CoordinatesModule() {
        super("Coordinates", "Shows XYZ position", Category.HUD);
        setX(10);
        setY(70);
        setWidth(100);
        setHeight(18);
        setShowBg(true);
        
        showDecimals = addSetting(new BooleanSetting("Show Decimals", "Display decimal precision", false));
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        if (mc.player == null) return;
        
        String format = showDecimals.get() ? "%.1f" : "%.0f";
        String x = String.format(format, mc.player.getX());
        String y = String.format(format, mc.player.getY());
        String z = String.format(format, mc.player.getZ());
        
        String text = String.format("%s, %s, %s", x, y, z);
        
        int textW = mc.textRenderer.getWidth(text);
        int w = textW + 12;
        int h = 18;
        
        HazeRenderer.drawCenteredText(context, mc.textRenderer, text, w / 2, (h - mc.textRenderer.fontHeight) / 2, getTextColor());
        
        setWidth(w);
        setHeight(h);
    }
}
