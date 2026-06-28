package com.cavrix.hazecore;

import com.cavrix.hazecore.animation.AnimationScheduler;
import com.cavrix.hazecore.theme.ThemeManager;
import com.cavrix.hazecore.plugin.PluginManager;
import com.cavrix.hazecore.asset.AssetManager;
import com.cavrix.hazecore.config.ConfigManager;
import com.cavrix.hazecore.event.EventBus;
import com.cavrix.hazecore.module.ModuleManager;
import com.cavrix.hazecore.render.RenderEngine;
import com.cavrix.hazecore.diagnostics.Profiler;
import com.cavrix.hazecore.cloud.CloudManager;

import java.io.File;
import java.lang.instrument.Instrumentation;

public class HazeCoreAgent {

    private static HazeCoreAgent instance;
    private final EventBus eventBus;
    private final ModuleManager moduleManager;
    private final ConfigManager configManager;
    private final AssetManager assetManager;
    private final RenderEngine renderEngine;
    private final AnimationScheduler animationScheduler;
    private final ThemeManager themeManager;
    private final PluginManager pluginManager;
    private final Profiler profiler;
    private final CloudManager cloudManager;

    private HazeCoreAgent() {
        System.out.println("[HazeCore] Initializing Core Framework...");
        this.eventBus = new EventBus();
        this.moduleManager = new ModuleManager(this.eventBus);
        this.assetManager = new AssetManager();
        this.configManager = new ConfigManager(new File("."), this.moduleManager);
        this.renderEngine = new RenderEngine(this.eventBus);
        this.animationScheduler = new AnimationScheduler(this.eventBus);
        this.themeManager = new ThemeManager();
        this.pluginManager = new PluginManager(new File("."));
        this.profiler = new Profiler();
        this.cloudManager = new CloudManager();
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("[HazeCore] premain invoked. Setting up instrumentation...");
        instance = new HazeCoreAgent();
        
        // Phase 1: Load
        instance.pluginManager.discoverAndLoad();
        instance.assetManager.initialize();
        instance.configManager.loadConfig();
        instance.cloudManager.initialize();
        
        // Phase 2: Init Core
        instance.moduleManager.registerModule(new com.cavrix.hazecore.module.impl.FpsModule());
        instance.moduleManager.initializeAll();

        // Phase 3: Enable Plugins
        instance.pluginManager.enableAll();

        inst.addTransformer(new MinecraftTransformer());
    }

    public static HazeCoreAgent getInstance() {
        return instance;
    }

    public EventBus getEventBus() { return eventBus; }
    public ModuleManager getModuleManager() { return moduleManager; }
    public ConfigManager getConfigManager() { return configManager; }
    public AssetManager getAssetManager() { return assetManager; }
    public RenderEngine getRenderEngine() { return renderEngine; }
    public AnimationScheduler getAnimationScheduler() { return animationScheduler; }
    public ThemeManager getThemeManager() { return themeManager; }
    public PluginManager getPluginManager() { return pluginManager; }
    public Profiler getProfiler() { return profiler; }
    public CloudManager getCloudManager() { return cloudManager; }
}
