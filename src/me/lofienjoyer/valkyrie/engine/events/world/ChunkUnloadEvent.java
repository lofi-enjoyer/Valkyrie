package me.lofienjoyer.valkyrie.engine.events.world;

import me.lofienjoyer.valkyrie.engine.world.Chunk;

public class ChunkUnloadEvent extends ChunkEvent {

    public ChunkUnloadEvent(Chunk chunk) {
        super(chunk);
    }

}
