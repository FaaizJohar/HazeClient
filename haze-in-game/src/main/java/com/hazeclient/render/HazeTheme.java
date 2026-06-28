package com.hazeclient.render;

/**
 * Runtime theme system for Haze Client.
 *
 * A theme overrides the design tokens from {@link HazeColors} and controls
 * UI density, corner radii, blur intensity, animation speed, and font scale.
 *
 * Theme changes are applied instantly — no restart needed.  The active theme
 * is stored as a singleton accessible from any rendering code.
 */
public final class HazeTheme {

    private static HazeTheme active = createDefault();

    // ─── Color Overrides ────────────────────────────────────────────────

    private int accentColor;
    private int accentColorHover;
    private int accentColorPressed;

    private int surfaceBase;
    private int surfacePrimary;
    private int surfaceElevated;
    private int surfaceSidebar;

    private int textPrimary;
    private int textSecondary;
    private int textMuted;

    private int cardOff;
    private int cardOn;
    private int cardBorder;

    private int toggleTrackOff;
    private int toggleTrackOn;
    private int toggleKnob;

    // ─── UI Properties ──────────────────────────────────────────────────

    private String name;
    private int    cornerRadius;       // px — for cards, panels
    private int    cornerRadiusSmall;  // px — for buttons, toggles
    private float  blurIntensity;      // 0.0 = none, 1.0 = maximum
    private float  animationSpeed;     // multiplier: 1.0 = default, 0.5 = slow, 2.0 = fast
    private float  fontScale;          // 1.0 = default
    private UIDensity density;

    public enum UIDensity {
        COMPACT(20, 4, 10),
        NORMAL(28, 6, 14),
        COMFORTABLE(36, 8, 18);

        public final int itemHeight;
        public final int itemGap;
        public final int padding;

        UIDensity(int itemHeight, int itemGap, int padding) {
            this.itemHeight = itemHeight;
            this.itemGap = itemGap;
            this.padding = padding;
        }
    }

    // ─── Factory ────────────────────────────────────────────────────────

    public static HazeTheme createDefault() {
        HazeTheme t = new HazeTheme();
        t.name = "Haze Dark";

        // Colors from design tokens
        t.accentColor        = HazeColors.HAZE_BLUE;
        t.accentColorHover   = HazeColors.HAZE_BLUE_LIGHT;
        t.accentColorPressed = HazeColors.HAZE_BLUE_DARK;

        t.surfaceBase     = HazeColors.SURFACE_BASE;
        t.surfacePrimary  = HazeColors.SURFACE_PRIMARY;
        t.surfaceElevated = HazeColors.SURFACE_ELEVATED;
        t.surfaceSidebar  = HazeColors.SURFACE_SIDEBAR;

        t.textPrimary   = HazeColors.TEXT_PRIMARY;
        t.textSecondary = HazeColors.TEXT_SECONDARY;
        t.textMuted     = HazeColors.TEXT_MUTED;

        t.cardOff    = HazeColors.CARD_OFF;
        t.cardOn     = HazeColors.CARD_ON;
        t.cardBorder = HazeColors.CARD_BORDER;

        t.toggleTrackOff = HazeColors.TOGGLE_TRACK_OFF;
        t.toggleTrackOn  = HazeColors.TOGGLE_TRACK_ON;
        t.toggleKnob     = HazeColors.TOGGLE_KNOB;

        t.cornerRadius      = 10;
        t.cornerRadiusSmall = 6;
        t.blurIntensity     = 0.5f;
        t.animationSpeed    = 1.0f;
        t.fontScale         = 1.0f;
        t.density           = UIDensity.NORMAL;

        return t;
    }

    public static HazeTheme createMidnight() {
        HazeTheme t = createDefault();
        t.name = "Midnight";
        t.accentColor        = 0xFF6C63FF;
        t.accentColorHover   = 0xFF8A82FF;
        t.accentColorPressed = 0xFF4F46CC;
        t.surfaceBase     = 0xFF050510;
        t.surfacePrimary  = 0xFF0E0E20;
        t.surfaceElevated = 0xFF1A1A35;
        t.surfaceSidebar  = 0xFF0A0A18;
        return t;
    }

