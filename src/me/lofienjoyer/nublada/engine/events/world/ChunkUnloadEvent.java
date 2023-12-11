package me.lofienjoyer.nublada.engine.events.world;

import me.lofienjoyer.nublada.engine.world.Chunk;

public class ChunkUnloadEvent extends ChunkEvent {

    public ChunkUnloadEvent(Chunk chunk) {
        super(chunk);
    }

}
