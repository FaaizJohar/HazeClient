package com.hazeclient.modules;

import com.hazeclient.modules.settings.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.*;

/**
 * Base class for all Haze Client modules.
 *
 * A module is a toggleable feature with:
 *   - Typed settings (auto-rendered by ClickGUI)
 *   - Position/size (for HUD modules — draggable in the HUD editor)
 *   - Keybind
 *   - Favorite flag
 *   - Background / color customization
 *   - Dependency chain
 *   - Full JSON serialization
 */
public abstract class Module {

    protected final MinecraftClient mc = MinecraftClient.getInstance();

    // ─── Identity ───────────────────────────────────────────────────────

    private final String name;
    private final String description;
    private final Category category;

    // ─── State ──────────────────────────────────────────────────────────

    private boolean enabled;
    private boolean favorite;
    private int keybind = -1; // GLFW key code, -1 = unbound

    // ─── HUD Position & Transform ───────────────────────────────────────

    private int x = 10;
    private int y = 10;
    private int width  = 50;
    private int height = 10;

    // Anchor point for responsive layouts
    private Anchor anchor = Anchor.TOP_LEFT;

    // Z-Index for layer ordering (higher = rendered on top)
    private int zIndex = 0;

    // ─── Settings ───────────────────────────────────────────────────────

    private final LinkedHashMap<String, Setting<?>> settings = new LinkedHashMap<>();

    // Common settings shared by all HUD modules
    private ColorSetting textColor;
    private ColorSetting backgroundColor;
    private ColorSetting borderColor;
    private FloatSetting backgroundOpacity;
    private FloatSetting scale;
    private BooleanSetting showBackground;
    private EnumSetting<BackgroundStyle> backgroundStyle;

    // ─── Dependencies ───────────────────────────────────────────────────

    private final List<String> dependencies = new ArrayList<>();

    // ═══════════════════════════════════════════════════════════════════
    //  ENUMS
    // ═══════════════════════════════════════════════════════════════════

    public enum Category {
        HUD("HUD", "On-screen information overlays"),
        RENDER("Render", "Visual enhancements"),
        MOVEMENT("Movement", "Movement tweaks"),
        COMBAT("Combat", "Combat utilities"),
        MISC("Misc", "Miscellaneous features"),
        PERFORMANCE("Performance", "Performance optimizations");

        public final String displayName;
        public final String description;

        Category(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
    }

    public enum Anchor {
        TOP_LEFT, TOP_CENTER, TOP_RIGHT,
        MIDDLE_LEFT, MIDDLE_CENTER, MIDDLE_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT
    }

    public enum BackgroundStyle {
        NONE, SOLID, ROUNDED, GRADIENT
    }

    // ═══════════════════════════════════════════════════════════════════
    //  CONSTRUCTOR
    // ═══════════════════════════════════════════════════════════════════

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = false;

        // Register common HUD settings for HUD-category modules
        if (category == Category.HUD) {
            textColor = addSetting(new ColorSetting("Text Color", "Module text color", 0xFFFFFFFF));
            backgroundColor = addSetting(new ColorSetting("Background Color", "Background tint", 0xFF0A0A0A));
            borderColor = addSetting(new ColorSetting("Border Color", "Border color", 0x30FFFFFF));
            backgroundOpacity = addSetting(new FloatSetting("BG Opacity", "Background transparency", 0.6f, 0f, 1f, 0.05f));
            scale = addSetting(new FloatSetting("Scale", "Module scale", 1.0f, 0.5f, 3.0f, 0.1f));
            showBackground = addSetting(new BooleanSetting("Show Background", "Draw background behind module", true));
            backgroundStyle = addSetting(new EnumSetting<>("BG Style", "Background shape", BackgroundStyle.ROUNDED));
        }
    }

    /** Backwards-compatible constructor (no description). */
    public Module(String name, Category category) {
        this(name, "", category);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  SETTINGS API
    // ═══════════════════════════════════════════════════════════════════

    /** Register a setting. Returns the setting for inline field assignment. */
    protected <T, S extends Setting<T>> S addSetting(S setting) {
        settings.put(setting.getName(), setting);
        return setting;
    }

    /** All settings in registration order. */
    public Collection<Setting<?>> getSettings() {
        return settings.values();
    }

    /** Get a specific setting by name. */
    public Setting<?> getSetting(String name) {
        return settings.get(name);
    }

    /** True if this module has any configurable settings. */
    public boolean hasSettings() {
        return !settings.isEmpty();
    }

    // ═══════════════════════════════════════════════════════════════════
    //  IDENTITY ACCESSORS
    // ═══════════════════════════════════════════════════════════════════

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }

