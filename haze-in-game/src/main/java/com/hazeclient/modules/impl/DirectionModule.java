package com.hazeclient.modules.impl;

import com.hazeclient.modules.Module;
import net.minecraft.client.gui.DrawContext;

/**
 * Displays the cardinal/intercardinal direction the player is facing.
 */
public class DirectionModule extends Module {

    private static final String[] DIRECTIONS = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};

    public DirectionModule() {
        super("Direction", "Shows facing direction (N/S/E/W)", Category.HUD);
        setX(10);
        setY(58);
        setWidth(80);
        setHeight(10);
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        if (mc.player == null) return;

        float yaw = mc.player.getYaw() % 360;
        if (yaw < 0) yaw += 360;

        int idx = Math.round(yaw / 45f) & 7;
        String facing = DIRECTIONS[idx];
        String text = "Facing: " + facing + String.format(" (%.1f°)", yaw);
        context.drawTextWithShadow(mc.textRenderer, text, getX(), getY(), getTextColor());
        setWidth(mc.textRenderer.getWidth(text));
    }
}
