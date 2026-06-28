package com.cavrix.hazecore.hud.elements;

import com.cavrix.hazecore.hud.HudElement;
import com.cavrix.hazecore.render.RenderUtils;

public class FpsHudElement extends HudElement {
    
    // In a real implementation, this would be updated by the Minecraft tick loop
    private int currentFps = 144; 

    public FpsHudElement() {
        super("FPS Counter", 10, 10);
    }

    @Override
    public void render(float partialTicks) {
        String text = "FPS: " + currentFps;
        
        // Draw the background for the HUD element
        RenderUtils.drawRoundedRect(this.x, this.y, 60 * scale, 15 * scale, 4f, 0x80000000);
        
        // Draw the text
        RenderUtils.drawString(text, this.x + 4, this.y + 4, 0xFFFFFFFF);
    }

    public void updateFps(int fps) {
        this.currentFps = fps;
    }
}
