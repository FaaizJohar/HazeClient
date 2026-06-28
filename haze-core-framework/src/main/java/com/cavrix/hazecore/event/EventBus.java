package com.cavrix.hazecore.event;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {
    private final Map<Class<? extends Event>, List<Listener>> listeners = new ConcurrentHashMap<>();

    public void register(Object subscriber) {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        for (Method method : subscriber.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Subscribe.class) && method.getParameterCount() == 1) {
                Class<?> eventType = method.getParameterTypes()[0];
                if (Event.class.isAssignableFrom(eventType)) {
                    try {
                        method.setAccessible(true);
                        MethodHandle handle = lookup.unreflect(method);
                        Subscribe subscribe = method.getAnnotation(Subscribe.class);
                        @SuppressWarnings("unchecked")
                        Class<? extends Event> castedEventType = (Class<? extends Event>) eventType;
                        
                        listeners.computeIfAbsent(castedEventType, k -> new CopyOnWriteArrayList<>())
                                 .add(new Listener(subscriber, handle, subscribe.priority()));
                                 
                        // Sort by priority (highest to lowest ordinal)
                        listeners.get(castedEventType).sort(Comparator.comparing(Listener::getPriority));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void unregister(Object subscriber) {
        for (List<Listener> eventListeners : listeners.values()) {
            eventListeners.removeIf(listener -> listener.subscriber == subscriber);
        }
    }

    public void post(Event event) {
        List<Listener> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (Listener listener : eventListeners) {
                try {
                    listener.handle.invoke(listener.subscriber, event);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    private static class Listener {
        final Object subscriber;
        final MethodHandle handle;
        final EventPriority priority;

        Listener(Object subscriber, MethodHandle handle, EventPriority priority) {
            this.subscriber = subscriber;
            this.handle = handle;
            this.priority = priority;
        }

        public EventPriority getPriority() {
            return priority;
        }
    }
}
