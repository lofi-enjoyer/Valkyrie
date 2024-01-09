package me.lofienjoyer.valkyrie.engine.events;

import java.util.*;

public class EventHandler {

    private final Map<Class<? extends Event>, Set<EventConsumer>> listeners;

    public EventHandler() {
        this.listeners = new HashMap<>();
    }

    /**
     * Adds an event to be processed at the end of the current game loop.
     * @param event Event to be processed
     */
    public synchronized void process(Event event) {
        var eventListeners = listeners.get(event.getClass());
        if (eventListeners != null)
            eventListeners.forEach(consumer -> consumer.consume(event));
    }

    /**
     * Registers a listener for a certain event type. The listener should be stored, so it can be unregistered if needed.
     * @param eventType Class of the event
     * @param consumer Listener to be added
     */
    public synchronized <T extends Event> void registerListener(Class<T> eventType, EventConsumer<T> consumer) {
        var currentEventListeners = listeners.get(eventType);

        if (currentEventListeners == null) {
            listeners.put(eventType, new HashSet<>());
            currentEventListeners = listeners.get(eventType);
        }

        currentEventListeners.add(consumer);
    }

    /**
     * Unregisters a listener of a certain event type.
     * @param eventType Class of the event
     * @param consumer Listener to be removed
     */
    public synchronized <T extends Event> void unregisterListener(Class<T> eventType, EventConsumer<T> consumer) {
        var currentEventListeners = listeners.get(eventType);

        if (currentEventListeners == null)
            return;

        currentEventListeners.remove(consumer);
    }

}
