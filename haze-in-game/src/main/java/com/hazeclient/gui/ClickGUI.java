package com.hazeclient.gui;

import com.hazeclient.gui.components.CategoryPanel;
import com.hazeclient.modules.Module;
import com.hazeclient.render.HazeAnimation;
import com.hazeclient.render.HazeRenderer;
import com.hazeclient.render.HazeTheme;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ClickGUI extends Screen {

    private final HazeAnimation openAnim = new HazeAnimation(0f);
    private final List<CategoryPanel> panels = new ArrayList<>();
    
    // Maintain z-index (last element = top)
    private CategoryPanel draggingPanel = null;

    public ClickGUI() {
        super(Text.literal("Haze Client"));
    }

    @Override
    protected void init() {
        super.init();
        openAnim.set(0f);
        openAnim.springTo(1f, 200f, 0.85f);

        if (panels.isEmpty()) {
            int px = 20;
            int py = 20;
            for (Module.Category cat : Module.Category.values()) {
                panels.add(new CategoryPanel(cat, px, py));
                px += 140; // 120 width + 20 gap
                if (px > width - 140) {
                    px = 20;
                    py += 100;
                }
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        openAnim.update();
        float openProgress = openAnim.get();

        HazeTheme theme = HazeTheme.get();
        
        // Background Blur
        HazeRenderer.drawFrostedRect(context, 0, 0, width, height, 0, theme.getSurfaceBase(), openProgress);

        context.getMatrices().push();
        // Intro animation scale/translate
        float scale = 0.95f + 0.05f * openProgress;
        context.getMatrices().translate(width / 2f * (1 - scale), height / 2f * (1 - scale), 0);
        context.getMatrices().scale(scale, scale, 1f);

        // Render panels from bottom to top
        for (CategoryPanel panel : panels) {
            panel.render(context, mouseX, mouseY, delta);
        }

        context.getMatrices().pop();
    }

    private void bringToFront(CategoryPanel p) {
        panels.remove(p);
        panels.add(p);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Reverse iterate to hit top panels first
        for (int i = panels.size() - 1; i >= 0; i--) {
            CategoryPanel p = panels.get(i);
            if (p.mouseClicked(mouseX, mouseY, button)) {
                bringToFront(p);
                // If it's a drag on the header, track it
                // We rely on the panel itself handling the logic, but we bring it to front
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (CategoryPanel p : panels) {
            p.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        // Drag only the active element (top panel typically)
        for (int i = panels.size() - 1; i >= 0; i--) {
            CategoryPanel p = panels.get(i);
            if (p.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (int i = panels.size() - 1; i >= 0; i--) {
            if (panels.get(i).keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            // let super close the screen
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (int i = panels.size() - 1; i >= 0; i--) {
            if (panels.get(i).charTyped(chr, modifiers)) return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
