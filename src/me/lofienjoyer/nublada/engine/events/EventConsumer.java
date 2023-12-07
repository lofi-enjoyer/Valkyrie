package me.lofienjoyer.nublada.engine.events;

public interface EventConsumer<T extends Event> {

    void consume(T event);

}
