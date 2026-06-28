package com.hazeclient.render;

/**
 * High-performance animation system for the Haze Client UI.
 *
 * Two animation modes:
 *   1. Time-based with easing (for UI transitions, fades, slides)
 *   2. Spring physics (for natural, responsive motion — ClickGUI panels, HUD snapping)
 *
 * All methods are designed for zero-allocation in the update path.
 * Pre-allocate one HazeAnimation per animated property, call {@link #update()}
 * every frame, and read the result with {@link #get()}.
 */
public final class HazeAnimation {

    // ─── State ──────────────────────────────────────────────────────────

    private float value;
    private float startValue;
    private float targetValue;

    // Time-based
    private long   startTimeMs;
    private long   durationMs;
    private Easing easing;

    // Spring physics
    private float velocity;
    private float stiffness;
    private float damping;
    private float mass;

    private Mode mode;
    private boolean finished = true;

    // ─── Modes ──────────────────────────────────────────────────────────

    public enum Mode { TIMED, SPRING }

    // ─── Easing Functions ───────────────────────────────────────────────

    @FunctionalInterface
    public interface Easing {
        float ease(float t);
    }

    // Pre-defined easings — lambda singletons, zero allocation.
    public static final Easing LINEAR           = t -> t;
    public static final Easing EASE_IN_QUAD     = t -> t * t;
    public static final Easing EASE_OUT_QUAD    = t -> t * (2 - t);
    public static final Easing EASE_IN_OUT_QUAD = t -> t < 0.5f ? 2 * t * t : -1 + (4 - 2 * t) * t;
    public static final Easing EASE_IN_CUBIC    = t -> t * t * t;
    public static final Easing EASE_OUT_CUBIC   = t -> { float u = t - 1; return u * u * u + 1; };
    public static final Easing EASE_IN_OUT_CUBIC = t -> t < 0.5f ? 4 * t * t * t : (t - 1) * (2 * t - 2) * (2 * t - 2) + 1;

    public static final Easing EASE_OUT_EXPO    = t -> t >= 1f ? 1f : 1f - (float) Math.pow(2, -10 * t);
    public static final Easing EASE_IN_EXPO     = t -> t <= 0f ? 0f : (float) Math.pow(2, 10 * (t - 1));

    public static final Easing EASE_OUT_BACK    = t -> {
        float c1 = 1.70158f;
        float c3 = c1 + 1;
        float u = t - 1;
        return 1 + c3 * u * u * u + c1 * u * u;
    };

    public static final Easing EASE_OUT_ELASTIC = t -> {
        if (t <= 0f) return 0f;
        if (t >= 1f) return 1f;
        return (float) (Math.pow(2, -10 * t) * Math.sin((t * 10 - 0.75) * (2 * Math.PI / 3)) + 1);
    };

    public static final Easing EASE_OUT_BOUNCE  = t -> {
        if (t < 1 / 2.75f) {
            return 7.5625f * t * t;
        } else if (t < 2 / 2.75f) {
            float u = t - 1.5f / 2.75f;
            return 7.5625f * u * u + 0.75f;
        } else if (t < 2.5f / 2.75f) {
            float u = t - 2.25f / 2.75f;
            return 7.5625f * u * u + 0.9375f;
        } else {
            float u = t - 2.625f / 2.75f;
            return 7.5625f * u * u + 0.984375f;
        }
    };

    // ─── Constructor ────────────────────────────────────────────────────

    public HazeAnimation(float initialValue) {
        this.value = initialValue;
        this.startValue = initialValue;
        this.targetValue = initialValue;
        this.finished = true;
        this.mode = Mode.TIMED;
    }

    // ─── Time-Based API ─────────────────────────────────────────────────

    /**
     * Starts a timed animation toward {@code target} over {@code durationMs}
     * using the specified easing curve.
     */
    public void animateTo(float target, long durationMs, Easing easing) {
        if (Float.compare(this.targetValue, target) == 0 && !this.finished) return;
        this.startValue  = this.value;
        this.targetValue = target;
        this.durationMs  = durationMs;
        this.easing      = easing;
        this.startTimeMs = System.currentTimeMillis();
        this.finished    = false;
        this.mode        = Mode.TIMED;
    }

    /** Convenience: animate with EASE_OUT_CUBIC over 250 ms. */
    public void animateTo(float target) {
        animateTo(target, 250, EASE_OUT_CUBIC);
    }

    // ─── Spring API ─────────────────────────────────────────────────────

    /**
     * Starts a spring animation toward {@code target}.
     *
     * @param target     desired resting position
     * @param stiffness  spring constant (higher = snappier). Recommended: 100–500
     * @param damping    damping ratio (1.0 = critically damped). Recommended: 0.6–1.0
     */
    public void springTo(float target, float stiffness, float damping) {
        this.targetValue = target;
        this.stiffness   = stiffness;
        this.damping     = damping;
        this.mass        = 1.0f;
        this.finished    = false;
        this.mode        = Mode.SPRING;
    }

    /** Spring with default stiffness=200, damping=0.8 */
    public void springTo(float target) {
        springTo(target, 200f, 0.8f);
    }

    // ─── Update (call every frame) ──────────────────────────────────────

    /**
     * Advances the animation by one frame. Must be called once per render tick.
     * Uses wall-clock delta for frame-rate-independent motion.
     */
    public void update() {
        if (finished) return;

        if (mode == Mode.TIMED) {
            long elapsed = System.currentTimeMillis() - startTimeMs;
            if (elapsed >= durationMs) {
                value = targetValue;
                finished = true;
            } else {
                float t = (float) elapsed / durationMs;
                float easedT = easing != null ? easing.ease(t) : t;
                value = startValue + (targetValue - startValue) * easedT;
            }
        } else { // SPRING
            // Semi-implicit Euler integration — stable for UI spring physics.
            float dt = 0.016f; // ~60 Hz fixed timestep for consistency
            float displacement = value - targetValue;
            float springForce  = -stiffness * displacement;
            float dampingForce = -2.0f * damping * (float) Math.sqrt(stiffness / mass) * velocity;
            float acceleration = (springForce + dampingForce) / mass;

            velocity += acceleration * dt;
            value    += velocity * dt;

            // Rest detection
            if (Math.abs(velocity) < 0.01f && Math.abs(value - targetValue) < 0.01f) {
                value    = targetValue;
                velocity = 0;
                finished = true;
            }
        }
    }

    // ─── Accessors ──────────────────────────────────────────────────────

    /** Current interpolated value. */
    public float get() {
        return value;
    }

    /** Instantly set the value without animation. */
    public void set(float v) {
        this.value = v;
        this.startValue = v;
        this.targetValue = v;
        this.velocity = 0;
        this.finished = true;
    }

    /** Target value the animation is moving toward. */
    public float getTarget() {
        return targetValue;
    }

    /** True when the animation has reached its target. */
    public boolean isFinished() {
        return finished;
    }

    /** True when the animation is actively interpolating. */
    public boolean isAnimating() {
        return !finished;
    }

    /**
     * Returns the progress 0.0–1.0 of the current timed animation.
     * For spring animations, returns an approximate progress based on distance.
     */
    public float getProgress() {
        if (finished) return 1.0f;
        if (mode == Mode.TIMED) {
            long elapsed = System.currentTimeMillis() - startTimeMs;
            return Math.min(1.0f, (float) elapsed / durationMs);
        }
        // Spring: approximate
        float total = Math.abs(startValue - targetValue);
        if (total < 0.001f) return 1.0f;
        return 1.0f - Math.abs(value - targetValue) / total;
    }
}
