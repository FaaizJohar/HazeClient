package com.cavrix.hazecore.config;

import com.cavrix.hazecore.module.ModuleManager;

import java.io.File;

public class ConfigManager {
    private final File configDir;
    private final ModuleManager moduleManager;

    public ConfigManager(File gameDir, ModuleManager moduleManager) {
        this.configDir = new File(gameDir, "haze-client");
        if (!this.configDir.exists()) {
            this.configDir.mkdirs();
        }
        this.moduleManager = moduleManager;
    }

    public void loadConfig() {
        System.out.println("[HazeCore] Loading configuration from " + configDir.getAbsolutePath());
        // In a full implementation, we'd use GSON or Moshi to load config files
        // and apply them to the modules in moduleManager.
    }

    public void saveConfig() {
        System.out.println("[HazeCore] Saving configuration to " + configDir.getAbsolutePath());
        // Save current state of moduleManager to JSON.
    }
}
