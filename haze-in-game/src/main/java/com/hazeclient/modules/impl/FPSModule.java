package com.hazeclient.modules.impl;

import com.hazeclient.modules.Module;
import com.hazeclient.render.HazeRenderer;
import com.hazeclient.render.HazeTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class FPSModule extends Module {

    public FPSModule() {
        super("FPS", "Displays current frames per second", Category.HUD);
        setX(10);
        setY(10);
        setWidth(50);
        setHeight(18);
        setShowBg(true); // From base module settings
    }

    public void render(DrawContext context, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        String text = "FPS: " + mc.getCurrentFps();
        HazeTheme theme = HazeTheme.get();
        
        int textW = mc.textRenderer.getWidth(text);
        int w = textW + 12;
        int h = 18;
        
        // Render text at centered position relative to 0,0
        HazeRenderer.drawCenteredText(context, mc.textRenderer, text, w / 2, (h - mc.textRenderer.fontHeight) / 2, getTextColor());
        
        // Update width/height for editor bounds
        setWidth(w);
        setHeight(h);
    }
}
