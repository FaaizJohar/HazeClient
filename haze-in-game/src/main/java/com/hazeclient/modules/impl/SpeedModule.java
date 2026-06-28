package com.hazeclient.modules.impl;

import com.hazeclient.modules.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;

public class SpeedModule extends Module {

    private Vec3d lastPos;

    public SpeedModule() {
        super("Speed", "Shows horizontal movement speed in blocks/sec (BPS)", Category.HUD);
        setX(10);
        setY(106);
        setWidth(80);
        setHeight(10);
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        if (mc.player == null) return;

        Vec3d currPos = new Vec3d(mc.player.getX(), 0, mc.player.getZ());
        double bps = 0.0;
        
        if (lastPos != null) {
            double dist = currPos.distanceTo(lastPos);
            // distance per tick * 20 ticks = blocks per second
            bps = dist * 20.0;
        }
        lastPos = currPos;

        String text = String.format("Speed: %.2f BPS", bps);
        context.drawTextWithShadow(mc.textRenderer, text, getX(), getY(), getTextColor());
        setWidth(mc.textRenderer.getWidth(text));
    }
}
