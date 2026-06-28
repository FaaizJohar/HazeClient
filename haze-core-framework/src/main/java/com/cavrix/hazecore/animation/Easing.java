package com.cavrix.hazecore.animation;

public interface Easing {
    float ease(float t);

    // Pre-defined easings
    Easing LINEAR = t -> t;
    
    Easing EASE_IN_QUAD = t -> t * t;
    Easing EASE_OUT_QUAD = t -> t * (2 - t);
    Easing EASE_IN_OUT_QUAD = t -> t < .5 ? 2 * t * t : -1 + (4 - 2 * t) * t;

    Easing EASE_IN_CUBIC = t -> t * t * t;
    Easing EASE_OUT_CUBIC = t -> (--t) * t * t + 1;
    Easing EASE_IN_OUT_CUBIC = t -> t < .5 ? 4 * t * t * t : (t - 1) * (2 * t - 2) * (2 * t - 2) + 1;
    
    // Custom Cubic Bézier approximation (simplified)
    static Easing cubicBezier(float x1, float y1, float x2, float y2) {
        // Precise cubic bezier would require root finding, falling back to a simpler approximation for performance
        return t -> {
            float u = 1 - t;
            return 3 * u * u * t * y1 + 3 * u * t * t * y2 + t * t * t;
        };
    }
}
