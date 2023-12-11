package me.lofienjoyer.nublada.engine.events.world;

import me.lofienjoyer.nublada.engine.events.Event;
import me.lofienjoyer.nublada.engine.world.Chunk;

public abstract class ChunkEvent implements Event {

    private final Chunk chunk;

    public ChunkEvent(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }

}
