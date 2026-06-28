package com.hazeclient.render;

/**
 * Haze Client Design Tokens — Color Palette.
 *
 * Every color used by the Haze Client UI is defined here so that theme
 * overrides only need to swap a single source of truth.  All values are
 * ARGB packed ints (0xAARRGGBB).
 *
 * Color naming follows the three-layer token architecture:
 *   Primitive  → raw hex values (private)
 *   Semantic   → role-based aliases (public)
 *   Component  → consumed by individual widgets (public)
 */
public final class HazeColors {

    private HazeColors() {}

    // ─── Primitive Palette ──────────────────────────────────────────────

    // Haze identity blues
    public static final int HAZE_BLUE        = 0xFF5B6EE1;
    public static final int HAZE_BLUE_LIGHT  = 0xFF8892F1;
    public static final int HAZE_BLUE_DARK   = 0xFF3A4BBF;

    // Accent
    public static final int CYAN_ACCENT      = 0xFF00AAFF;
    public static final int CYAN_ACCENT_DIM  = 0xFF0077BB;

    // Neutrals
    public static final int WHITE            = 0xFFFFFFFF;
    public static final int GRAY_100         = 0xFFF0F0F0;
    public static final int GRAY_200         = 0xFFD0D0D0;
    public static final int GRAY_300         = 0xFFB3B3B3;
    public static final int GRAY_400         = 0xFF888888;
    public static final int GRAY_500         = 0xFF666666;
    public static final int GRAY_600         = 0xFF444444;
    public static final int GRAY_700         = 0xFF2A2A2A;
    public static final int GRAY_800         = 0xFF1A1A1A;
    public static final int GRAY_900         = 0xFF111111;
    public static final int NEAR_BLACK       = 0xFF0D0D0D;
    public static final int TRUE_BLACK       = 0xFF000000;

    // ─── Semantic: Surfaces ─────────────────────────────────────────────

    /** Deepest background — behind everything. */
    public static final int SURFACE_BASE     = NEAR_BLACK;

    /** Primary card / panel background. */
    public static final int SURFACE_PRIMARY  = GRAY_800;

    /** Elevated surface (modals, popups). */
    public static final int SURFACE_ELEVATED = GRAY_700;

    /** Sidebar / panel chrome. */
    public static final int SURFACE_SIDEBAR  = 0xFF151515;

    /** Overlay dim (semi-transparent black). */
    public static final int OVERLAY_DIM      = 0x88000000;
    public static final int OVERLAY_HEAVY    = 0xCC000000;

    // ─── Semantic: Text ─────────────────────────────────────────────────

    public static final int TEXT_PRIMARY     = WHITE;
    public static final int TEXT_SECONDARY   = GRAY_300;
    public static final int TEXT_MUTED       = GRAY_500;
    public static final int TEXT_DISABLED    = GRAY_600;
    public static final int TEXT_ACCENT      = CYAN_ACCENT;

    // ─── Semantic: Interactive ──────────────────────────────────────────

    public static final int INTERACTIVE_DEFAULT   = HAZE_BLUE;
    public static final int INTERACTIVE_HOVER     = HAZE_BLUE_LIGHT;
    public static final int INTERACTIVE_PRESSED   = HAZE_BLUE_DARK;
    public static final int INTERACTIVE_DISABLED  = GRAY_600;

    // ─── Semantic: Status ───────────────────────────────────────────────

    public static final int STATUS_SUCCESS   = 0xFF00FF88;
    public static final int STATUS_WARNING   = 0xFFFFAA00;
    public static final int STATUS_ERROR     = 0xFFFF4455;
    public static final int STATUS_INFO      = CYAN_ACCENT;

    // ─── Semantic: Module cards ─────────────────────────────────────────

    public static final int CARD_OFF         = GRAY_800;
    public static final int CARD_ON          = 0xFF0D3B66;
    public static final int CARD_HOVER       = GRAY_700;
    public static final int CARD_BORDER      = 0x30FFFFFF;

    // ─── Semantic: Toggle switch ────────────────────────────────────────

    public static final int TOGGLE_TRACK_OFF = GRAY_600;
    public static final int TOGGLE_TRACK_ON  = HAZE_BLUE;
    public static final int TOGGLE_KNOB      = WHITE;

    // ─── Semantic: Scrollbar ────────────────────────────────────────────

    public static final int SCROLLBAR_TRACK  = 0x20FFFFFF;
    public static final int SCROLLBAR_THUMB  = 0x60FFFFFF;

    // ─── Semantic: Selection / Focus ────────────────────────────────────

    public static final int SELECTION        = CYAN_ACCENT;
    public static final int GLOW_COLOR       = 0x00AAFF;   // RGB only — alpha set by glow logic

    // ─── Semantic: HUD Editor ───────────────────────────────────────────

    public static final int GRID_LINE        = 0x18FFFFFF;
    public static final int SNAP_LINE        = 0xCC00CCFF;
    public static final int ANCHOR_DOT       = 0xFFFF6600;

    // ─── Alpha helpers ──────────────────────────────────────────────────

    /**
     * Returns the given RGB color with the specified alpha (0.0 – 1.0).
     */
    public static int withAlpha(int rgb, float alpha) {
        int a = Math.max(0, Math.min(255, (int) (alpha * 255)));
        return (rgb & 0x00FFFFFF) | (a << 24);
    }

    /**
     * Linearly interpolates between two ARGB colors.
     */
    public static int lerp(int from, int to, float t) {
        t = Math.max(0, Math.min(1, t));
        int a = (int) (((from >> 24) & 0xFF) * (1 - t) + ((to >> 24) & 0xFF) * t);
        int r = (int) (((from >> 16) & 0xFF) * (1 - t) + ((to >> 16) & 0xFF) * t);
        int g = (int) (((from >>  8) & 0xFF) * (1 - t) + ((to >>  8) & 0xFF) * t);
        int b = (int) ((from & 0xFF)         * (1 - t) + (to & 0xFF)         * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Extracts alpha component as a float 0.0–1.0.
     */
    public static float alphaOf(int argb) {
        return ((argb >> 24) & 0xFF) / 255f;
    }
}
