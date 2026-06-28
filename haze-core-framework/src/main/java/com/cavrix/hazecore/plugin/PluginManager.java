package com.cavrix.hazecore.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PluginManager {
    private final File pluginDir;
    private final List<HazePlugin> plugins = new ArrayList<>();

    public PluginManager(File gameDir) {
        this.pluginDir = new File(gameDir, "haze-plugins");
        if (!this.pluginDir.exists()) {
            this.pluginDir.mkdirs();
        }
    }

    public void discoverAndLoad() {
        System.out.println("[HazeCore] Discovering plugins in " + pluginDir.getAbsolutePath());
        // In a real implementation, we would scan JAR files, use a custom ClassLoader,
        // read plugin.json metadata, and instantiate the main class.
        
        for (HazePlugin plugin : plugins) {
            System.out.println("[HazeCore] Loading plugin: " + plugin.getName() + " v" + plugin.getVersion());
            plugin.onLoad();
        }
    }

    public void enableAll() {
        for (HazePlugin plugin : plugins) {
            try {
                plugin.onEnable();
                System.out.println("[HazeCore] Enabled plugin: " + plugin.getName());
            } catch (Exception e) {
                System.err.println("[HazeCore] Failed to enable plugin: " + plugin.getName());
                e.printStackTrace();
            }
        }
    }

    public void disableAll() {
        for (HazePlugin plugin : plugins) {
            try {
                plugin.onDisable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
