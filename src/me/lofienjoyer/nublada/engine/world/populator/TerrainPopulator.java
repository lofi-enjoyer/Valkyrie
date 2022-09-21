package me.lofienjoyer.nublada.engine.world.populator;

import me.lofienjoyer.nublada.engine.utils.PerlinNoise;
import me.lofienjoyer.nublada.engine.world.Chunk;

import static me.lofienjoyer.nublada.engine.world.World.CHUNK_WIDTH;

public class TerrainPopulator extends Populator {

    public TerrainPopulator(PerlinNoise noise) {
        super(noise);
    }

    public void populate(Chunk chunk) {
        int chunkX = chunk.getPosition().x * CHUNK_WIDTH;
        int chunkZ = chunk.getPosition().y * CHUNK_WIDTH;

        byte voxel;
        for (int x = 0; x < CHUNK_WIDTH; x++) {
            for (int z = 0; z < CHUNK_WIDTH; z++) {

                double noiseValue = (noise.noise(x + chunkX, z + chunkZ) + 1) / 2f;
                double maxHeight = noiseValue * noiseValue * 250;

                for (int y = 0; y < maxHeight; y++) {
                    voxel = 2;
                    if ((int) (maxHeight) == y) {
                        voxel = 1;
                    }
                    chunk.setBlock(voxel, x, y, z, false);
                }

                // Water and sand
                if (maxHeight < 40) {
                    for (int y = 0; y < 40; y++) {
                        if (chunk.getBlock(x, y, z) == 0)
                            chunk.setBlock(7, x, y, z, false);
                        else if (chunk.getBlock(x, y, z) == 1)
                            chunk.setBlock(11, x, y, z, false);
                    }
                }
            }
        }
    }
}
