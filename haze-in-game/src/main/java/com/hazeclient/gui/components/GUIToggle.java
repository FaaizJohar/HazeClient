package com.hazeclient.gui.components;

import com.hazeclient.modules.settings.BooleanSetting;
import com.hazeclient.render.HazeAnimation;
import com.hazeclient.render.HazeColors;
import com.hazeclient.render.HazeRenderer;
import com.hazeclient.render.HazeTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.font.TextRenderer;

public class GUIToggle extends GUIComponent {

    private final BooleanSetting setting;
    private final HazeAnimation toggleAnim;

    public GUIToggle(BooleanSetting setting, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.setting = setting;
        this.toggleAnim = new HazeAnimation(setting.get() ? 1f : 0f);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hovered = isHovered(mouseX, mouseY);
        
        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        HazeTheme theme = HazeTheme.get();

        // Draw name
        int textColor = hovered ? theme.getTextPrimary() : theme.getTextSecondary();
        HazeRenderer.drawText(context, font, setting.getName(), x, y + (height - font.fontHeight) / 2, textColor, 1.0f);

        // Update animation
        toggleAnim.springTo(setting.get() ? 1f : 0f);
        toggleAnim.update();

        // Draw toggle switch
        int toggleWidth = 32;
        int toggleHeight = 16;
        int toggleX = x + width - toggleWidth;
        int toggleY = y + (height - toggleHeight) / 2;

        HazeRenderer.drawToggle(context, toggleX, toggleY, toggleWidth, toggleHeight, toggleAnim.get(), 
                theme.getToggleTrackOff(), theme.getToggleTrackOn(), theme.getToggleKnob());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hovered && button == 0) {
            setting.set(!setting.get());
            return true;
        }
        return false;
    }
}
