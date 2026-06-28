package com.hazeclient.modules.impl;

import com.hazeclient.modules.Module;
import com.hazeclient.render.HazeRenderer;
import com.hazeclient.render.HazeTheme;
import net.minecraft.client.gui.DrawContext;

public class MemoryModule extends Module {

    public MemoryModule() {
        super("Memory", "Shows JVM memory usage", Category.HUD);
        setX(10);
        setY(250);
        setWidth(100);
        setHeight(20);
        setShowBg(true);
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        long max = Runtime.getRuntime().maxMemory();
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        long used = total - free;
        
        long usedMb = used / 1024L / 1024L;
        long maxMb = max / 1024L / 1024L;
        
        float percentage = (float) used / (float) max;
        
        String text = "Mem: " + usedMb + "M / " + maxMb + "M";
        
        int textW = mc.textRenderer.getWidth(text);
        int w = Math.max(100, textW + 12);
        int h = 20;
        
        HazeTheme theme = HazeTheme.get();
        
        // Progress bar BG
        HazeRenderer.drawRect(context, 0, h - 2, w, h, theme.getCardBorder());
        
        // Progress bar FG
        int color = percentage > 0.8f ? 0xFFE05050 : theme.getAccentColor();
        HazeRenderer.drawRect(context, 0, h - 2, (int)(w * percentage), h, color);
        
        HazeRenderer.drawCenteredText(context, mc.textRenderer, text, w / 2, (h - 2 - mc.textRenderer.fontHeight) / 2, getTextColor());
        
        setWidth(w);
        setHeight(h);
    }
}
