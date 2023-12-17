package me.lofienjoyer.valkyrie.engine.events.world;

import me.lofienjoyer.valkyrie.engine.world.Chunk;
import org.joml.Vector3i;

public class ChunkUpdateEvent extends ChunkEvent {

    private final Vector3i updatePosition;

    public ChunkUpdateEvent(Chunk chunk, Vector3i updatePosition) {
        super(chunk);
        this.updatePosition = updatePosition;
    }

    public Vector3i getUpdatePosition() {
        return updatePosition;
    }

}
