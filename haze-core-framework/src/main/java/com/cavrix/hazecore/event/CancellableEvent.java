package com.cavrix.hazecore.event;

public abstract class CancellableEvent extends Event {
    private boolean cancelled;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
