package com.cavrix.hazecore.ui;

public abstract class UIComponent {
    protected float x, y, width, height;
    protected boolean visible = true;
    protected boolean hovered = false;
    protected UIComponent parent;

    public UIComponent(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void render(float partialTicks, int mouseX, int mouseY);
    
    public void update(int mouseX, int mouseY) {
        this.hovered = isHovered(mouseX, mouseY);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        return false;
    }

    public boolean mouseReleased(int mouseX, int mouseY, int mouseButton) {
        return false;
    }

    public void keyTyped(char typedChar, int keyCode) {}

    protected boolean isHovered(int mouseX, int mouseY) {
        float absX = getAbsoluteX();
        float absY = getAbsoluteY();
        return mouseX >= absX && mouseX <= absX + width && mouseY >= absY && mouseY <= absY + height;
    }

    public float getAbsoluteX() {
        return parent == null ? x : parent.getAbsoluteX() + x;
    }

    public float getAbsoluteY() {
        return parent == null ? y : parent.getAbsoluteY() + y;
    }

    // Getters / Setters
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public float getWidth() { return width; }
    public void setWidth(float width) { this.width = width; }
    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public void setParent(UIComponent parent) { this.parent = parent; }
}
