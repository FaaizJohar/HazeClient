package com.hazeclient.modules.impl;

import com.hazeclient.modules.Module;
import com.hazeclient.render.HazeAnimation;
import com.hazeclient.render.HazeRenderer;
import com.hazeclient.render.HazeTheme;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class KeystrokesModule extends Module {

    private static class KeyInfo {
        int x, y, w, h;
        String name;
        int keyBinding;
        HazeAnimation anim = new HazeAnimation(0f);
        
        KeyInfo(String name, int kb, int x, int y, int w, int h) {
            this.name = name;
            this.keyBinding = kb;
            this.x = x; this.y = y; this.w = w; this.h = h;
        }
    }

    private final KeyInfo[] keys;

    public KeystrokesModule() {
        super("Keystrokes", "Visual WASD + Space key display", Category.HUD);
        setX(10);
        setY(150);
        setWidth(60);
        setHeight(80);
        setShowBg(false);
        
        int gap = 2;
        int size = 20;
        
        keys = new KeyInfo[] {
            new KeyInfo("W", GLFW.GLFW_KEY_W, size + gap, 0, size, size),
            new KeyInfo("A", GLFW.GLFW_KEY_A, 0, size + gap, size, size),
            new KeyInfo("S", GLFW.GLFW_KEY_S, size + gap, size + gap, size, size),
            new KeyInfo("D", GLFW.GLFW_KEY_D, (size + gap) * 2, size + gap, size, size),
            new KeyInfo("LMB", GLFW.GLFW_MOUSE_BUTTON_LEFT, 0, (size + gap) * 2, (size * 3 + gap * 2) / 2 - 1, size),
            new KeyInfo("RMB", GLFW.GLFW_MOUSE_BUTTON_RIGHT, (size * 3 + gap * 2) / 2 + 1, (size + gap) * 2, (size * 3 + gap * 2) / 2, size),
            new KeyInfo("SPACE", GLFW.GLFW_KEY_SPACE, 0, (size + gap) * 3, size * 3 + gap * 2, size / 2)
        };
        
        setWidth(size * 3 + gap * 2);
        setHeight((size + gap) * 3 + size / 2);
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        HazeTheme theme = HazeTheme.get();
        
        for (KeyInfo k : keys) {
            boolean pressed;
            if (k.keyBinding == GLFW.GLFW_MOUSE_BUTTON_LEFT || k.keyBinding == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                pressed = GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), k.keyBinding) == GLFW.GLFW_PRESS;
            } else {
                pressed = GLFW.glfwGetKey(mc.getWindow().getHandle(), k.keyBinding) == GLFW.GLFW_PRESS;
            }
            
            k.anim.springTo(pressed ? 1f : 0f, 300f, 0.7f);
            k.anim.update();
            float p = k.anim.get();
            
            int bg = HazeRenderer.lerpColor(HazeRenderer.withAlpha(theme.getSurfacePrimary(), 0.5f), theme.getCardOn(), p);
            HazeRenderer.drawRoundedRect(context, k.x, k.y, k.w, k.h, 2, bg);
            
            HazeRenderer.drawCenteredText(context, mc.textRenderer, k.name, k.x + k.w / 2, k.y + (k.h - mc.textRenderer.fontHeight) / 2, theme.getTextPrimary());
        }
    }
}
