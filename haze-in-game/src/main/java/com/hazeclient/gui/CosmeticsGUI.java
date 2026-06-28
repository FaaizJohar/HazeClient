package com.hazeclient.gui;

import com.hazeclient.render.HazeRenderer;
import com.hazeclient.render.HazeTheme;
import com.hazeclient.render.cosmetics.CosmeticsManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CosmeticsGUI extends Screen {

    private final Screen parent;
    private float playerSpin = 0f;

    public CosmeticsGUI(Screen parent) {
        super(Text.literal("Cosmetics Wardrobe"));
        this.parent = parent;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw background
        HazeRenderer.drawGradientRect(context, 0, 0, width, height, 0xFF000000, 0xFF1A1A1A);
        
        HazeTheme theme = HazeTheme.get();
        
        // Header
        HazeRenderer.drawCenteredScaledText(context, textRenderer, "Cosmetics Wardrobe", width / 2, 20, theme.getTextPrimary(), 2.0f);
        
        // Render 3D Player Model
        if (client.player != null) {
            playerSpin += delta * 2.0f;
            int renderX = width / 2;
            int renderY = height / 2 + 80;
            int scale = 80;
            InventoryScreen.drawEntity(context, renderX, renderY, scale, 0, -playerSpin, client.player);
        } else {
            HazeRenderer.drawCenteredText(context, textRenderer, "Player model not available in main menu", width / 2, height / 2, theme.getTextMuted());
            HazeRenderer.drawCenteredText(context, textRenderer, "(Join a world to preview cosmetics)", width / 2, height / 2 + 15, theme.getTextMuted());
        }

        // Toggles Sidebar
        int panelW = 200;
        int panelX = 30;
        int panelY = 80;
        
        HazeRenderer.drawRoundedRect(context, panelX, panelY, panelW, 200, 6, theme.getSurfaceElevated());
        HazeRenderer.drawRoundedBorder(context, panelX, panelY, panelW, 200, 6, theme.getCardBorder());
        
        CosmeticsManager.PlayerCosmetics pc = null;
        if (client.player != null) {
            CosmeticsManager.loadPlayer(client.player.getUuid());
            pc = CosmeticsManager.getCosmetics(client.player);
        }
        
        int y = panelY + 20;
        drawToggle(context, "Cape", pc != null && pc.capeTexture != null, panelX + 15, y, mouseX, mouseY, theme); y += 40;
        drawToggle(context, "Dragon Wings", pc != null && pc.wingsTexture != null, panelX + 15, y, mouseX, mouseY, theme); y += 40;
        drawToggle(context, "Glowing Halo", pc != null && pc.hasHalo, panelX + 15, y, mouseX, mouseY, theme);
        
        // Back button
        drawButton(context, "Back", width / 2 - 50, height - 40, 100, 30, mouseX, mouseY, theme);

        super.render(context, mouseX, mouseY, delta);
    }
    
    private void drawToggle(DrawContext ctx, String name, boolean state, int x, int y, int mx, int my, HazeTheme theme) {
        HazeRenderer.drawText(ctx, textRenderer, name, x, y + 6, theme.getTextPrimary(), 1.2f);
        
        int boxX = x + 140;
        int boxY = y + 4;
        boolean hover = mx >= boxX && mx <= boxX + 30 && my >= boxY && my <= boxY + 16;
        
        int color = state ? theme.getAccentColor() : theme.getSurfacePrimary();
        HazeRenderer.drawRoundedRect(ctx, boxX, boxY, 30, 16, 8, color);
        
        // knob
        int knobX = state ? boxX + 16 : boxX + 2;
        HazeRenderer.drawCircle(ctx, knobX + 6, boxY + 8, 6, 0xFFFFFFFF);
    }
    
    private void drawButton(DrawContext ctx, String text, int x, int y, int w, int h, int mx, int my, HazeTheme theme) {
        boolean hover = mx >= x && mx <= x + w && my >= y && my <= y + h;
        
        int bgCol = hover ? theme.getSurfaceElevated() : theme.getSurfaceElevated();
        HazeRenderer.drawRoundedRect(ctx, x, y, w, h, 6, HazeRenderer.withAlpha(bgCol, 0.85f));
        HazeRenderer.drawRoundedBorder(ctx, x, y, w, h, 6, hover ? theme.getAccentColor() : theme.getCardBorder());
        
        int textCol = hover ? theme.getAccentColor() : theme.getTextPrimary();
        HazeRenderer.drawCenteredText(ctx, textRenderer, text, x + w/2, y + (h - textRenderer.fontHeight)/2, textCol);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;
        
        if (client.player != null) {
            CosmeticsManager.PlayerCosmetics pc = CosmeticsManager.getCosmetics(client.player);
            if (pc != null) {
                int panelX = 30;
                int y = 80 + 20;
                
                // Cape
                if (checkToggleClick(mouseX, mouseY, panelX + 155, y + 4)) {
                    pc.capeTexture = (pc.capeTexture == null) ? new Identifier("hazeclient", "textures/cosmetics/cape_default.png") : null;
                    return true;
                } y += 40;
                
                // Wings
                if (checkToggleClick(mouseX, mouseY, panelX + 155, y + 4)) {
                    pc.wingsTexture = (pc.wingsTexture == null) ? new Identifier("hazeclient", "textures/cosmetics/wings_default.png") : null;
                    return true;
                } y += 40;
                
                // Halo
                if (checkToggleClick(mouseX, mouseY, panelX + 155, y + 4)) {
                    pc.hasHalo = !pc.hasHalo;
                    return true;
                }
            }
        }
        
        if (mouseX >= width / 2 - 50 && mouseX <= width / 2 + 50 && mouseY >= height - 40 && mouseY <= height - 10) {
            client.setScreen(parent);
            return true;
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    private boolean checkToggleClick(double mx, double my, int boxX, int boxY) {
        return mx >= boxX - 15 && mx <= boxX + 15 && my >= boxY && my <= boxY + 16;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
