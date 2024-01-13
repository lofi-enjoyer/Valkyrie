package me.lofienjoyer.valkyrie.engine.world.populator;

import me.lofienjoyer.valkyrie.engine.utils.PerlinNoise;
import me.lofienjoyer.valkyrie.engine.world.Chunk;

import java.util.Random;

import static me.lofienjoyer.valkyrie.engine.world.World.CHUNK_HEIGHT;
import static me.lofienjoyer.valkyrie.engine.world.World.CHUNK_WIDTH;

public class TallGrassPopulator extends Populator {

    private static final int MAX_PER_CHUNK = 150;

    public TallGrassPopulator(PerlinNoise noise) {
        super(noise);
    }

    @Override
    public void populate(Chunk chunk) {
        Random random = new Random();
        var grassAmount = noise.noise(chunk.getPosition().x * 32, chunk.getPosition().y * 32) * MAX_PER_CHUNK;
        for (int i = 0; i < grassAmount; i++) {
            int grassX = random.nextInt(CHUNK_WIDTH);
            int grassZ = random.nextInt(CHUNK_WIDTH);

            int grassY = 0;
            for (int y = 50; y < CHUNK_HEIGHT - 15; y++) {
                if (chunk.getBlock(grassX, y, grassZ) == 0 && chunk.getBlock(grassX, y - 1, grassZ) == 1) {
                    grassY = y;
                    break;
                }
            }

            if (grassY == 0) continue;

            chunk.setBlock(33, grassX, grassY, grassZ, false);
        }
    }

}
