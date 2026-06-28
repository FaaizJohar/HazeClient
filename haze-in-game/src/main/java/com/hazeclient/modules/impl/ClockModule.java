package com.hazeclient.modules.impl;

import com.hazeclient.modules.Module;
import net.minecraft.client.gui.DrawContext;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClockModule extends Module {
    private static final DateTimeFormatter FORMATTER_12H = DateTimeFormatter.ofPattern("hh:mm a");

    public ClockModule() {
        super("Clock", "Displays current real-world time", Category.HUD);
        setX(10);
        setY(118);
        setWidth(60);
        setHeight(10);
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        String time = LocalTime.now().format(FORMATTER_12H);
        String text = "Time: " + time;
        context.drawTextWithShadow(mc.textRenderer, text, getX(), getY(), getTextColor());
        setWidth(mc.textRenderer.getWidth(text));
    }
}
