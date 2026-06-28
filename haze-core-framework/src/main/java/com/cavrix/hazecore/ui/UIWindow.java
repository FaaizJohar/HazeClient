package com.cavrix.hazecore.ui;

import java.util.ArrayList;
import java.util.List;

public class UIWindow extends UIComponent {
    private String title;
    private final List<UIComponent> children = new ArrayList<>();
    
    // Dragging state
    private boolean dragging = false;
    private float dragX, dragY;

    public UIWindow(String title, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.title = title;
    }

    public void addComponent(UIComponent component) {
        component.setParent(this);
        children.add(component);
    }

    @Override
    public void render(float partialTicks, int mouseX, int mouseY) {
        if (!visible) return;

        // Render window background and title bar here
        
        // Render children
        for (UIComponent child : children) {
            child.render(partialTicks, mouseX, mouseY);
        }
    }

    @Override
    public void update(int mouseX, int mouseY) {
        super.update(mouseX, mouseY);
        
        if (dragging) {
            this.x = mouseX - dragX;
            this.y = mouseY - dragY;
        }

        for (UIComponent child : children) {
            child.update(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!visible) return false;

        // Check children first (top to bottom)
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseClicked(mouseX, mouseY, mouseButton)) {
                return true;
            }
        }

        if (isHovered(mouseX, mouseY) && mouseButton == 0) {
            // Simplified drag check (assume whole window is draggable for now)
            dragging = true;
            dragX = mouseX - x;
            dragY = mouseY - y;
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int mouseButton) {
        dragging = false;
        for (UIComponent child : children) {
            child.mouseReleased(mouseX, mouseY, mouseButton);
        }
        return false;
    }
}
