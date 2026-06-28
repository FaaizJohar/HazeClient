package com.cavrix.hazecore.theme;

import java.util.HashMap;
import java.util.Map;

public class ThemeManager {
    private final Map<String, Theme> themes = new HashMap<>();
    private Theme currentTheme;

    public ThemeManager() {
        // Register default theme
        Theme defaultTheme = new Theme("Default Haze");
        themes.put(defaultTheme.getName(), defaultTheme);
        this.currentTheme = defaultTheme;
    }

    public void registerTheme(Theme theme) {
        themes.put(theme.getName(), theme);
    }

    public void applyTheme(String name) {
        if (themes.containsKey(name)) {
            this.currentTheme = themes.get(name);
            System.out.println("[HazeCore] Applied theme: " + name);
            // Optionally, post a ThemeChangedEvent to the EventBus here
        }
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }
}
