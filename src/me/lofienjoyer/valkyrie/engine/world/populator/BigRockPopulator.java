package me.lofienjoyer.valkyrie.engine.world.populator;

import me.lofienjoyer.valkyrie.engine.utils.PerlinNoise;
import me.lofienjoyer.valkyrie.engine.world.BlockRegistry;
import me.lofienjoyer.valkyrie.engine.world.Chunk;

import java.util.Random;

import static me.lofienjoyer.valkyrie.engine.world.World.CHUNK_HEIGHT;
import static me.lofienjoyer.valkyrie.engine.world.World.CHUNK_WIDTH;

public class BigRockPopulator extends Populator {

    private static final int MIN_RADIUS = 2;
    private static final int MAX_RADIUS = 6;

    private final int STONE_ID;
    private final int COAL_ORE_ID;
    private final int DIRT_ID;
    private final int GRASS_BLOCK_ID;

    public BigRockPopulator(PerlinNoise noise) {
        super(noise);

        this.STONE_ID = BlockRegistry.getBlock("stone").getId();
        this.COAL_ORE_ID = BlockRegistry.getBlock("coal_ore").getId();
        this.DIRT_ID = BlockRegistry.getBlock("dirt").getId();
        this.GRASS_BLOCK_ID = BlockRegistry.getBlock("grass_block").getId();
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
            if (chunk.getBlock(rockX, y, rockZ) == 0 && chunk.getBlock(rockX, y - 1, rockZ) == STONE_ID) {
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
                    var blockToPlace = random.nextFloat() > 0.8 ? COAL_ORE_ID : STONE_ID;
                    if (currentBlock == 0 || currentBlock == GRASS_BLOCK_ID || currentBlock == DIRT_ID)
                        chunk.setBlock(blockToPlace, x + rockX, y + rockY, z + rockZ, false);
                }
            }
        }
    }

}
