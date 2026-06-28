package com.hazeclient.gui;

public class HUDModule {
    public String name;
    public float x, y;
    public float width, height;
    
    // Advanced Transformations
    public float scale = 1.0f;
    public float rotation = 0.0f;
    public float opacity = 1.0f;
    public int zIndex = 0;
    
    public boolean isVisible = true;

    public HUDModule(String name, float x, float y, float width, float height) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
