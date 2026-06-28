package com.hazeclient.gui.components;

import com.hazeclient.modules.settings.EnumSetting;
import com.hazeclient.render.HazeRenderer;
import com.hazeclient.render.HazeTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.font.TextRenderer;

public class GUIDropdown extends GUIComponent {

    private final EnumSetting<?> setting;
    private boolean expanded = false;
    private final int ITEM_HEIGHT = 14;

    public GUIDropdown(EnumSetting<?> setting, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.setting = setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hovered = isHovered(mouseX, mouseY);
        
        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        HazeTheme theme = HazeTheme.get();

        int textColor = hovered || expanded ? theme.getTextPrimary() : theme.getTextSecondary();
        
        // Name
        HazeRenderer.drawText(context, font, setting.getName(), x, y + (height - font.fontHeight) / 2, textColor, 1.0f);
        
        // Value Box
        int boxW = 80;
        int boxX = x + width - boxW;
        int boxY = y + (height - ITEM_HEIGHT) / 2;
        
        HazeRenderer.drawRoundedRect(context, boxX, boxY, boxW, ITEM_HEIGHT, 4, theme.getSurfaceElevated());
        HazeRenderer.drawRoundedBorder(context, boxX, boxY, boxW, ITEM_HEIGHT, 4, expanded ? theme.getAccentColor() : theme.getCardBorder());
        HazeRenderer.drawCenteredText(context, font, setting.get().name(), boxX + boxW / 2, boxY + (ITEM_HEIGHT - font.fontHeight) / 2, theme.getTextPrimary());

        // Expanded items
        if (expanded) {
            Enum<?>[] values = setting.getValues();
            int listY = boxY + ITEM_HEIGHT + 2;
            int listH = values.length * ITEM_HEIGHT;
            
            HazeRenderer.drawRoundedRect(context, boxX, listY, boxW, listH, 4, theme.getSurfaceElevated());
            HazeRenderer.drawRoundedBorder(context, boxX, listY, boxW, listH, 4, theme.getCardBorder());
            
            for (int i = 0; i < values.length; i++) {
                int itemY = listY + i * ITEM_HEIGHT;
                boolean itemHovered = mouseX >= boxX && mouseX <= boxX + boxW && mouseY >= itemY && mouseY <= itemY + ITEM_HEIGHT;
                
                if (itemHovered) {
                    HazeRenderer.drawRoundedRect(context, boxX + 1, itemY, boxW - 2, ITEM_HEIGHT, 2, theme.getSurfaceElevated());
                }
                
                int itemColor = (values[i] == setting.get()) ? theme.getAccentColor() : theme.getTextPrimary();
                HazeRenderer.drawCenteredText(context, font, values[i].name(), boxX + boxW / 2, itemY + (ITEM_HEIGHT - font.fontHeight) / 2, itemColor);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;
        
        int boxW = 80;
        int boxX = x + width - boxW;
        int boxY = y + (height - ITEM_HEIGHT) / 2;
        
        // Clicked header
        if (mouseX >= boxX && mouseX <= boxX + boxW && mouseY >= boxY && mouseY <= boxY + ITEM_HEIGHT) {
            expanded = !expanded;
            return true;
        }
        
        // Clicked item
        if (expanded) {
            Enum<?>[] values = setting.getValues();
            int listY = boxY + ITEM_HEIGHT + 2;
            int listH = values.length * ITEM_HEIGHT;
            
            if (mouseX >= boxX && mouseX <= boxX + boxW && mouseY >= listY && mouseY <= listY + listH) {
                int index = (int)((mouseY - listY) / ITEM_HEIGHT);
                if (index >= 0 && index < values.length) {
                    setting.setIndex(index);
                    expanded = false;
                    return true;
                }
            } else {
                // Clicked outside, close
                expanded = false;
            }
        }
        
        return false;
    }

    public int getExpandedHeight() {
        return expanded ? height + setting.getValues().length * ITEM_HEIGHT + 2 : height;
    }
}
