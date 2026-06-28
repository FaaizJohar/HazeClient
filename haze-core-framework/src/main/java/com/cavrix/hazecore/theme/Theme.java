package com.cavrix.hazecore.theme;

public class Theme {
    private String name;
    
    // Core palette
    private int primaryColor;
    private int secondaryColor;
    private int accentColor;
    
    // Backgrounds
    private int backgroundPrimary;
    private int backgroundSecondary;
    
    // Typography
    private int textPrimary;
    private int textSecondary;
    private int textMuted;

    public Theme(String name) {
        this.name = name;
        // Default "Dark Mode" values
        this.primaryColor = 0xFF5B6EE1; // Haze Blue
        this.secondaryColor = 0xFF36454F; 
        this.accentColor = 0xFF8892F1;
        this.backgroundPrimary = 0xFF0D0D0D; // Deep dark
        this.backgroundSecondary = 0xFF1A1A1A; // Slightly lighter
        this.textPrimary = 0xFFFFFFFF;
        this.textSecondary = 0xFFB3B3B3;
        this.textMuted = 0xFF666666;
    }

    // Getters and Setters omitted for brevity
    public String getName() { return name; }
    public int getPrimaryColor() { return primaryColor; }
    public int getBackgroundPrimary() { return backgroundPrimary; }
    public int getTextPrimary() { return textPrimary; }
}
