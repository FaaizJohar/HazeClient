package com.hazeclient.config;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.hazeclient.HazeClientMod;
import com.hazeclient.modules.Module;
import com.hazeclient.modules.ModuleRegistry;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * JSON-based persistence for all module states, positions, and settings.
 *
 * Features:
 *   - Auto-save on changes (debounced)
 *   - Named profiles ("Default", "PvP", "Building")
 *   - Import / export profiles as standalone JSON
 *   - Thread-safe save (writes to temp then atomic-renames)
 */
public final class ModuleConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type MAP_TYPE = new TypeToken<Map<String, Object>>(){}.getType();

    private static Path configDir;
    private static String activeProfile = "Default";

    // Debounce
    private static long lastChangeTime = 0;
    private static boolean dirty = false;
    private static final long DEBOUNCE_MS = 2000;

    private ModuleConfig() {}

    // ═══════════════════════════════════════════════════════════════════
    //  INITIALIZATION
    // ═══════════════════════════════════════════════════════════════════

    public static void initialize() {
        configDir = getConfigDirectory();
        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            HazeClientMod.LOGGER.error("Failed to create config directory", e);
        }
        load();
    }

    private static Path getConfigDirectory() {
        // Fabric provides the config dir via FabricLoader, but we can also use the run dir
        Path runDir = Path.of(System.getProperty("user.dir", "."));
        return runDir.resolve("config").resolve("hazeclient");
    }

    // ═══════════════════════════════════════════════════════════════════
    //  SAVE
    // ═══════════════════════════════════════════════════════════════════

    /** Marks config as dirty — will auto-save after debounce period. */
    public static void markDirty() {
        dirty = true;
        lastChangeTime = System.currentTimeMillis();
    }

    /** Should be called from the client tick to check debounced save. */
    public static void tick() {
        if (dirty && System.currentTimeMillis() - lastChangeTime >= DEBOUNCE_MS) {
            save();
            dirty = false;
        }
    }

    /** Force immediate save. */
    public static void save() {
        saveProfile(activeProfile);
    }

    public static void saveProfile(String profileName) {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("profile", profileName);
        root.put("version", 1);

        Map<String, Object> modules = new LinkedHashMap<>();
        for (Module mod : ModuleRegistry.getModules()) {
            modules.put(mod.getName(), mod.toMap());
        }
        root.put("modules", modules);

        Path file = configDir.resolve(profileName.toLowerCase().replace(" ", "_") + ".json");
        try {
            Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
            try (Writer writer = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
                GSON.toJson(root, writer);
            }
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            HazeClientMod.LOGGER.info("Saved profile: {}", profileName);
        } catch (IOException e) {
            HazeClientMod.LOGGER.error("Failed to save profile: " + profileName, e);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  LOAD
    // ═══════════════════════════════════════════════════════════════════

    public static void load() {
        loadProfile(activeProfile);
    }

    @SuppressWarnings("unchecked")
    public static void loadProfile(String profileName) {
        Path file = configDir.resolve(profileName.toLowerCase().replace(" ", "_") + ".json");
        if (!Files.exists(file)) {
            HazeClientMod.LOGGER.info("No config file for profile '{}', using defaults.", profileName);
            return;
        }

        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            Map<String, Object> root = GSON.fromJson(reader, MAP_TYPE);
            if (root == null) return;

            Object modulesRaw = root.get("modules");
            if (modulesRaw instanceof Map<?,?> modulesMap) {
                for (var entry : modulesMap.entrySet()) {
                    String modName = entry.getKey().toString();
                    Module mod = ModuleRegistry.getModuleByName(modName);
                    if (mod != null && entry.getValue() instanceof Map<?,?> modData) {
                        mod.fromMap((Map<String, Object>) modData);
                    }
                }
            }

            activeProfile = profileName;
            HazeClientMod.LOGGER.info("Loaded profile: {}", profileName);
        } catch (Exception e) {
            HazeClientMod.LOGGER.error("Failed to load profile: " + profileName, e);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  PROFILES
    // ═══════════════════════════════════════════════════════════════════

    public static String getActiveProfile() { return activeProfile; }

    public static void switchProfile(String profileName) {
        save(); // save current first
        activeProfile = profileName;
        // Reset all modules to defaults before loading
        for (Module mod : ModuleRegistry.getModules()) {
            mod.setEnabled(false);
            for (var setting : mod.getSettings()) {
                setting.reset();
            }
        }
        loadProfile(profileName);
    }

    /** Lists all available profile names (from files on disk). */
    public static List<String> listProfiles() {
        List<String> profiles = new ArrayList<>();
        if (configDir == null || !Files.isDirectory(configDir)) {
            profiles.add("Default");
            return profiles;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(configDir, "*.json")) {
            for (Path path : stream) {
                String fileName = path.getFileName().toString();
                fileName = fileName.replace(".json", "").replace("_", " ");
                // Capitalize
                StringBuilder sb = new StringBuilder();
                for (String word : fileName.split(" ")) {
                    if (!sb.isEmpty()) sb.append(' ');
                    if (!word.isEmpty()) sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
                }
                profiles.add(sb.toString());
            }
        } catch (IOException ignored) {}
        if (profiles.isEmpty()) profiles.add("Default");
        return profiles;
    }

    /** Delete a profile (cannot delete Default). */
    public static boolean deleteProfile(String profileName) {
        if ("Default".equalsIgnoreCase(profileName)) return false;
        Path file = configDir.resolve(profileName.toLowerCase().replace(" ", "_") + ".json");
        try {
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            HazeClientMod.LOGGER.error("Failed to delete profile: " + profileName, e);
            return false;
        }
    }

    /** Export a profile to a specific path (for sharing). */
    public static void exportProfile(String profileName, Path destination) {
        Path source = configDir.resolve(profileName.toLowerCase().replace(" ", "_") + ".json");
        if (Files.exists(source)) {
            try {
                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                HazeClientMod.LOGGER.error("Failed to export profile", e);
            }
        }
    }

    /** Import a profile from a path. */
    public static void importProfile(Path source) {
        if (!Files.exists(source)) return;
        String fileName = source.getFileName().toString().replace(".json", "");
        Path dest = configDir.resolve(fileName + ".json");
        try {
            Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
            HazeClientMod.LOGGER.info("Imported profile: {}", fileName);
        } catch (IOException e) {
            HazeClientMod.LOGGER.error("Failed to import profile", e);
        }
    }
}
