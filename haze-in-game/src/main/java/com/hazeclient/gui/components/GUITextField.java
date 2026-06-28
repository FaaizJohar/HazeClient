package com.hazeclient.gui.components;

import com.hazeclient.modules.settings.StringSetting;
import com.hazeclient.render.HazeRenderer;
import com.hazeclient.render.HazeTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.font.TextRenderer;
import org.lwjgl.glfw.GLFW;

public class GUITextField extends GUIComponent {

    private final StringSetting setting;
    private boolean focused = false;
    private final int BOX_HEIGHT = 16;
    private long cursorTimer = 0;

    public GUITextField(StringSetting setting, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.setting = setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hovered = isHovered(mouseX, mouseY);
        
        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        HazeTheme theme = HazeTheme.get();

        int textColor = hovered || focused ? theme.getTextPrimary() : theme.getTextSecondary();
        
        // Name
        HazeRenderer.drawText(context, font, setting.getName(), x, y + (height - font.fontHeight) / 2, textColor, 1.0f);
        
        // Text Box
        int boxW = 100;
        int boxX = x + width - boxW;
        int boxY = y + (height - BOX_HEIGHT) / 2;
        
        HazeRenderer.drawRoundedRect(context, boxX, boxY, boxW, BOX_HEIGHT, 4, theme.getSurfaceElevated());
        HazeRenderer.drawRoundedBorder(context, boxX, boxY, boxW, BOX_HEIGHT, 4, focused ? theme.getAccentColor() : theme.getCardBorder());
        
        // Value
        String text = setting.get();
        
        // Cursor
        boolean showCursor = focused && (System.currentTimeMillis() - cursorTimer) % 1000 < 500;
        String displayText = text + (showCursor ? "_" : "");
        
        // Clipping if too long
        int maxW = boxW - 8;
        while (font.getWidth(displayText) > maxW && text.length() > 0) {
            text = text.substring(1);
            displayText = text + (showCursor ? "_" : "");
        }
        
        HazeRenderer.drawText(context, font, displayText, boxX + 4, boxY + (BOX_HEIGHT - font.fontHeight) / 2, theme.getTextPrimary(), 1.0f);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int boxW = 100;
        int boxX = x + width - boxW;
        int boxY = y + (height - BOX_HEIGHT) / 2;
        
        focused = (mouseX >= boxX && mouseX <= boxX + boxW && mouseY >= boxY && mouseY <= boxY + BOX_HEIGHT);
        if (focused) {
            cursorTimer = System.currentTimeMillis(); // reset blink
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!focused) return false;
        
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            String curr = setting.get();
            if (!curr.isEmpty()) {
                setting.set(curr.substring(0, curr.length() - 1));
            }
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_ESCAPE) {
            focused = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!focused) return false;
        
        String curr = setting.get();
        if (curr.length() < setting.getMaxLength()) {
            setting.set(curr + chr);
        }
        return true;
    }
}
