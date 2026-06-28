package com.cavrix.hazecore.module;

import com.cavrix.hazecore.event.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();
    private final EventBus eventBus;

    public ModuleManager(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void register(Module module) {
        module.setEventBus(eventBus);
        modules.add(module);
        module.onLoad();
    }

    public void initializeAll() {
        for (Module m : modules) m.onInitialize();
        for (Module m : modules) m.onPostInitialize();
    }

    public void destroyAll() {
        for (Module m : modules) {
            m.setEnabled(false);
            m.onUnload();
            m.onDestroy();
        }
    }

    public List<Module> getModules() {
        return Collections.unmodifiableList(modules);
    }

    public List<Module> getModulesByCategory(ModuleCategory category) {
        return modules.stream().filter(m -> m.getCategory() == category).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> clazz) {
        for (Module m : modules) {
            if (m.getClass() == clazz) {
                return (T) m;
            }
        }
        return null;
    }
}
