package com.cavrix.hazecore.plugin;

public interface HazePlugin {
    String getName();
    String getVersion();
    String getAuthor();

    /**
     * Called when the plugin is first discovered and loaded.
     */
    void onLoad();

    /**
     * Called after all core systems have initialized.
     * This is where a plugin should register its modules, events, and UI components.
     */
    void onEnable();

    /**
     * Called when the client is shutting down or the plugin is hot-unloaded.
     */
    void onDisable();
}
