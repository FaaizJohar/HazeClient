package com.hazeclient.gui;

import com.hazeclient.render.HazeAnimation;
import com.hazeclient.render.HazeRenderer;
import com.hazeclient.render.HazeTheme;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class SocialOverlay extends Screen {

    private final HazeAnimation openAnim = new HazeAnimation(0f);
    private final Screen parent;
    
    private final int WIDTH = 300;
    private final int HEIGHT = 400;

    public SocialOverlay(Screen parent) {
        super(Text.literal("Haze Social"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        openAnim.set(0f);
        openAnim.springTo(1f, 200f, 0.85f);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (parent != null) {
            parent.render(context, -1, -1, delta);
        }
        
        openAnim.update();
        float progress = openAnim.get();
        HazeTheme theme = HazeTheme.get();
        
        // Dim background
        HazeRenderer.drawRect(context, 0, 0, width, height, HazeRenderer.withAlpha(0x000000, 0.5f * progress));

        context.getMatrices().push();
        // Scale from center
        context.getMatrices().translate(width / 2f * (1 - progress), height / 2f * (1 - progress), 0);
        context.getMatrices().scale(progress, progress, 1f);
        
        int x = (width - WIDTH) / 2;
        int y = (height - HEIGHT) / 2;
        
        // Window Background
        HazeRenderer.drawShadow(context, x, y, WIDTH, HEIGHT, 8, 0x000000, 16, 0.5f);
        HazeRenderer.drawRoundedRect(context, x, y, WIDTH, HEIGHT, 8, theme.getSurfaceBase());
        HazeRenderer.drawRoundedBorder(context, x, y, WIDTH, HEIGHT, 8, theme.getCardBorder());
        
        // Header
        HazeRenderer.drawRoundedRect(context, x, y, WIDTH, 40, 8, theme.getSurfaceElevated());
        HazeRenderer.drawHorizontalSeparator(context, x, y + 40, WIDTH, theme.getCardBorder());
        HazeRenderer.drawText(context, textRenderer, "Friends List", x + 16, y + 16, theme.getTextPrimary(), 1.2f);
        
        // Content Stub
        HazeRenderer.drawCenteredText(context, textRenderer, "Social features coming soon...", width/2, y + HEIGHT/2, theme.getTextSecondary());

        context.getMatrices().pop();
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}
