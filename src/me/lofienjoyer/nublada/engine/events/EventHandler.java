package me.lofienjoyer.nublada.engine.events;

import java.util.*;

public class EventHandler {

    private final Map<Class<? extends Event>, List<EventConsumer>> listeners;
    private final List<Event> eventsToProcess;

    public EventHandler() {
        this.listeners = new HashMap<>();
        this.eventsToProcess = new ArrayList<>();
    }

    /**
     * Adds an event to be processed at the end of the current game loop.
     * @param event Event to be processed
     */
    public void process(Event event) {
        eventsToProcess.add(event);
    }

    /**
     * Processes the list of pending events and then clears it.
     */
    public void update() {
        eventsToProcess.forEach(event -> {
            var eventListeners = listeners.get(event.getClass());
            eventListeners.forEach(consumer -> consumer.consume(event));
        });

        eventsToProcess.clear();
    }

    /**
     * Registers a listener for a certain event type. The listener should be stored, so it can be unregistered if needed.
     * @param eventType Class of the event
     * @param listener Listener to be added
     */
    public void registerListener(Class<? extends Event> eventType, EventConsumer listener) {
        var currentEventListeners = listeners.get(eventType);

        if (currentEventListeners == null) {
            listeners.put(eventType, new ArrayList<>());
            currentEventListeners = listeners.get(eventType);
        }

        currentEventListeners.add(listener);
    }

    /**
     * Unregisters a listener of a certain event type.
     * @param eventType Class of the event
     * @param listener Listener to be removed
     */
    public void unregisterListener(Class<? extends Event> eventType, EventConsumer listener) {
        var currentEventListeners = listeners.get(eventType);

        if (currentEventListeners == null)
            return;

        currentEventListeners.remove(listener);
    }

}
