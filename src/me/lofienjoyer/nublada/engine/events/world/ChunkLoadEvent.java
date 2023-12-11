package me.lofienjoyer.nublada.engine.events.world;

import me.lofienjoyer.nublada.engine.world.Chunk;

public class ChunkLoadEvent extends ChunkEvent {

    public ChunkLoadEvent(Chunk chunk) {
        super(chunk);
    }

}
