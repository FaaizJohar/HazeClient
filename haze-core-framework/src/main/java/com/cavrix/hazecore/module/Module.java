package com.cavrix.hazecore.module;

import com.cavrix.hazecore.event.EventBus;

public abstract class Module {
    private final String name;
    private final String description;
    private final ModuleCategory category;
    private boolean enabled;
    
    // Using EventBus passed during initialization or centrally accessed
    protected EventBus eventBus;

    public Module(String name, String description, ModuleCategory category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = false;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ModuleCategory getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (enabled) {
                onEnable();
                if (eventBus != null) eventBus.register(this);
            } else {
                if (eventBus != null) eventBus.unregister(this);
                onDisable();
            }
        }
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    protected void onEnable() {
        // Override for enable logic
    }

    protected void onDisable() {
        // Override for disable logic
    }

    // Lifecycle hooks
    public void onLoad() {}
    public void onInitialize() {}
    public void onPostInitialize() {}
    public void onUnload() {}
    public void onDestroy() {}
}
