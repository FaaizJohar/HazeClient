package com.hazeclient.gui.components;

import com.hazeclient.modules.settings.ColorSetting;
import com.hazeclient.render.HazeRenderer;
import com.hazeclient.render.HazeTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.math.MathHelper;
import java.awt.Color;

public class GUIColorPicker extends GUIComponent {

    private final ColorSetting setting;
    private boolean expanded = false;
    private boolean draggingHue = false;
    private boolean draggingSV = false;
    
    private final int PICKER_SIZE = 80;
    private final int HUE_WIDTH = 12;

    public GUIColorPicker(ColorSetting setting, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.setting = setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hovered = isHovered(mouseX, mouseY);
        
        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        HazeTheme theme = HazeTheme.get();

        int textColor = hovered ? theme.getTextPrimary() : theme.getTextSecondary();
        
        // Name
        HazeRenderer.drawText(context, font, setting.getName(), x, y + (height - font.fontHeight)/2, textColor, 1.0f);
        
        // Color preview box
        int boxSize = 16;
        int boxX = x + width - boxSize;
        int boxY = y + (height - boxSize) / 2;
        
        HazeRenderer.drawRoundedRect(context, boxX, boxY, boxSize, boxSize, 4, setting.get());
        HazeRenderer.drawRoundedBorder(context, boxX, boxY, boxSize, boxSize, 4, theme.getCardBorder());

        // Expanded picker
        if (expanded) {
            int pickerX = x;
            int pickerY = y + height + 4;
            int pickerW = PICKER_SIZE + HUE_WIDTH + 4;
            int pickerH = PICKER_SIZE;
            
            // BG
            HazeRenderer.drawRoundedRect(context, pickerX - 4, pickerY - 4, pickerW + 8, pickerH + 8, 4, theme.getSurfaceElevated());
            
            float[] hsb = setting.toHSB();
            
            // Interaction
            if (draggingSV) {
                float s = MathHelper.clamp((float)(mouseX - pickerX) / PICKER_SIZE, 0f, 1f);
                float v = 1f - MathHelper.clamp((float)(mouseY - pickerY) / PICKER_SIZE, 0f, 1f);
                setting.fromHSB(hsb[0], s, v);
            }
            if (draggingHue) {
                float h = 360f * MathHelper.clamp((float)(mouseY - pickerY) / PICKER_SIZE, 0f, 1f);
                setting.fromHSB(h, hsb[1], hsb[2]);
            }
            
            hsb = setting.toHSB(); // update after drag
            
            // Draw SV Area
            int baseColor = Color.HSBtoRGB(hsb[0]/360f, 1f, 1f);
            HazeRenderer.drawGradientRect(context, pickerX, pickerY, pickerX + PICKER_SIZE, pickerY + PICKER_SIZE, 0xFFFFFFFF, baseColor);
            HazeRenderer.drawGradientRect(context, pickerX, pickerY, pickerX + PICKER_SIZE, pickerY + PICKER_SIZE, 0x00000000, 0xFF000000);
            
            // SV Cursor
            int cursorX = pickerX + (int)(hsb[1] * PICKER_SIZE);
            int cursorY = pickerY + (int)((1f - hsb[2]) * PICKER_SIZE);
            HazeRenderer.drawCircle(context, cursorX, cursorY, 3, 0xFFFFFFFF);
            HazeRenderer.drawCircle(context, cursorX, cursorY, 2, 0xFF000000);

            // Draw Hue Slider
            int hueX = pickerX + PICKER_SIZE + 4;
            for (int i = 0; i < PICKER_SIZE; i++) {
                int hueColor = Color.HSBtoRGB(i / (float)PICKER_SIZE, 1f, 1f);
                HazeRenderer.drawRect(context, hueX, pickerY + i, hueX + HUE_WIDTH, pickerY + i + 1, hueColor);
            }
            
            // Hue Cursor
            int hCursorY = pickerY + (int)((hsb[0]/360f) * PICKER_SIZE);
            HazeRenderer.drawRect(context, hueX - 1, hCursorY - 1, hueX + HUE_WIDTH + 1, hCursorY + 1, 0xFFFFFFFF);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;
        
        int boxSize = 16;
        int boxX = x + width - boxSize;
        int boxY = y + (height - boxSize) / 2;
        
        if (mouseX >= boxX && mouseX <= boxX + boxSize && mouseY >= boxY && mouseY <= boxY + boxSize) {
            expanded = !expanded;
            return true;
        }
        
        if (expanded) {
            int pickerX = x;
            int pickerY = y + height + 4;
            
            if (mouseX >= pickerX && mouseX <= pickerX + PICKER_SIZE && mouseY >= pickerY && mouseY <= pickerY + PICKER_SIZE) {
                draggingSV = true;
                return true;
            }
            
            int hueX = pickerX + PICKER_SIZE + 4;
            if (mouseX >= hueX && mouseX <= hueX + HUE_WIDTH && mouseY >= pickerY && mouseY <= pickerY + PICKER_SIZE) {
                draggingHue = true;
                return true;
            }
        }
        
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggingSV || draggingHue) {
            draggingSV = false;
            draggingHue = false;
            return true;
        }
        return false;
    }

    public int getExpandedHeight() {
        return expanded ? height + PICKER_SIZE + 8 : height;
    }
}
