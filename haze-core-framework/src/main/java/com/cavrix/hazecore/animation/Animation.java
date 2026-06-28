package com.cavrix.hazecore.animation;

public class Animation {
    private float startValue;
    private float targetValue;
    private float currentValue;
    
    private long startTime;
    private long durationMs;
    private Easing easing;
    private boolean finished;

    public Animation(float initialValue) {
        this.startValue = initialValue;
        this.targetValue = initialValue;
        this.currentValue = initialValue;
        this.finished = true;
    }

    public void animateTo(float target, long durationMs, Easing easing) {
        if (this.targetValue == target) return;
        
        this.startValue = this.currentValue;
        this.targetValue = target;
        this.durationMs = durationMs;
        this.easing = easing;
        this.startTime = System.currentTimeMillis();
        this.finished = false;
    }

    public void update() {
        if (finished) return;

        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed >= durationMs) {
            this.currentValue = targetValue;
            this.finished = true;
        } else {
            float t = (float) elapsed / durationMs;
            float easedT = easing.ease(t);
            this.currentValue = startValue + (targetValue - startValue) * easedT;
        }
    }

    public float getValue() {
        return currentValue;
    }

    public void setValue(float value) {
        this.currentValue = value;
        this.startValue = value;
        this.targetValue = value;
        this.finished = true;
    }
    
    public boolean isFinished() {
        return finished;
    }
}
