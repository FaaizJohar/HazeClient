package com.cavrix.hazecore.hud;

public abstract class HudElement {
    private String name;
    private float x, y, width, height;
    private float scale = 1.0f;
    private boolean visible = true;

    public HudElement(String name, float defaultX, float defaultY, float defaultWidth, float defaultHeight) {
        this.name = name;
        this.x = defaultX;
        this.y = defaultY;
        this.width = defaultWidth;
        this.height = defaultHeight;
    }

    public abstract void render(float partialTicks);

    // Getters and Setters for drag-and-drop
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }

    public float getY() { return y; }
    public void setY(float y) { this.y = y; }

    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public float getScale() { return scale; }
    public void setScale(float scale) { this.scale = scale; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    
    public String getName() { return name; }
}
