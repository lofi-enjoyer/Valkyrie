package me.lofienjoyer.valkyrie.engine.world.populator;

import me.lofienjoyer.valkyrie.engine.utils.PerlinNoise;
import me.lofienjoyer.valkyrie.engine.world.BlockRegistry;
import me.lofienjoyer.valkyrie.engine.world.Chunk;

import java.util.Random;

import static me.lofienjoyer.valkyrie.engine.world.World.CHUNK_HEIGHT;
import static me.lofienjoyer.valkyrie.engine.world.World.CHUNK_WIDTH;

public class GrassPopulator extends Populator {

    private static final int MAX_PER_CHUNK = 600;

    private final int GRASS_BLOCK_ID;
    private final int TALL_GRASS_ID;
    private final int SHORT_GRASS_ID;
    private final int CORNFLOWER_ID;

    public GrassPopulator(PerlinNoise noise) {
        super(noise);

        this.GRASS_BLOCK_ID = BlockRegistry.getBlock("grass_block").getId();
        this.TALL_GRASS_ID = BlockRegistry.getBlock("tall_grass").getId();
        this.SHORT_GRASS_ID = BlockRegistry.getBlock("short_grass").getId();
        this.CORNFLOWER_ID = BlockRegistry.getBlock("cornflower").getId();
    }

    @Override
    public void populate(Chunk chunk) {
        Random random = new Random();
        var grassAmount = noise.noise(chunk.getPosition().x * 32, chunk.getPosition().y * 32);
        grassAmount = Math.sqrt(1 - grassAmount) * MAX_PER_CHUNK;
        for (int i = 0; i < grassAmount; i++) {
            int grassX = random.nextInt(CHUNK_WIDTH);
            int grassZ = random.nextInt(CHUNK_WIDTH);

            int grassY = 0;
            for (int y = 40; y < CHUNK_HEIGHT - 15; y++) {
                if (chunk.getBlock(grassX, y, grassZ) == 0 && chunk.getBlock(grassX, y - 1, grassZ) == GRASS_BLOCK_ID) {
                    grassY = y;
                    break;
                }
            }

            if (grassY == 0) continue;

            var blockId = SHORT_GRASS_ID;
            var grassDice = random.nextFloat();
            if (grassDice > 0.97) {
                blockId = CORNFLOWER_ID;
            } else if (grassDice > 0.8) {
                blockId = TALL_GRASS_ID;
            }

            chunk.setBlock(blockId, grassX, grassY, grassZ, false);
        }
    }

}
