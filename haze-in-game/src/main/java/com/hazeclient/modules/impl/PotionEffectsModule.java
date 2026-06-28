package com.hazeclient.modules.impl;

import com.hazeclient.modules.Module;
import com.hazeclient.modules.settings.BooleanSetting;
import com.hazeclient.render.HazeRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.StringHelper;

import java.util.Collection;

public class PotionEffectsModule extends Module {

    private final BooleanSetting showIcons;

    public PotionEffectsModule() {
        super("Potion Effects", "Displays active potion effects", Category.HUD);
        setX(10);
        setY(120);
        setWidth(100);
        setHeight(20);
        setShowBg(false);
        
        showIcons = addSetting(new BooleanSetting("Show Icons", "Render effect icons", true));
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        if (mc.player == null) return;
        
        Collection<StatusEffectInstance> effects = mc.player.getStatusEffects();
        if (effects.isEmpty() && mc.currentScreen instanceof com.hazeclient.gui.HUDEditor) {
            // Dummy render for editor
            String text = "Speed II 0:30";
            HazeRenderer.drawText(context, mc.textRenderer, text, 0, 0, getTextColor(), 1.0f);
            setWidth(mc.textRenderer.getWidth(text));
            setHeight(mc.textRenderer.fontHeight);
            return;
        }

        int yOff = 0;
        int maxWidth = 0;
        boolean icons = showIcons.get();
        
        for (StatusEffectInstance effect : effects) {
            String name = effect.getEffectType().getName().getString();
            int amp = effect.getAmplifier();
            if (amp > 0) {
                name += " " + (amp + 1);
            }
            String duration = StringHelper.formatTicks(effect.getDuration());
            String text = name + " " + duration;
            
            int color = effect.getEffectType().getColor();
            // Fallback to module text color if we don't want native color, but let's use native
            
            int xOff = 0;
            // Draw Icon (placeholder for actual texture logic)
            if (icons) {
                // context.drawTexture(...) would go here
                xOff = 12;
            }
            
            HazeRenderer.drawText(context, mc.textRenderer, text, xOff, yOff, color, 1.0f);
            
            maxWidth = Math.max(maxWidth, xOff + mc.textRenderer.getWidth(text));
            yOff += mc.textRenderer.fontHeight + 2;
        }
        
        setWidth(Math.max(50, maxWidth));
        setHeight(Math.max(10, yOff));
    }
}
