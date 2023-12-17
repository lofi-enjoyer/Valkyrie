package me.lofienjoyer.valkyrie.engine.events;

public interface EventConsumer<T extends Event> {

    void consume(T event);

}
