package com.hazeclient.gui.components;

import com.hazeclient.modules.Module;
import com.hazeclient.modules.ModuleRegistry;
import com.hazeclient.render.HazeAnimation;
import com.hazeclient.render.HazeRenderer;
import com.hazeclient.render.HazeTheme;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class CategoryPanel extends GUIComponent {
    private final Module.Category category;
    private final List<ModuleComponent> modules = new ArrayList<>();
    private final HazeAnimation heightAnim = new HazeAnimation(0f);
    
    private boolean expanded = true;
    private boolean dragging = false;
    private int dragOffsetX, dragOffsetY;
    
    public final int HEADER_HEIGHT = 24;
    private final int WIDTH = 120;

    public CategoryPanel(Module.Category category, int startX, int startY) {
        super(startX, startY, 120, 24);
        this.category = category;
        this.width = WIDTH;
        
        int yOffset = HEADER_HEIGHT;
        for (Module m : ModuleRegistry.getModulesByCategory(category)) {
            modules.add(new ModuleComponent(m, 0, yOffset, WIDTH));
            yOffset += 18; // base height of module comp
        }
        
        heightAnim.set(yOffset);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Calculate dynamic height
        int contentHeight = HEADER_HEIGHT;
        for (ModuleComponent mc : modules) {
            contentHeight += mc.getHeight(); // takes into account expanded settings
        }
        
        heightAnim.springTo(expanded ? contentHeight : HEADER_HEIGHT, 200f, 0.85f);
        heightAnim.update();
        float currentHeight = heightAnim.get();
        
        this.height = (int) currentHeight;
        
        HazeTheme theme = HazeTheme.get();
        
        // Shadow
        HazeRenderer.drawShadow(context, x, y, width, this.height, theme.getCornerRadius());
        
        // Background
        HazeRenderer.drawRoundedRect(context, x, y, width, this.height, theme.getCornerRadius(), theme.getSurfaceElevated());
        HazeRenderer.drawRoundedBorder(context, x, y, width, this.height, theme.getCornerRadius(), theme.getCardBorder());
        
        // Header
        int headerColor = theme.getSurfacePrimary();
        if (isHoveredHeader(mouseX, mouseY)) {
            headerColor = theme.getSurfaceElevated();
        }
        
        // We draw the header rect (not fully rounded on bottom)
        HazeRenderer.drawRect(context, x + 1, y + 1, x + width - 1, y + HEADER_HEIGHT, headerColor);
        HazeRenderer.drawHorizontalSeparator(context, x, y + HEADER_HEIGHT, width, theme.getCardBorder());
        
        HazeRenderer.drawText(context, net.minecraft.client.MinecraftClient.getInstance().textRenderer, category.displayName, x + 8, y + 8, theme.getTextPrimary(), 1.0f);
        
        // Scissor for expanding/collapsing content
        if (currentHeight > HEADER_HEIGHT + 1) {
            context.enableScissor(x, y + HEADER_HEIGHT, x + width, y + this.height);
            int yOff = HEADER_HEIGHT;
            for (ModuleComponent mc : modules) {
                mc.setPosition(x, y + yOff);
                mc.render(context, mouseX, mouseY, delta);
                yOff += mc.getHeight();
            }
            context.disableScissor();
        }
    }

    private boolean isHoveredHeader(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + HEADER_HEIGHT;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHoveredHeader((int) mouseX, (int) mouseY)) {
            if (button == 0) { // Left click header to drag
                dragging = true;
                dragOffsetX = (int) mouseX - x;
                dragOffsetY = (int) mouseY - y;
                return true;
            } else if (button == 1) { // Right click header to toggle expand
                expanded = !expanded;
                return true;
            }
        }
        
        if (expanded && mouseX >= x && mouseX <= x + width && mouseY >= y + HEADER_HEIGHT && mouseY <= y + height) {
            for (ModuleComponent mc : modules) {
                if (mc.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            dragging = false;
        }
        
        if (expanded) {
            for (ModuleComponent mc : modules) {
                mc.mouseReleased(mouseX, mouseY, button);
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging) {
            this.x = (int) mouseX - dragOffsetX;
            this.y = (int) mouseY - dragOffsetY;
            return true;
        }
        
        if (expanded) {
            for (ModuleComponent mc : modules) {
                if (mc.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (expanded) {
            for (ModuleComponent mc : modules) {
                if (mc.keyPressed(keyCode, scanCode, modifiers)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (expanded) {
            for (ModuleComponent mc : modules) {
                if (mc.charTyped(chr, modifiers)) return true;
            }
        }
        return false;
    }
}
