package com.hazeclient.gui.components;

import com.hazeclient.modules.settings.FloatSetting;
import com.hazeclient.modules.settings.IntSetting;
import com.hazeclient.modules.settings.Setting;
import com.hazeclient.render.HazeRenderer;
import com.hazeclient.render.HazeTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.math.MathHelper;

public class GUISlider extends GUIComponent {

    private final Setting<?> setting;
    private boolean dragging;

    public GUISlider(Setting<?> setting, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.setting = setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hovered = isHovered(mouseX, mouseY);
        
        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        HazeTheme theme = HazeTheme.get();

        int textColor = hovered || dragging ? theme.getTextPrimary() : theme.getTextSecondary();
        
        // Name
        HazeRenderer.drawText(context, font, setting.getName(), x, y, textColor, 1.0f);
        
        // Value
        String valueStr = "";
        float normalized = 0f;
        if (setting instanceof IntSetting iSet) {
            valueStr = String.valueOf(iSet.get());
            normalized = iSet.getNormalized();
        } else if (setting instanceof FloatSetting fSet) {
            valueStr = String.format("%.2f", fSet.get());
            normalized = fSet.getNormalized();
        }
        HazeRenderer.drawRightText(context, font, valueStr, x + width, y, textColor);

        // Slider track
        int trackY = y + font.fontHeight + 4;
        int trackHeight = 4;
        
        if (dragging) {
            updateValueFromMouse(mouseX);
            // Re-fetch normalized after updating
            if (setting instanceof IntSetting iSet) normalized = iSet.getNormalized();
            else if (setting instanceof FloatSetting fSet) normalized = fSet.getNormalized();
        }
        
        HazeRenderer.drawSlider(context, x, trackY, width, trackHeight, normalized, 
            theme.getToggleTrackOff(), theme.getAccentColor(), theme.getTextPrimary());
    }

    private void updateValueFromMouse(int mouseX) {
        float normalized = MathHelper.clamp((float)(mouseX - x) / width, 0f, 1f);
        if (setting instanceof IntSetting iSet) {
            iSet.setNormalized(normalized);
        } else if (setting instanceof FloatSetting fSet) {
            fSet.setNormalized(normalized);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hovered && button == 0) {
            dragging = true;
            updateValueFromMouse((int)mouseX);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging) {
            dragging = false;
            return true;
        }
        return false;
    }
}
