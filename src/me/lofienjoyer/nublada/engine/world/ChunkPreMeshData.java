package me.lofienjoyer.nublada.engine.world;

import static me.lofienjoyer.nublada.engine.world.World.CHUNK_HEIGHT;
import static me.lofienjoyer.nublada.engine.world.World.CHUNK_WIDTH;

public class ChunkPreMeshData {

    private static final int NORTH = 0;
    private static final int SOUTH = 2;
    private static final int EAST = 1;
    private static final int WEST = 3;

    private final short[] chunkData;
    private final short[][] neighborsData;

    public ChunkPreMeshData(Chunk chunk) {
        if (chunk.getVoxels() != null) {
            this.chunkData = chunk.getVoxels().clone();
        } else {
            this.chunkData = chunk.decompress(chunk.getCompressedData());
        }

        this.neighborsData = new short[chunk.getNeighbors().length][CHUNK_WIDTH * CHUNK_HEIGHT * CHUNK_WIDTH];
        for (int i = 0; i < chunk.getNeighbors().length; i++) {
            Chunk neighbor = chunk.getNeighbors()[i];
            if (neighbor == null || neighbor.getState() == ChunkState.UNLOADED)
                continue;

            if (neighbor.getVoxels() != null) {
                neighborsData[i] = neighbor.getVoxels().clone();
            } else {
                neighborsData[i] = neighbor.decompress(neighbor.getCompressedData());
            }
        }
    }

    public int getBlock(int x, int y, int z) {
        if (chunkData == null) return 0;
        if (y < 0 || y > CHUNK_HEIGHT - 1) return 0;
        if (x < 0) {
            return neighborsData[EAST] != null ? neighborsData[EAST][x + CHUNK_WIDTH | y << 5 | z << 13] : 0;
        }
        if (x > CHUNK_WIDTH - 1) {
            return neighborsData[WEST] != null ? neighborsData[WEST][x - CHUNK_WIDTH | y << 5 | z << 13] : 0;
        }
        if (z < 0) {
            return neighborsData[NORTH] != null ? neighborsData[NORTH][x | y << 5 | z + CHUNK_WIDTH << 13] : 0;
        }
        if (z > CHUNK_WIDTH - 1) {
            return neighborsData[SOUTH] != null ? neighborsData[SOUTH][x | y << 5 | z - CHUNK_WIDTH << 13] : 0;
        }
        return chunkData[x | y << 5 | z << 13];
    }

}
