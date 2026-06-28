package com.hazeclient.modules.impl;

import com.hazeclient.modules.Module;
import com.hazeclient.modules.settings.BooleanSetting;
import com.hazeclient.render.HazeAnimation;
import com.hazeclient.render.HazeRenderer;
import com.hazeclient.render.HazeTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class TargetHUDModule extends Module {

    private final BooleanSetting showArmor;
    private final BooleanSetting rounded;
    
    private final HazeAnimation hpAnim = new HazeAnimation(20f);
    private final HazeAnimation showAnim = new HazeAnimation(0f);
    
    private LivingEntity target;
    private long lastTargetTime;

    public TargetHUDModule() {
        super("Target HUD", "Shows info about the entity you're fighting", Category.HUD);
        showArmor = addSetting(new BooleanSetting("Show Armor", "Render target armor bar", true));
        rounded = addSetting(new BooleanSetting("Rounded", "Use rounded corners", true));
        
        setBgStyle(BackgroundStyle.NONE); // we draw our own background
        setSize(140, 50);
    }

    @Override
    public void render(DrawContext context, float delta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        
        // Find target
        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            EntityHitResult ehr = (EntityHitResult) mc.crosshairTarget;
            if (ehr.getEntity() instanceof LivingEntity le) {
                target = le;
                lastTargetTime = System.currentTimeMillis();
            }
        }
        
        // Timeout after 2 seconds of not looking at them
        boolean hasTarget = target != null && (System.currentTimeMillis() - lastTargetTime < 2000);
        boolean inEditor = mc.currentScreen instanceof com.hazeclient.gui.HUDEditor;
        
        if (inEditor && !hasTarget) {
            // Mock target for editor
            showAnim.springTo(1f, 200f, 0.8f);
        } else {
            showAnim.springTo(hasTarget ? 1f : 0f, 200f, 0.8f);
        }
        showAnim.update();
        
        float progress = showAnim.get();
        if (progress < 0.01f) return;
        
        HazeTheme theme = HazeTheme.get();
        
        int w = 140;
        int h = showArmor.get() ? 50 : 40;
        setSize(w, h);
        
        context.getMatrices().push();
        // Scale from center based on showAnim
        context.getMatrices().translate(w/2f * (1 - progress), h/2f * (1 - progress), 0);
        context.getMatrices().scale(progress, progress, 1f);
        
        // Background
        int bgCol = HazeRenderer.withAlpha(theme.getSurfaceElevated(), 0.8f);
        if (rounded.get()) {
            HazeRenderer.drawRoundedRect(context, 0, 0, w, h, 6, bgCol);
            HazeRenderer.drawRoundedBorder(context, 0, 0, w, h, 6, theme.getCardBorder());
        } else {
            HazeRenderer.drawRect(context, 0, 0, w, h, bgCol);
            HazeRenderer.drawBorder(context, -1, -1, w+2, h+2, theme.getCardBorder());
        }

        // Target Info
        String name = "Entity";
        float hp = 20f;
        float maxHp = 20f;
        
        if (inEditor && !hasTarget) {
            name = "Dummy Player";
            hp = 14f;
        } else if (target != null) {
            name = target.getName().getString();
            hp = target.getHealth();
            maxHp = target.getMaxHealth();
        }
        
        hpAnim.springTo(hp, 150f, 0.8f);
        hpAnim.update();
        float currentHp = hpAnim.get();
        
        // Avatar Box (Left)
        int avSize = 30;
        HazeRenderer.drawRoundedRect(context, 6, 6, avSize, avSize, 4, 0x44FFFFFF);
        // Usually draw entity head here
        
        // Name
        HazeRenderer.drawText(context, mc.textRenderer, name, 42, 8, theme.getTextPrimary(), 1f);
        
        // Health Bar
        int barX = 42;
        int barY = 22;
        int barW = w - 42 - 8;
        int barH = 6;
        
        float hpRatio = Math.min(1f, currentHp / maxHp);
        HazeRenderer.drawRoundedRect(context, barX, barY, barW, barH, 3, theme.getToggleTrackOff());
        HazeRenderer.drawRoundedRect(context, barX, barY, (int)(barW * hpRatio), barH, 3, theme.getAccentColor());
        
        // Health text
        String hpText = String.format("%.1f", currentHp);
        HazeRenderer.drawRightText(context, mc.textRenderer, hpText, w - 8, 8, theme.getAccentColor());
        
        // Armor (Optional)
        if (showArmor.get()) {
            int armY = 32;
            int armW = barW;
            float armorRatio = 0.5f; // stub
            if (target != null) armorRatio = target.getArmor() / 20f;
            else if (inEditor) armorRatio = 0.75f;
            
            HazeRenderer.drawRoundedRect(context, barX, armY, armW, 4, 2, theme.getToggleTrackOff());
            HazeRenderer.drawRoundedRect(context, barX, armY, (int)(armW * armorRatio), 4, 2, 0xFF44AAFF);
        }
        
        context.getMatrices().pop();
    }
}
