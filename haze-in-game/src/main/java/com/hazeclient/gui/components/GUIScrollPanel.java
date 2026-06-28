package com.hazeclient.gui.components;

import com.hazeclient.render.HazeAnimation;
import com.hazeclient.render.HazeRenderer;
import com.hazeclient.render.HazeTheme;
import com.hazeclient.render.HazeColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import java.util.ArrayList;
import java.util.List;

public class GUIScrollPanel extends GUIComponent {

    private final List<GUIComponent> children = new ArrayList<>();
    private final HazeAnimation scrollAnim = new HazeAnimation(0f);
    private float scrollTarget = 0f;
    private int contentHeight = 0;
    private int scrollVelocity = 0;

    public GUIScrollPanel(int x, int y, int width, int height) {
        super(x, y, width, height);
        scrollAnim.springTo(0f, 150f, 0.8f);
    }

    public void clear() {
        children.clear();
        contentHeight = 0;
        scrollTarget = 0f;
    }

    public void addComponent(GUIComponent component) {
        children.add(component);
        recalculateHeight();
    }

    public void recalculateHeight() {
        contentHeight = 0;
        for (GUIComponent child : children) {
            contentHeight = Math.max(contentHeight, child.getY() + child.getHeight());
        }
    }
    
    public List<GUIComponent> getChildren() {
        return children;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hovered = isHovered(mouseX, mouseY);
        
        // Smooth scroll physics
        scrollAnim.springTo(scrollTarget, 200f, 0.9f);
        scrollAnim.update();
        float currentScroll = scrollAnim.get();

        double scaleFactor = MinecraftClient.getInstance().getWindow().getScaleFactor();
        HazeRenderer.beginScissor(x, y, width, height, scaleFactor);
        
        // Render children offset by scroll
        for (GUIComponent child : children) {
            // Adjust mouse Y for children to interact correctly
            child.setPosition(child.getX(), child.getY()); // Ensure X is absolute
            
            // Only render if visible
            int childAbsY = y + child.getY() - (int)currentScroll;
            if (childAbsY + child.getHeight() > y && childAbsY < y + height) {
                // Temporarily translate to apply scroll visually
                context.getMatrices().push();
                context.getMatrices().translate(0, -currentScroll, 0);
                child.render(context, mouseX, mouseY + (int)currentScroll, delta);
                context.getMatrices().pop();
            }
        }
        
        HazeRenderer.endScissor();

        // Scrollbar
        int maxScroll = Math.max(0, contentHeight - height);
        if (maxScroll > 0) {
            HazeTheme theme = HazeTheme.get();
            int barW = 4;
            int barX = x + width - barW - 2;
            
            float visibleRatio = (float) height / contentHeight;
            int thumbH = Math.max(20, (int) (height * visibleRatio));
            float scrollProgress = currentScroll / maxScroll;
            int thumbY = y + (int) (scrollProgress * (height - thumbH));
            
            HazeRenderer.drawRoundedRect(context, barX, y, barW, height, 2, HazeColors.SCROLLBAR_TRACK);
            HazeRenderer.drawRoundedRect(context, barX, thumbY, barW, thumbH, 2, HazeColors.SCROLLBAR_THUMB);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!hovered) return false;
        float currentScroll = scrollAnim.get();
        for (GUIComponent child : children) {
            int childAbsY = y + child.getY() - (int)currentScroll;
            if (childAbsY + child.getHeight() > y && childAbsY < y + height) {
                if (child.mouseClicked(mouseX, mouseY + currentScroll, button)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        float currentScroll = scrollAnim.get();
        for (GUIComponent child : children) {
            if (child.mouseReleased(mouseX, mouseY + currentScroll, button)) return true;
        }
        return false;
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        float currentScroll = scrollAnim.get();
        for (GUIComponent child : children) {
            if (child.mouseDragged(mouseX, mouseY + currentScroll, button, deltaX, deltaY)) return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!hovered) return false;
        
        // Pass scroll to children first (e.g. nested scroll, dropdowns)
        float currentScroll = scrollAnim.get();
        for (GUIComponent child : children) {
            if (child.mouseScrolled(mouseX, mouseY + currentScroll, amount)) return true;
        }
        
        // Handle scroll locally
        int maxScroll = Math.max(0, contentHeight - height);
        if (maxScroll > 0) {
            scrollTarget -= amount * 30; // 30px per scroll click
            scrollTarget = Math.max(0, Math.min(scrollTarget, maxScroll));
            return true;
        }
        return false;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (GUIComponent child : children) {
            if (child.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        return false;
    }
    
    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (GUIComponent child : children) {
            if (child.charTyped(chr, modifiers)) return true;
        }
        return false;
    }
}
