package com.cavrix.hazecore.render;

import com.cavrix.hazecore.event.EventBus;
import com.cavrix.hazecore.event.Render2DEvent;
import com.cavrix.hazecore.event.Subscribe;

public class RenderEngine {
    private final EventBus eventBus;

    public RenderEngine(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
    }

    @Subscribe
    public void onRender2D(Render2DEvent event) {
        // Called every frame by the injected hook
        // Setup OpenGL state for 2D rendering
        
        // Dispatch to HUD elements
        
        // Restore OpenGL state
    }
    
    /**
     * Called statically by the injected ASM hook at the end of the game render loop.
     * This bridges the static ASM hook to the instance-based RenderEngine.
     */
    public static void onRender(float partialTicks) {
        // In a real implementation, HazeCoreAgent would have a static instance of EventBus
        // that we can post to.
        // HazeCoreAgent.getInstance().getEventBus().post(new Render2DEvent(partialTicks));
    }
}