    public static HazeTheme createEmber() {
        HazeTheme t = createDefault();
        t.name = "Ember";
        t.accentColor        = 0xFFFF6B35;
        t.accentColorHover   = 0xFFFF8B5E;
        t.accentColorPressed = 0xFFCC5528;
        t.cardOn = 0xFF3B1A0A;
        t.toggleTrackOn = 0xFFFF6B35;
        return t;
    }

    public static HazeTheme createAurora() {
        HazeTheme t = createDefault();
        t.name = "Aurora";
        t.accentColor        = 0xFF00E5A0;
        t.accentColorHover   = 0xFF33FFBB;
        t.accentColorPressed = 0xFF00B880;
        t.cardOn = 0xFF0A2B20;
        t.toggleTrackOn = 0xFF00E5A0;
        return t;
    }

    // ─── Singleton Access ───────────────────────────────────────────────

    public static HazeTheme get() {
        return active;
    }

    public static void set(HazeTheme theme) {
        active = theme;
    }

    // ─── Computed Helpers ────────────────────────────────────────────────

    /** Returns the animation duration in ms, adjusted by the theme's speed multiplier. */
    public long animDuration(long baseMs) {
        return Math.max(16, (long) (baseMs / animationSpeed));
    }

    /** Returns the overlay dim color with theme-aware alpha. */
    public int overlayDim() {
        return HazeColors.withAlpha(0x000000, 0.5f + blurIntensity * 0.2f);
    }

    // ─── Getters ────────────────────────────────────────────────────────

    public String getName()            { return name; }
    public int    getAccentColor()     { return accentColor; }
    public int    getAccentHover()     { return accentColorHover; }
    public int    getAccentPressed()   { return accentColorPressed; }
    public int    getSurfaceBase()     { return surfaceBase; }
    public int    getSurfacePrimary()  { return surfacePrimary; }
    public int    getSurfaceElevated() { return surfaceElevated; }
    public int    getSurfaceSidebar()  { return surfaceSidebar; }
    public int    getTextPrimary()     { return textPrimary; }
    public int    getTextSecondary()   { return textSecondary; }
    public int    getTextMuted()       { return textMuted; }
    public int    getCardOff()         { return cardOff; }
    public int    getCardOn()          { return cardOn; }
    public int    getCardBorder()      { return cardBorder; }
    public int    getToggleTrackOff()  { return toggleTrackOff; }
    public int    getToggleTrackOn()   { return toggleTrackOn; }
    public int    getToggleKnob()      { return toggleKnob; }
    public int    getCornerRadius()    { return cornerRadius; }
    public int    getCornerRadiusSmall() { return cornerRadiusSmall; }
    public float  getBlurIntensity()   { return blurIntensity; }
    public float  getAnimationSpeed()  { return animationSpeed; }
    public float  getFontScale()       { return fontScale; }
    public UIDensity getDensity()      { return density; }

    // ─── Setters (for theme editor) ─────────────────────────────────────

    public void setAccentColor(int c)        { this.accentColor = c; }
    public void setAccentHover(int c)        { this.accentColorHover = c; }
    public void setAccentPressed(int c)      { this.accentColorPressed = c; }
    public void setSurfaceBase(int c)        { this.surfaceBase = c; }
    public void setSurfacePrimary(int c)     { this.surfacePrimary = c; }
    public void setSurfaceElevated(int c)    { this.surfaceElevated = c; }
    public void setCornerRadius(int r)       { this.cornerRadius = r; }
    public void setCornerRadiusSmall(int r)  { this.cornerRadiusSmall = r; }
    public void setBlurIntensity(float b)    { this.blurIntensity = Math.max(0, Math.min(1, b)); }
    public void setAnimationSpeed(float s)   { this.animationSpeed = Math.max(0.1f, Math.min(5, s)); }
    public void setFontScale(float s)        { this.fontScale = Math.max(0.5f, Math.min(2.0f, s)); }
    public void setDensity(UIDensity d)      { this.density = d; }
}
