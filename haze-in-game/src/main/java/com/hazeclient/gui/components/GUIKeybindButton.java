package com.hazeclient.gui.components;

import com.hazeclient.modules.Module;
import com.hazeclient.render.HazeRenderer;
import com.hazeclient.render.HazeTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.font.TextRenderer;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.util.InputUtil;

public class GUIKeybindButton extends GUIComponent {

    private final Module module;
    private boolean listening = false;
    private final int BUTTON_WIDTH = 60;
    private final int BUTTON_HEIGHT = 16;

    public GUIKeybindButton(Module module, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.module = module;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hovered = isHovered(mouseX, mouseY);
        
        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        HazeTheme theme = HazeTheme.get();

        int textColor = hovered ? theme.getTextPrimary() : theme.getTextSecondary();
        
        // Name
        HazeRenderer.drawText(context, font, "Keybind", x, y + (height - font.fontHeight) / 2, textColor, 1.0f);
        
        // Button
        int btnX = x + width - BUTTON_WIDTH;
        int btnY = y + (height - BUTTON_HEIGHT) / 2;
        
        String keyName = "NONE";
        if (listening) {
            keyName = "Listening...";
        } else if (module.hasKeybind()) {
            keyName = InputUtil.fromKeyCode(module.getKeybind(), -1).getLocalizedText().getString().toUpperCase();
        }
        
        int bgColor = listening ? theme.getAccentColor() : (hovered ? theme.getSurfaceElevated() : theme.getSurfaceElevated());
        HazeRenderer.drawRoundedRect(context, btnX, btnY, BUTTON_WIDTH, BUTTON_HEIGHT, 4, bgColor);
        HazeRenderer.drawRoundedBorder(context, btnX, btnY, BUTTON_WIDTH, BUTTON_HEIGHT, 4, theme.getCardBorder());
        
        HazeRenderer.drawCenteredText(context, font, keyName, btnX + BUTTON_WIDTH / 2, btnY + (BUTTON_HEIGHT - font.fontHeight) / 2, theme.getTextPrimary());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int btnX = x + width - BUTTON_WIDTH;
        int btnY = y + (height - BUTTON_HEIGHT) / 2;
        
        if (mouseX >= btnX && mouseX <= btnX + BUTTON_WIDTH && mouseY >= btnY && mouseY <= btnY + BUTTON_HEIGHT) {
            if (button == 0) {
                listening = !listening;
                return true;
            } else if (button == 1) { // Right click to clear
                module.setKeybind(-1);
                listening = false;
                return true;
            }
        } else if (listening) {
            listening = false;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (listening) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == GLFW.GLFW_KEY_DELETE) {
                module.setKeybind(-1);
            } else {
                module.setKeybind(keyCode);
            }
            listening = false;
            return true;
        }
        return false;
    }
}