    // ═══════════════════════════════════════════════════════════════════
    //  TOGGLE / ENABLE
    // ═══════════════════════════════════════════════════════════════════

    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (enabled) onEnable();
            else         onDisable();
        }
    }

    public void toggle() { setEnabled(!enabled); }

    protected void onEnable() {}
    protected void onDisable() {}

    // ═══════════════════════════════════════════════════════════════════
    //  KEYBIND
    // ═══════════════════════════════════════════════════════════════════

    public int getKeybind() { return keybind; }
    public void setKeybind(int glfwKeyCode) { this.keybind = glfwKeyCode; }
    public boolean hasKeybind() { return keybind > 0; }

    // ═══════════════════════════════════════════════════════════════════
    //  FAVORITE
    // ═══════════════════════════════════════════════════════════════════

    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean fav) { this.favorite = fav; }
    public void toggleFavorite() { this.favorite = !this.favorite; }

    // ═══════════════════════════════════════════════════════════════════
    //  HUD POSITION / SIZE
    // ═══════════════════════════════════════════════════════════════════

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getWidth() { return width; }
    protected void setWidth(int w) { this.width = w; }

    public int getHeight() { return height; }
    protected void setHeight(int h) { this.height = h; }

    public Anchor getAnchor() { return anchor; }
    public void setAnchor(Anchor a) { this.anchor = a; }

    protected void setSize(int w, int h) {
        this.width = w;
        this.height = h;
    }

    public int getZIndex() { return zIndex; }
    public void setZIndex(int z) { this.zIndex = z; }

    // ─── Common HUD settings accessors ──────────────────────────────────

    public int getTextColor()  { return textColor != null ? textColor.get() : 0xFFFFFFFF; }
    public int getBgColor()    { return backgroundColor != null ? backgroundColor.get() : 0xFF0A0A0A; }
    public float getBgOpacity(){ return backgroundOpacity != null ? backgroundOpacity.get() : 0.6f; }
    public float getScale()    { return scale != null ? scale.get() : 1.0f; }
    public boolean showBg()    { return showBackground != null && showBackground.get(); }
    public BackgroundStyle getBgStyle() { return backgroundStyle != null ? backgroundStyle.get() : BackgroundStyle.ROUNDED; }

    public void setShowBg(boolean show) { if (showBackground != null) showBackground.set(show); }
    public void setBgStyle(BackgroundStyle style) { if (backgroundStyle != null) backgroundStyle.set(style); }
    public void setBgOpacity(float opacity) { if (backgroundOpacity != null) backgroundOpacity.set(opacity); }

    // ═══════════════════════════════════════════════════════════════════
    //  DEPENDENCIES
    // ═══════════════════════════════════════════════════════════════════

    protected void addDependency(String moduleName) {
        dependencies.add(moduleName);
    }

    public List<String> getDependencies() { return dependencies; }

    // ═══════════════════════════════════════════════════════════════════
    //  RENDERING (override in HUD modules)
    // ═══════════════════════════════════════════════════════════════════

    /** Called every frame for HUD modules. */
    public void render(DrawContext context, float tickDelta) {}

    // ═══════════════════════════════════════════════════════════════════
    //  SERIALIZATION
    // ═══════════════════════════════════════════════════════════════════

    /** Serialize all state to a map (for JSON persistence). */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("enabled", enabled);
        map.put("favorite", favorite);
        map.put("keybind", keybind);
        map.put("x", x);
        map.put("y", y);
        map.put("anchor", anchor.name());
        map.put("zIndex", zIndex);

        if (!settings.isEmpty()) {
            Map<String, Object> settingsMap = new LinkedHashMap<>();
            for (var entry : settings.entrySet()) {
                settingsMap.put(entry.getKey(), entry.getValue().serialize());
            }
            map.put("settings", settingsMap);
        }

        return map;
    }

    /** Deserialize state from a map. */
    @SuppressWarnings("unchecked")
    public void fromMap(Map<String, Object> map) {
        if (map == null) return;

        if (map.containsKey("enabled")) {
            Object val = map.get("enabled");
            if (val instanceof Boolean b) setEnabled(b);
        }
        if (map.containsKey("favorite")) {
            Object val = map.get("favorite");
            if (val instanceof Boolean b) favorite = b;
        }
        if (map.containsKey("keybind")) {
            Object val = map.get("keybind");
            if (val instanceof Number n) keybind = n.intValue();
        }
        if (map.containsKey("x")) {
            Object val = map.get("x");
            if (val instanceof Number n) x = n.intValue();
        }
        if (map.containsKey("y")) {
            Object val = map.get("y");
            if (val instanceof Number n) y = n.intValue();
        }
        if (map.containsKey("anchor")) {
            Object val = map.get("anchor");
            if (val instanceof String s) {
                try { anchor = Anchor.valueOf(s); } catch (IllegalArgumentException ignored) {}
            }
        }
        if (map.containsKey("zIndex")) {
            Object val = map.get("zIndex");
            if (val instanceof Number n) zIndex = n.intValue();
        }
        if (map.containsKey("settings")) {
            Object raw = map.get("settings");
            if (raw instanceof Map<?,?> settingsMap) {
                for (var entry : settingsMap.entrySet()) {
                    Setting<?> setting = settings.get(entry.getKey().toString());
                    if (setting != null) {
                        setting.deserialize(entry.getValue());
                    }
                }
            }
        }
    }
}
