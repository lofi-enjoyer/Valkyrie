package me.lofienjoyer.valkyrie.engine.events.world;

import me.lofienjoyer.valkyrie.engine.world.Chunk;

public class ChunkLoadEvent extends ChunkEvent {

    public ChunkLoadEvent(Chunk chunk) {
        super(chunk);
    }

}
