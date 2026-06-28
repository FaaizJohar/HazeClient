package com.hazeclient.modules.impl;

import com.hazeclient.modules.Module;
import com.hazeclient.render.HazeRenderer;
import com.hazeclient.render.HazeTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.List;

public class CPSModule extends Module {

    private final List<Long> leftClicks = new ArrayList<>();
    private final List<Long> rightClicks = new ArrayList<>();
    
    private boolean wasLeftPressed = false;
    private boolean wasRightPressed = false;

    public CPSModule() {
        super("CPS", "Clicks per second counter", Category.HUD);
        setX(10);
        setY(30);
        setWidth(60);
        setHeight(18);
        setShowBg(true);
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        long now = System.currentTimeMillis();
        
        // Poll mouse state
        boolean leftPressed = GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean rightPressed = GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        
        if (leftPressed && !wasLeftPressed) leftClicks.add(now);
        if (rightPressed && !wasRightPressed) rightClicks.add(now);
        
        wasLeftPressed = leftPressed;
        wasRightPressed = rightPressed;
        
        // Remove old clicks (older than 1000ms)
        leftClicks.removeIf(t -> now - t > 1000);
        rightClicks.removeIf(t -> now - t > 1000);

        String text = leftClicks.size() + " | " + rightClicks.size() + " CPS";
        
        int textW = mc.textRenderer.getWidth(text);
        int w = textW + 12;
        int h = 18;
        
        HazeRenderer.drawCenteredText(context, mc.textRenderer, text, w / 2, (h - mc.textRenderer.fontHeight) / 2, getTextColor());
        
        setWidth(w);
        setHeight(h);
    }
}
