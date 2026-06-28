package com.hazeclient.modules;

import com.hazeclient.modules.impl.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Central registry for all Haze Client modules.
 *
 * Provides:
 *   - Category-cached module lists (no per-frame streaming)
 *   - Search by name substring
 *   - Favorites list
 *   - Profile-aware initialization
 */
public final class ModuleRegistry {

    private static final List<Module> modules = new ArrayList<>();
    private static final Map<Module.Category, List<Module>> categoryCache = new EnumMap<>(Module.Category.class);
    private static final Map<String, Module> nameIndex = new LinkedHashMap<>();
    private static boolean cacheValid = false;

    private ModuleRegistry() {}

    // ═══════════════════════════════════════════════════════════════════
    //  INITIALIZATION
    // ═══════════════════════════════════════════════════════════════════

    public static void initialize() {
        modules.clear();
        categoryCache.clear();
        nameIndex.clear();
        cacheValid = false;

        // ── HUD Modules ─────────────────────────────────────────────────
        // register(new FPSModule());
        register(new PingModule());
        register(new CPSModule());
        register(new CoordinatesModule());
        register(new DirectionModule());
        register(new MemoryModule());
        register(new ArmorModule());
        register(new KeystrokesModule());
        register(new ServerInfoModule());
        register(new SpeedModule());
        register(new ClockModule());
        register(new BiomeModule());

        // ── Movement Modules ────────────────────────────────────────────
        register(new ToggleSprintModule());
        register(new ToggleSneakModule());

        // ── Render Modules ──────────────────────────────────────────────
        register(new ZoomModule());
        register(new FreelookModule());

        // Enable core modules by default
        // enableDefault("FPS");
        enableDefault("Ping");
        enableDefault("CPS");
        enableDefault("Coordinates");
        enableDefault("ToggleSprint");

        rebuildCache();
    }

    private static void enableDefault(String name) {
        Module m = getModuleByName(name);
        if (m != null) m.setEnabled(true);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  REGISTRATION
    // ═══════════════════════════════════════════════════════════════════

    public static void register(Module module) {
        modules.add(module);
        nameIndex.put(module.getName().toLowerCase(), module);
        cacheValid = false;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  QUERIES
    // ═══════════════════════════════════════════════════════════════════

    public static List<Module> getModules() {
        return Collections.unmodifiableList(modules);
    }

    /** Category-filtered list — cached, no allocation per call. */
    public static List<Module> getModulesByCategory(Module.Category category) {
        if (!cacheValid) rebuildCache();
        return categoryCache.getOrDefault(category, Collections.emptyList());
    }

    /** Case-insensitive name lookup. */
    public static Module getModuleByName(String name) {
        return nameIndex.get(name.toLowerCase());
    }

    /** Lookup by class. */
    @SuppressWarnings("unchecked")
    public static <T extends Module> T getModule(Class<T> clazz) {
        for (Module m : modules) {
            if (clazz.isInstance(m)) return (T) m;
        }
        return null;
    }

    /** Search modules by partial name match (case-insensitive). */
    public static List<Module> search(String query) {
        if (query == null || query.isBlank()) return modules;
        String lowerQuery = query.toLowerCase().trim();
        return modules.stream()
                .filter(m -> m.getName().toLowerCase().contains(lowerQuery)
                          || m.getDescription().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    /** All favorited modules. */
    public static List<Module> getFavorites() {
        return modules.stream()
                .filter(Module::isFavorite)
                .collect(Collectors.toList());
    }

    /** All enabled modules. */
    public static List<Module> getEnabled() {
        return modules.stream()
                .filter(Module::isEnabled)
                .collect(Collectors.toList());
    }

    /** Modules with the given keybind. */
    public static List<Module> getByKeybind(int glfwKeyCode) {
        return modules.stream()
                .filter(m -> m.getKeybind() == glfwKeyCode)
                .collect(Collectors.toList());
    }

    /** Total module count. */
    public static int getCount() {
        return modules.size();
    }

    // ═══════════════════════════════════════════════════════════════════
    //  CACHE
    // ═══════════════════════════════════════════════════════════════════

    private static void rebuildCache() {
        categoryCache.clear();
        for (Module.Category cat : Module.Category.values()) {
            categoryCache.put(cat, new ArrayList<>());
        }
        for (Module mod : modules) {
            categoryCache.get(mod.getCategory()).add(mod);
        }
        
        categoryCache.get(Module.Category.HUD).sort(java.util.Comparator.comparingInt(Module::getZIndex));
        
        cacheValid = true;
    }

    /** Call after adding new modules dynamically. */
    public static void invalidateCache() {
        cacheValid = false;
    }
}
