package me.lofienjoyer.nublada.engine.world;

import org.joml.Vector2i;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.Map;

public class FutureChunk {

    private final Vector2i position;

    private final Map<Vector3i, Short> futureBlocks;

    public FutureChunk(Vector2i position) {
        this.position = position;
        this.futureBlocks = new HashMap<>();
    }

    public void addBlock(short id, int x, int y, int z) {
        futureBlocks.put(new Vector3i(x, y ,z), id);
    }

    public void setBlocksInChunk(Chunk chunk) {
        futureBlocks.forEach((position, id) -> {
            chunk.setBlock(id, position.x, position.y, position.z, false);
        });
    }

    public Vector2i getPosition() {
        return position;
    }
}
