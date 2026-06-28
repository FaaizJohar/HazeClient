package com.hazeclient.gui;

import com.hazeclient.render.HazeAnimation;
import com.hazeclient.render.HazeRenderer;
import com.hazeclient.render.HazeTheme;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ScreenshotViewer extends Screen {

    private final HazeAnimation openAnim = new HazeAnimation(0f);
    
    public ScreenshotViewer() {
        super(Text.literal("Haze Screenshots"));
    }

    @Override
    protected void init() {
        super.init();
        openAnim.set(0f);
        openAnim.springTo(1f, 200f, 0.85f);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        openAnim.update();
        float progress = openAnim.get();
        HazeTheme theme = HazeTheme.get();
        
        HazeRenderer.drawRect(context, 0, 0, width, height, theme.getSurfaceBase());
        
        context.getMatrices().push();
        // Scale from center
        context.getMatrices().translate(width / 2f * (1 - progress), height / 2f * (1 - progress), 0);
        context.getMatrices().scale(progress, progress, 1f);
        
        // Header
        HazeRenderer.drawText(context, textRenderer, "Screenshots", 20, 20, theme.getTextPrimary(), 2.0f);
        
        // Grid Stub
        int cols = 3;
        int gap = 20;
        int cardW = (width - 40 - (cols - 1) * gap) / cols;
        int cardH = (int)(cardW * 9f / 16f); // 16:9 aspect ratio
        
        int x = 20;
        int y = 60;
        for (int i=0; i<6; i++) {
            int cx = x + (i % cols) * (cardW + gap);
            int cy = y + (i / cols) * (cardH + gap);
            
            boolean hover = mouseX >= cx && mouseX <= cx + cardW && mouseY >= cy && mouseY <= cy + cardH;
            
            HazeRenderer.drawRoundedRect(context, cx, cy, cardW, cardH, 8, theme.getSurfaceElevated());
            HazeRenderer.drawRoundedBorder(context, cx, cy, cardW, cardH, 8, hover ? theme.getAccentColor() : theme.getCardBorder());
            
            HazeRenderer.drawCenteredText(context, textRenderer, "Screenshot " + (i+1), cx + cardW/2, cy + cardH/2, theme.getTextSecondary());
        }

        context.getMatrices().pop();
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}
