package com.hazeclient.gui;

import com.hazeclient.render.HazeAnimation;
import com.hazeclient.render.HazeRenderer;
import com.hazeclient.render.HazeTheme;
import com.hazeclient.render.particles.ParticleEngine;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Text;

public class HazeMainMenu extends Screen {

    private final HazeAnimation introAnim = new HazeAnimation(0f);
    private final ParticleEngine particleEngine = new ParticleEngine();
    private final boolean showBackground;

    public HazeMainMenu(boolean showBackground) {
        super(Text.literal("Haze Main Menu"));
        this.showBackground = showBackground;
    }

    @Override
    protected void init() {
        super.init();
        introAnim.set(0f);
        introAnim.springTo(1f, 150f, 0.9f);
        
        if (showBackground) {
            particleEngine.init(width, height);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        introAnim.update();
        float intro = introAnim.get();
        HazeTheme theme = HazeTheme.get();

        if (showBackground) {
            // Draw gradient background
            HazeRenderer.drawGradientRect(context, 0, 0, width, height, 0xFF0D1B2A, 0xFF1B263B);
            
            // Render dynamic particles
            particleEngine.render(context, mouseX, mouseY, delta);
        }

        context.getMatrices().push();
        // Intro scale
        float scale = 0.95f + 0.05f * intro;
        context.getMatrices().translate(width / 2f * (1 - scale), height / 2f * (1 - scale), 0);
        context.getMatrices().scale(scale, scale, 1f);

        // Sidebar / Main Panel
        int panelW = 250;
        int panelX = (width - panelW) / 2;
        int panelY = height / 4;
        
        // Haze Logo
        HazeRenderer.drawCenteredScaledText(context, textRenderer, "HAZE", width / 2, panelY - 50, theme.getTextPrimary(), 4.0f);
        HazeRenderer.drawCenteredText(context, textRenderer, "Premium Client", width / 2, panelY - 5, theme.getAccentColor());
        
        // Buttons
        int by = panelY + 40;
        
        drawMenuButton(context, "Singleplayer", panelX, by, panelW, 30, mouseX, mouseY, theme); by += 35;
        drawMenuButton(context, "Multiplayer", panelX, by, panelW, 30, mouseX, mouseY, theme); by += 35;
        drawMenuButton(context, "Cosmetics", panelX, by, panelW, 30, mouseX, mouseY, theme); by += 35;
        drawMenuButton(context, "Alt Manager", panelX, by, panelW, 30, mouseX, mouseY, theme); by += 35;
        
        by += 15; // gap
        drawMenuButton(context, "Settings", panelX, by, panelW, 30, mouseX, mouseY, theme); by += 35;
        drawMenuButton(context, "Quit Game", panelX, by, panelW, 30, mouseX, mouseY, theme);

        context.getMatrices().pop();

        super.render(context, mouseX, mouseY, delta);
    }
    
    private void drawMenuButton(DrawContext ctx, String text, int x, int y, int w, int h, int mx, int my, HazeTheme theme) {
        boolean hover = mx >= x && mx <= x + w && my >= y && my <= y + h;
        
        int bgCol = hover ? theme.getSurfaceElevated() : theme.getSurfaceElevated();
        HazeRenderer.drawRoundedRect(ctx, x, y, w, h, 6, HazeRenderer.withAlpha(bgCol, 0.85f));
        HazeRenderer.drawRoundedBorder(ctx, x, y, w, h, 6, hover ? theme.getAccentColor() : theme.getCardBorder());
        
        int textCol = hover ? theme.getAccentColor() : theme.getTextPrimary();
        HazeRenderer.drawCenteredText(ctx, textRenderer, text, x + w/2, y + (h - textRenderer.fontHeight)/2, textCol);
        
        if (hover) {
            HazeRenderer.drawShadow(ctx, x, y, w, h, 6, theme.getAccentColor(), 8, 0.5f);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;
        
        int panelW = 250;
        int panelX = (width - panelW) / 2;
        int panelY = height / 4;
        int by = panelY + 40;
        
        if (checkClick(mouseX, mouseY, panelX, by, panelW, 30)) {
            client.setScreen(new SelectWorldScreen(this));
            return true;
        } by += 35;
        
        if (checkClick(mouseX, mouseY, panelX, by, panelW, 30)) {
            client.setScreen(new MultiplayerScreen(this));
            return true;
        } by += 35;
        
        if (checkClick(mouseX, mouseY, panelX, by, panelW, 30)) {
            client.setScreen(new CosmeticsGUI(this));
            return true;
        } by += 35;
        
        if (checkClick(mouseX, mouseY, panelX, by, panelW, 30)) {
            // TODO: Alt Manager Screen
            return true;
        } by += 35;
        
        by += 15;
        
        if (checkClick(mouseX, mouseY, panelX, by, panelW, 30)) {
            client.setScreen(new OptionsScreen(this, client.options));
            return true;
        } by += 35;
        
        if (checkClick(mouseX, mouseY, panelX, by, panelW, 30)) {
            client.scheduleStop();
            return true;
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    private boolean checkClick(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
