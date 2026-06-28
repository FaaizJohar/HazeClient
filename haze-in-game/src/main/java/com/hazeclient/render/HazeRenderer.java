package com.hazeclient.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.font.TextRenderer;

/**
 * Zero-allocation rendering utilities for the Haze Client UI.
 *
 * All methods operate on the current DrawContext's MatrixStack and avoid
 * creating temporary objects in hot paths.  Colors use bitwise alpha
 * manipulation to avoid shader swaps.
 *
 * Extended in the premium redesign with:
 *   - Gradient rounded rectangles
 *   - Drop shadows (layered outset)
 *   - Frosted-glass blur simulation
 *   - GL scissor clip regions
 *   - Toggle switch / slider / progress bar drawing
 *   - Scaled text
 *   - Separator lines and dividers
 */
public final class HazeRenderer {

    private HazeRenderer() {}

    // ═══════════════════════════════════════════════════════════════════
    //  MATRIX TRANSFORM HELPERS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Pushes a combined translate + scale + rotation transform.
     * Call {@link #popTransform} when done.
     */
    public static void pushTransform(DrawContext ctx, float x, float y, float scale, float rotDeg) {
        ctx.getMatrices().push();
        ctx.getMatrices().translate(x, y, 0);
        if (scale != 1.0f) {
            ctx.getMatrices().scale(scale, scale, 1.0f);
        }
        if (rotDeg != 0.0f) {
            ctx.getMatrices().multiply(
                new org.joml.Quaternionf().rotationZ((float) Math.toRadians(rotDeg))
            );
        }
    }

    public static void popTransform(DrawContext ctx) {
        ctx.getMatrices().pop();
    }

    // ═══════════════════════════════════════════════════════════════════
    //  COLOR UTILITIES
    // ═══════════════════════════════════════════════════════════════════

    /** Packs RGB with an opacity float (0–1) into ARGB. */
    public static int withAlpha(int rgb, float opacity) {
        int a = Math.max(0, Math.min(255, (int) (opacity * 255)));
        return (rgb & 0x00FFFFFF) | (a << 24);
    }

    /** Linearly interpolates between two ARGB colors. */
    public static int lerpColor(int from, int to, float t) {
        t = Math.max(0, Math.min(1, t));
        int a = (int) (((from >> 24) & 0xFF) * (1 - t) + ((to >> 24) & 0xFF) * t);
        int r = (int) (((from >> 16) & 0xFF) * (1 - t) + ((to >> 16) & 0xFF) * t);
        int g = (int) (((from >>  8) & 0xFF) * (1 - t) + ((to >>  8) & 0xFF) * t);
        int b = (int) ((from & 0xFF) * (1 - t) + (to & 0xFF) * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  PRIMITIVE RECTANGLES
    // ═══════════════════════════════════════════════════════════════════

    /** Filled rectangle (x1,y1)→(x2,y2) with full ARGB. */
    public static void drawRect(DrawContext ctx, int x1, int y1, int x2, int y2, int argb) {
        ctx.fill(x1, y1, x2, y2, argb);
    }

    /** Filled rectangle with opacity. */
    public static void drawRect(DrawContext ctx, int x1, int y1, int x2, int y2, int color, float opacity) {
        ctx.fill(x1, y1, x2, y2, withAlpha(color, opacity));
    }

    /** Simulated rounded rectangle by layering fills. */
    public static void drawRoundedRect(DrawContext ctx, int x, int y, int w, int h, int radius, int argb) {
        radius = Math.min(radius, Math.min(w / 2, h / 2));
        if (radius <= 0) {
            ctx.fill(x, y, x + w, y + h, argb);
            return;
        }
        // Central body
        ctx.fill(x, y + radius, x + w, y + h - radius, argb);
        // Top/bottom strips
        ctx.fill(x + radius, y, x + w - radius, y + radius, argb);
        ctx.fill(x + radius, y + h - radius, x + w - radius, y + h, argb);
        // Rounded corners via arc scanlines
        for (int i = 0; i < radius; i++) {
            int offset = radius - (int) Math.sqrt(radius * radius - (radius - i) * (radius - i));
            ctx.fill(x + offset, y + i, x + radius, y + i + 1, argb);
            ctx.fill(x + w - radius, y + i, x + w - offset, y + i + 1, argb);
            ctx.fill(x + offset, y + h - 1 - i, x + radius, y + h - i, argb);
            ctx.fill(x + w - radius, y + h - 1 - i, x + w - offset, y + h - i, argb);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  GRADIENT RECTANGLES
    // ═══════════════════════════════════════════════════════════════════

    /** Vertical gradient fill. */
    public static void drawGradientRect(DrawContext ctx, int x1, int y1, int x2, int y2, int colorTop, int colorBottom) {
        ctx.fillGradient(x1, y1, x2, y2, colorTop, colorBottom);
    }

    /**
     * Rounded rectangle with a vertical gradient (top→bottom).
     * Uses scanline rasterization with per-line color interpolation.
     */
    public static void drawGradientRoundedRect(DrawContext ctx, int x, int y, int w, int h, int radius,
                                                int colorTop, int colorBottom) {
        radius = Math.min(radius, Math.min(w / 2, h / 2));
        if (h <= 0 || w <= 0) return;

        for (int row = 0; row < h; row++) {
            float t = (float) row / Math.max(1, h - 1);
            int lineColor = lerpColor(colorTop, colorBottom, t);

            int inset = 0;
            if (row < radius) {
                inset = radius - (int) Math.sqrt(radius * radius - (radius - row) * (radius - row));
            } else if (row >= h - radius) {
                int fromBottom = h - 1 - row;
                inset = radius - (int) Math.sqrt(radius * radius - (radius - fromBottom) * (radius - fromBottom));
            }
            ctx.fill(x + inset, y + row, x + w - inset, y + row + 1, lineColor);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  BORDERS & OUTLINES
    // ═══════════════════════════════════════════════════════════════════

    /** 1px border rectangle. */
    public static void drawBorder(DrawContext ctx, int x, int y, int w, int h, int argb) {
        ctx.fill(x, y, x + w, y + 1, argb);          // top
        ctx.fill(x, y + h - 1, x + w, y + h, argb);  // bottom
        ctx.fill(x, y + 1, x + 1, y + h - 1, argb);  // left
        ctx.fill(x + w - 1, y + 1, x + w, y + h - 1, argb); // right
    }

    /** Rounded border — outline only, no fill. */
    public static void drawRoundedBorder(DrawContext ctx, int x, int y, int w, int h, int radius, int argb) {
        radius = Math.min(radius, Math.min(w / 2, h / 2));
        if (radius <= 0) {
            drawBorder(ctx, x, y, w, h, argb);
            return;
        }
        // Straight edges
        ctx.fill(x + radius, y, x + w - radius, y + 1, argb);           // top
        ctx.fill(x + radius, y + h - 1, x + w - radius, y + h, argb);   // bottom
        ctx.fill(x, y + radius, x + 1, y + h - radius, argb);           // left
        ctx.fill(x + w - 1, y + radius, x + w, y + h - radius, argb);   // right
        // Corners
        for (int i = 0; i < radius; i++) {
            int outer = radius - (int) Math.sqrt(radius * radius - (radius - i) * (radius - i));
            int inner = radius - (int) Math.sqrt(Math.max(0, (radius - 1) * (radius - 1) - (radius - 1 - i) * (radius - 1 - i)));
            // Top-left
            ctx.fill(x + outer, y + i, x + Math.max(outer + 1, inner), y + i + 1, argb);
            // Top-right
            ctx.fill(x + w - Math.max(outer + 1, inner), y + i, x + w - outer, y + i + 1, argb);
            // Bottom-left
            ctx.fill(x + outer, y + h - 1 - i, x + Math.max(outer + 1, inner), y + h - i, argb);
            // Bottom-right
            ctx.fill(x + w - Math.max(outer + 1, inner), y + h - 1 - i, x + w - outer, y + h - i, argb);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  SHADOWS & GLOW
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Drop shadow behind a rectangle — layered outset rects with decreasing alpha.
     * Renders BEHIND the element (call before drawing the element itself).
     */
    public static void drawShadow(DrawContext ctx, int x, int y, int w, int h, int radius,
                                   int color, int layers, float maxAlpha) {
        for (int i = layers; i > 0; i--) {
            float alpha = maxAlpha * (1.0f - (float) i / layers) * (1.0f - (float) i / layers);
            int argb = withAlpha(color, alpha);
            drawRoundedRect(ctx, x - i, y - i, w + i * 2, h + i * 2, radius + i, argb);
        }
    }

    /** Default shadow — dark, 6 layers. */
    public static void drawShadow(DrawContext ctx, int x, int y, int w, int h, int radius) {
        drawShadow(ctx, x, y, w, h, radius, 0x000000, 6, 0.25f);
    }

    /**
     * Glow effect around a rectangle (layered outset with colored alpha).
     */
    public static void drawGlow(DrawContext ctx, int x, int y, int w, int h, int color, int layers) {
        for (int i = layers; i > 0; i--) {
            float alpha = 0.08f * (1.0f - (float) i / layers);
            int argb = withAlpha(color, alpha);
            ctx.fill(x - i, y - i, x + w + i, y + h + i, argb);
        }
    }

    /** Pulsing glow — alpha oscillates based on system time. */
    public static void drawPulsingGlow(DrawContext ctx, int x, int y, int w, int h, int color, int layers) {
        float pulse = (float) (0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 400.0));
        for (int i = layers; i > 0; i--) {
            float alpha = 0.12f * pulse * (1.0f - (float) i / layers);
            ctx.fill(x - i, y - i, x + w + i, y + h + i, withAlpha(color, alpha));
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  FROSTED GLASS / BLUR SIMULATION
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Simulates a frosted-glass backdrop by layering translucent fills.
     * Not a true blur — but visually convincing at high frame rates.
     */
    public static void drawFrostedRect(DrawContext ctx, int x, int y, int w, int h, int radius,
                                        int tintColor, float intensity) {
        // Base layer — semi-opaque dark to desaturate
        drawRoundedRect(ctx, x, y, w, h, radius, withAlpha(0x0A0A0A, 0.6f * intensity));
        // Tint layer
        drawRoundedRect(ctx, x, y, w, h, radius, withAlpha(tintColor, 0.15f * intensity));
        // Edge highlight (top)
        ctx.fill(x + radius, y, x + w - radius, y + 1, withAlpha(0xFFFFFF, 0.06f * intensity));
    }

    // ═══════════════════════════════════════════════════════════════════
    //  GL SCISSOR (CLIP REGIONS)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Enables GL scissor test clipping to the given screen-space rectangle.
     * All subsequent draw calls will be clipped. Call {@link #endScissor} when done.
     *
     * @param scaleFactor the GUI scale factor from MinecraftClient.getInstance().getWindow().getScaleFactor()
     */
    public static void beginScissor(int x, int y, int w, int h, double scaleFactor) {
        RenderSystem.enableScissor(
            (int) (x * scaleFactor),
            (int) ((net.minecraft.client.MinecraftClient.getInstance().getWindow().getScaledHeight() - y - h) * scaleFactor),
            (int) (w * scaleFactor),
            (int) (h * scaleFactor)
        );
    }

    public static void endScissor() {
        RenderSystem.disableScissor();
    }

    // ═══════════════════════════════════════════════════════════════════
    //  TEXT DRAWING
    // ═══════════════════════════════════════════════════════════════════

    /** Text with shadow and opacity. */
    public static void drawText(DrawContext ctx, TextRenderer font, String text,
                                 int x, int y, int color, float opacity) {
        ctx.drawText(font, text, x, y, withAlpha(color, opacity), true);
    }

    /** Centered text. */
    public static void drawCenteredText(DrawContext ctx, TextRenderer font, String text,
                                         int centerX, int y, int argb) {
        int tw = font.getWidth(text);
        ctx.drawText(font, text, centerX - tw / 2, y, argb, true);
    }

    /** Right-aligned text. */
    public static void drawRightText(DrawContext ctx, TextRenderer font, String text,
                                      int rightX, int y, int argb) {
        int tw = font.getWidth(text);
        ctx.drawText(font, text, rightX - tw, y, argb, true);
    }

    /** Scaled text — pushes a scale transform, draws, then pops. */
    public static void drawScaledText(DrawContext ctx, TextRenderer font, String text,
                                       int x, int y, int argb, float scale) {
        ctx.getMatrices().push();
        ctx.getMatrices().translate(x, y, 0);
        ctx.getMatrices().scale(scale, scale, 1.0f);
        ctx.drawText(font, text, 0, 0, argb, true);
        ctx.getMatrices().pop();
    }

    /** Centered scaled text. */
    public static void drawCenteredScaledText(DrawContext ctx, TextRenderer font, String text,
                                               int centerX, int y, int argb, float scale) {
        int tw = (int) (font.getWidth(text) * scale);
        drawScaledText(ctx, font, text, centerX - tw / 2, y, argb, scale);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  UI WIDGETS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Draws an animated toggle switch.
     *
     * @param knobPosition 0.0 = off, 1.0 = on (animate this for smooth transition)
     */
    public static void drawToggle(DrawContext ctx, int x, int y, int width, int height,
                                   float knobPosition, int trackOff, int trackOn, int knobColor) {
        int radius = height / 2;
        int trackColor = lerpColor(trackOff, trackOn, knobPosition);
        drawRoundedRect(ctx, x, y, width, height, radius, trackColor);

        // Knob
        int knobSize = height - 4;
        int knobRange = width - knobSize - 4;
        int knobX = x + 2 + (int) (knobRange * knobPosition);
        drawRoundedRect(ctx, knobX, y + 2, knobSize, knobSize, knobSize / 2, knobColor);
    }

    /** Toggle with theme colors. */
    public static void drawToggle(DrawContext ctx, int x, int y, float knobPosition) {
        HazeTheme t = HazeTheme.get();
        drawToggle(ctx, x, y, 32, 16, knobPosition,
                   t.getToggleTrackOff(), t.getToggleTrackOn(), t.getToggleKnob());
    }

    /**
     * Draws a horizontal slider.
     *
     * @param value 0.0–1.0 normalized position
     */
    public static void drawSlider(DrawContext ctx, int x, int y, int width, int height,
                                   float value, int trackColor, int fillColor, int knobColor) {
        int radius = height / 2;
        // Track
        drawRoundedRect(ctx, x, y, width, height, radius, trackColor);
        // Filled portion
        int fillW = Math.max(height, (int) (width * value));
        drawRoundedRect(ctx, x, y, fillW, height, radius, fillColor);
        // Knob
        int knobSize = height + 4;
        int knobX = x + (int) ((width - knobSize) * value);
        drawRoundedRect(ctx, knobX, y - 2, knobSize, knobSize, knobSize / 2, knobColor);
    }

    /** Slider with theme colors. */
    public static void drawSlider(DrawContext ctx, int x, int y, int width, float value) {
        HazeTheme t = HazeTheme.get();
        drawSlider(ctx, x, y, width, 6, value,
                   HazeColors.GRAY_600, t.getAccentColor(), HazeColors.WHITE);
    }

    /**
     * Draws a progress bar (no knob).
     */
    public static void drawProgressBar(DrawContext ctx, int x, int y, int width, int height,
                                        float progress, int trackColor, int fillColor, int radius) {
        drawRoundedRect(ctx, x, y, width, height, radius, trackColor);
        int fillW = (int) (width * Math.max(0, Math.min(1, progress)));
        if (fillW > 0) {
            drawRoundedRect(ctx, x, y, fillW, height, radius, fillColor);
        }
    }

    /** Progress bar with theme colors. */
    public static void drawProgressBar(DrawContext ctx, int x, int y, int width, float progress) {
        HazeTheme t = HazeTheme.get();
        drawProgressBar(ctx, x, y, width, 4, progress,
                        HazeColors.GRAY_700, t.getAccentColor(), 2);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  SEPARATORS & DIVIDERS
    // ═══════════════════════════════════════════════════════════════════

    /** Horizontal separator line. */
    public static void drawHorizontalSeparator(DrawContext ctx, int x, int y, int width, int argb) {
        ctx.fill(x, y, x + width, y + 1, argb);
    }

    /** Vertical separator line. */
    public static void drawVerticalSeparator(DrawContext ctx, int x, int y, int height, int argb) {
        ctx.fill(x, y, x + 1, y + height, argb);
    }

    /** Default subtle separator. */
    public static void drawSeparator(DrawContext ctx, int x, int y, int width) {
        drawHorizontalSeparator(ctx, x, y, width, HazeColors.withAlpha(HazeColors.WHITE, 0.08f));
    }

    // ═══════════════════════════════════════════════════════════════════
    //  SNAP GUIDE LINES (HUD Editor)
    // ═══════════════════════════════════════════════════════════════════

    /** Dashed horizontal snap line. */
    public static void drawHorizontalSnapLine(DrawContext ctx, int y, int screenWidth, int argb) {
        int dashLen = 4, gapLen = 3;
        for (int x = 0; x < screenWidth; x += dashLen + gapLen) {
            ctx.fill(x, y, Math.min(x + dashLen, screenWidth), y + 1, argb);
        }
    }

    /** Dashed vertical snap line. */
    public static void drawVerticalSnapLine(DrawContext ctx, int x, int screenHeight, int argb) {
        int dashLen = 4, gapLen = 3;
        for (int y = 0; y < screenHeight; y += dashLen + gapLen) {
            ctx.fill(x, y, x + 1, Math.min(y + dashLen, screenHeight), argb);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  ICONS (simple geometric)
    // ═══════════════════════════════════════════════════════════════════

    /** Draws a small filled circle (for indicators, dots). */
    public static void drawCircle(DrawContext ctx, int cx, int cy, int radius, int argb) {
        for (int y = -radius; y <= radius; y++) {
            int halfWidth = (int) Math.sqrt(radius * radius - y * y);
            ctx.fill(cx - halfWidth, cy + y, cx + halfWidth, cy + y + 1, argb);
        }
    }

    /** Star/favorite indicator (5-point, simplified as a diamond + dot). */
    public static void drawStar(DrawContext ctx, int cx, int cy, int size, int argb) {
        // Simple diamond shape for a small star icon
        for (int i = 0; i < size; i++) {
            int span = i;
            ctx.fill(cx - span, cy - size + i, cx + span + 1, cy - size + i + 1, argb);
        }
        for (int i = 0; i < size; i++) {
            int span = size - 1 - i;
            ctx.fill(cx - span, cy + i, cx + span + 1, cy + i + 1, argb);
        }
    }

    /** Draws a small "X" close icon. */
    public static void drawCloseIcon(DrawContext ctx, int x, int y, int size, int argb) {
        for (int i = 0; i < size; i++) {
            ctx.fill(x + i, y + i, x + i + 1, y + i + 1, argb);
            ctx.fill(x + size - 1 - i, y + i, x + size - i, y + i + 1, argb);
        }
    }

    /** Draws a search magnifier icon (circle + diagonal line). */
    public static void drawSearchIcon(DrawContext ctx, int x, int y, int size, int argb) {
        int r = size * 3 / 8;
        int cx = x + r + 1;
        int cy = y + r + 1;
        // Circle outline (approximate)
        drawRoundedBorder(ctx, cx - r, cy - r, r * 2, r * 2, r, argb);
        // Handle
        for (int i = 0; i < size / 3; i++) {
            ctx.fill(cx + r + i - 1, cy + r + i - 1, cx + r + i, cy + r + i, argb);
        }
    }
}
