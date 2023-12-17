package me.lofienjoyer.valkyrie.engine.events.world;

import me.lofienjoyer.valkyrie.engine.events.Event;
import me.lofienjoyer.valkyrie.engine.world.Chunk;

public abstract class ChunkEvent implements Event {

    private final Chunk chunk;

    public ChunkEvent(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }

}
