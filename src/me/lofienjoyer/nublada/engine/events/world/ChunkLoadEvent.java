package me.lofienjoyer.nublada.engine.events.world;

import me.lofienjoyer.nublada.engine.events.Event;
import me.lofienjoyer.nublada.engine.world.Chunk;

public class ChunkLoadEvent implements Event {

    private final Chunk chunk;

    public ChunkLoadEvent(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }

}
