package me.lofienjoyer.valkyrie.engine.world.populator;

import me.lofienjoyer.valkyrie.engine.utils.PerlinNoise;
import me.lofienjoyer.valkyrie.engine.world.Chunk;

import java.util.Random;

import static me.lofienjoyer.valkyrie.engine.world.World.CHUNK_HEIGHT;
import static me.lofienjoyer.valkyrie.engine.world.World.CHUNK_WIDTH;

public class BigRockPopulator extends Populator {

    private static final int MIN_RADIUS = 2;
    private static final int MAX_RADIUS = 6;

    public BigRockPopulator(PerlinNoise noise) {
        super(noise);
    }

    @Override
    public void populate(Chunk chunk) {
        Random random = new Random();

        if (random.nextFloat() < 0.95)
            return;

        var rockRadius = random.nextInt(MAX_RADIUS - MIN_RADIUS + 1) + MIN_RADIUS;
        int rockX = random.nextInt(CHUNK_WIDTH);
        int rockZ = random.nextInt(CHUNK_WIDTH);

        int rockY = 0;
        for (int y = 1; y < CHUNK_HEIGHT - 15; y++) {
            if (chunk.getBlock(rockX, y, rockZ) == 0 && chunk.getBlock(rockX, y - 1, rockZ) == 1) {
                rockY = y;
                break;
            }
        }

        if (rockY == 0)
            return;

        for (int x = -rockRadius; x <= rockRadius; x++) {
            for (int y = -rockRadius; y <= rockRadius; y++) {
                for (int z = -rockRadius; z <= rockRadius; z++) {
                    if (x*x + y*y + z*z >= rockRadius * rockRadius || random.nextFloat() < 0.2)
                        continue;

                    var currentBlock = chunk.getBlock(x + rockX, y + rockY, z + rockZ);
                    var blockToPlace = random.nextFloat() > 0.8 ? 10 : 2;
                    if (currentBlock == 0 || currentBlock == 1 || currentBlock == 8)
                        chunk.setBlock(blockToPlace, x + rockX, y + rockY, z + rockZ, false);
                }
            }
        }
    }

}
