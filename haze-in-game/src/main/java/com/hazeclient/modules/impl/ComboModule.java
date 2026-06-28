package com.hazeclient.modules.impl;

import com.hazeclient.modules.Module;
import com.hazeclient.modules.settings.BooleanSetting;
import com.hazeclient.render.HazeAnimation;
import com.hazeclient.render.HazeRenderer;
import com.hazeclient.render.HazeTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class ComboModule extends Module {

    private final BooleanSetting showComboText;
    
    private int currentCombo = 0;
    private long lastHitTime = 0;
    private final HazeAnimation popAnim = new HazeAnimation(1f);

    public ComboModule() {
        super("Combo", "Displays your current hit combo", Category.HUD);
        showComboText = addSetting(new BooleanSetting("Show 'Combo' Text", "Display the word Combo", true));
        
        setSize(80, 24);
    }
    
    public void onHit() {
        currentCombo++;
        lastHitTime = System.currentTimeMillis();
        popAnim.set(1.5f); // Pop scale up
        popAnim.springTo(1f, 150f, 0.6f);
    }

    @Override
    public void render(DrawContext context, float delta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        boolean inEditor = mc.currentScreen instanceof com.hazeclient.gui.HUDEditor;
        
        // Timeout combo after 3 seconds
        if (!inEditor && System.currentTimeMillis() - lastHitTime > 3000) {
            currentCombo = 0;
        }
        
        int displayCombo = currentCombo;
        if (inEditor && displayCombo == 0) {
            displayCombo = 12; // dummy value for preview
        }
        
        if (displayCombo == 0) {
            setSize(0, 0); // Hide if no combo
            return;
        }

        popAnim.update();
        float scale = popAnim.get();
        HazeTheme theme = HazeTheme.get();
        
        String text = displayCombo + "";
        if (showComboText.get()) {
            text += " Combo";
        }
        
        int w = mc.textRenderer.getWidth(text) + 8;
        setSize(w, 24);
        
        context.getMatrices().push();
        // Scale around center
        context.getMatrices().translate(w/2f * (1 - scale), 12 * (1 - scale), 0);
        context.getMatrices().scale(scale, scale, 1f);
        
        // Color based on combo size
        int color = theme.getTextPrimary();
        if (displayCombo > 20) color = 0xFFFF3333; // Red
        else if (displayCombo > 10) color = 0xFFFFAA00; // Orange
        else if (displayCombo > 5) color = 0xFFFFFF33; // Yellow
        
        // Shadow and text
        HazeRenderer.drawShadow(context, 0, 0, w, 24, 4, color, 4, 0.4f);
        HazeRenderer.drawCenteredText(context, mc.textRenderer, text, w/2, 8, color);
        
        context.getMatrices().pop();
    }
}
