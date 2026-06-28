package com.hazeclient.gui.components;

import com.hazeclient.render.HazeRenderer;
import com.hazeclient.render.HazeTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.font.TextRenderer;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class GUISearchBar extends GUIComponent {

    private String text = "";
    private boolean focused = false;
    private long cursorTimer = 0;
    private final Consumer<String> onSearch;
    private final String placeholder;

    public GUISearchBar(int x, int y, int width, int height, String placeholder, Consumer<String> onSearch) {
        super(x, y, width, height);
        this.placeholder = placeholder;
        this.onSearch = onSearch;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hovered = isHovered(mouseX, mouseY);
        
        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        HazeTheme theme = HazeTheme.get();

        // Background
        HazeRenderer.drawRoundedRect(context, x, y, width, height, theme.getCornerRadiusSmall(), theme.getSurfaceElevated());
        HazeRenderer.drawRoundedBorder(context, x, y, width, height, theme.getCornerRadiusSmall(), focused ? theme.getAccentColor() : theme.getCardBorder());
        
        // Search Icon
        HazeRenderer.drawSearchIcon(context, x + 6, y + (height - 10) / 2, 10, theme.getTextSecondary());

        // Text
        int textX = x + 22;
        int textY = y + (height - font.fontHeight) / 2;
        
        if (text.isEmpty() && !focused) {
            HazeRenderer.drawText(context, font, placeholder, textX, textY, theme.getTextMuted(), 1.0f);
        } else {
            boolean showCursor = focused && (System.currentTimeMillis() - cursorTimer) % 1000 < 500;
            String displayText = text + (showCursor ? "_" : "");
            
            // Basic clipping
            int maxW = width - 30;
            String renderText = text;
            while (font.getWidth(renderText + (showCursor ? "_" : "")) > maxW && renderText.length() > 0) {
                renderText = renderText.substring(1);
            }
            displayText = renderText + (showCursor ? "_" : "");
            
            HazeRenderer.drawText(context, font, displayText, textX, textY, theme.getTextPrimary(), 1.0f);
        }
        
        // Clear Icon (if text isn't empty)
        if (!text.isEmpty()) {
            boolean clearHovered = mouseX >= x + width - 18 && mouseX <= x + width - 6 && mouseY >= y + (height - 10) / 2 && mouseY <= y + (height + 10) / 2;
            HazeRenderer.drawCloseIcon(context, x + width - 16, y + (height - 10) / 2, 10, clearHovered ? theme.getTextPrimary() : theme.getTextSecondary());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!hovered) {
            focused = false;
            return false;
        }
        
        if (button == 0) {
            // Check clear button
            if (!text.isEmpty() && mouseX >= x + width - 18 && mouseX <= x + width - 6) {
                text = "";
                onSearch.accept(text);
                return true;
            }
            
            focused = true;
            cursorTimer = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!focused) return false;
        
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (!text.isEmpty()) {
                text = text.substring(0, text.length() - 1);
                onSearch.accept(text);
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
        
        text += chr;
        onSearch.accept(text);
        return true;
    }
    
    public void setText(String text) {
        this.text = text;
        onSearch.accept(text);
    }
}
