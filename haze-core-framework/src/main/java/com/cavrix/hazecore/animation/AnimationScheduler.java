package com.cavrix.hazecore.animation;

import com.cavrix.hazecore.event.EventBus;
import com.cavrix.hazecore.event.Subscribe;
import com.cavrix.hazecore.event.TickEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AnimationScheduler {
    private final List<Animation> activeAnimations = new CopyOnWriteArrayList<>();

    public AnimationScheduler(EventBus eventBus) {
        eventBus.register(this);
    }

    public void register(Animation animation) {
        if (!activeAnimations.contains(animation)) {
            activeAnimations.add(animation);
        }
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (event.getPhase() == TickEvent.Phase.START) {
            for (Animation anim : activeAnimations) {
                anim.update();
                if (anim.isFinished()) {
                    activeAnimations.remove(anim);
                }
            }
        }
    }
}
