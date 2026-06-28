package com.cavrix.hazecore.event;

public class TickEvent extends Event {
    public enum Phase {
        START,
        END
    }

    private final Phase phase;

    public TickEvent(Phase phase) {
        this.phase = phase;
    }

    public Phase getPhase() {
        return phase;
    }
}
