package me.lofienjoyer.valkyrie.engine.world.populator;

import me.lofienjoyer.valkyrie.engine.utils.PerlinNoise;
import me.lofienjoyer.valkyrie.engine.world.Chunk;

import static me.lofienjoyer.valkyrie.engine.world.World.CHUNK_WIDTH;

public class TerrainPopulator extends Populator {

    private static final float PERSISTANCE = 0.5f;

    public TerrainPopulator(PerlinNoise noise) {
        super(noise);
    }

    public void populate(Chunk chunk) {
        int chunkX = chunk.getPosition().x * CHUNK_WIDTH;
        int chunkZ = chunk.getPosition().y * CHUNK_WIDTH;

        for (int x = 0; x < CHUNK_WIDTH; x++) {
            for (int z = 0; z < CHUNK_WIDTH; z++) {

                double noiseValue = getOctave(x + chunkX, z + chunkZ);
                double maxHeight = noiseValue * noiseValue * 120;

                for (int y = 0; y < (int)maxHeight - 2; y++) {
                    chunk.setBlock(2, x, y, z, false);
                }

                for (int y = (int)maxHeight - 2; y < (int) maxHeight; y++) {
                    chunk.setBlock(8, x, y, z, false);
                }

                chunk.setBlock(1, x, (int)maxHeight, z, false);

                // Water and sand
                if (maxHeight < 40) {
                    for (int y = 0; y < 40; y++) {
                        if (chunk.getBlock(x, y, z) == 0)
                            chunk.setBlock(7, x, y, z, false);
                    }
                }

                if (maxHeight < 45) {
                    for (int y = 0; y < 45; y++) {
                        if (chunk.getBlock(x, y, z) == 1)
                            chunk.setBlock(11, x, y, z, false);
                    }
                }
            }
        }
    }

    private double getOctave(int x, int z) {
        double octave = 0;
        float frequency = 1;
        float amplitude = 1;
        float amplitudeSum = 0;
        for (int i = 0; i < 4; i++) {
            octave += ((noise.noise(x * frequency, z * frequency) + 1) / 2f) * amplitude;

            amplitudeSum += amplitude;

            amplitude *= PERSISTANCE;
            frequency *= 2;
        }

        return octave / amplitudeSum;
    }

}
