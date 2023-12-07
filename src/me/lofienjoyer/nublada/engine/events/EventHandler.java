package me.lofienjoyer.nublada.engine.events;

import java.util.*;

public class EventHandler {

    private final Map<Class<? extends Event>, List<EventConsumer>> listeners;
    private List<Event> eventsToProcess;
    private final List<Event> nextTickEvents;

    public EventHandler() {
        this.listeners = new HashMap<>();
        this.nextTickEvents = new ArrayList<>();
    }

    /**
     * Adds an event to be processed at the end of the current game loop.
     * @param event Event to be processed
     */
    public void process(Event event) {
        synchronized (nextTickEvents) {
            nextTickEvents.add(event);
        }
    }

    /**
     * Processes the list of pending events and then clears it.
     */
    public void update() {
        synchronized (nextTickEvents) {
            eventsToProcess = new ArrayList<>(nextTickEvents);
            nextTickEvents.clear();
        }
        eventsToProcess.forEach(event -> {
            var eventListeners = listeners.get(event.getClass());
            if (eventListeners != null)
                eventListeners.forEach(consumer -> consumer.consume(event));
        });
    }

    /**
     * Registers a listener for a certain event type. The listener should be stored, so it can be unregistered if needed.
     * @param eventType Class of the event
     * @param consumer Listener to be added
     */
    public <T extends Event> void registerListener(Class<T> eventType, EventConsumer<T> consumer) {
        var currentEventListeners = listeners.get(eventType);

        if (currentEventListeners == null) {
            listeners.put(eventType, new ArrayList<>());
            currentEventListeners = listeners.get(eventType);
        }

        currentEventListeners.add(consumer);
    }

    /**
     * Unregisters a listener of a certain event type.
     * @param eventType Class of the event
     * @param consumer Listener to be removed
     */
    public <T extends Event> void unregisterListener(Class<T> eventType, EventConsumer<T> consumer) {
        var currentEventListeners = listeners.get(eventType);

        if (currentEventListeners == null)
            return;

        currentEventListeners.remove(consumer);
    }

}
