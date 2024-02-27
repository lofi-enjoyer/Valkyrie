package me.lofienjoyer.valkyrie.engine.world.populator;

import me.lofienjoyer.valkyrie.engine.utils.PerlinNoise;
import me.lofienjoyer.valkyrie.engine.world.BlockRegistry;
import me.lofienjoyer.valkyrie.engine.world.Chunk;

import java.util.Random;

import static me.lofienjoyer.valkyrie.engine.world.World.CHUNK_HEIGHT;

public class CastlePopulator extends Populator {

    private static final int HEIGHT = 25;
    private static final int RADIUS = 6;

    private static final int FREQUENCY = 1000;

    private final int GRASS_BLOCK_ID;
    private final int STONE_BRICK_ID;

    public CastlePopulator(PerlinNoise noise) {
        super(noise);

        this.GRASS_BLOCK_ID = BlockRegistry.getBlock("grass_block").getId();
        this.STONE_BRICK_ID = BlockRegistry.getBlock("stone_brick").getId();
    }

    @Override
    public void populate(Chunk chunk) {
        var random = new Random();
        double value = noise.noise((chunk.getPosition().x * 32) * (float)FREQUENCY, (chunk.getPosition().y * 32) * (float)FREQUENCY);
        value = (value + 1) / 2f;

        if (value < 0.8)
            return;

        int castleX = random.nextInt(16);
        int castleZ = random.nextInt(16);

        int castleY = 0;

        for (int y = 1; y < CHUNK_HEIGHT - 30; y++) {
            if (chunk.getBlock(castleX, y, castleZ) == 0 && chunk.getBlock(castleX, y - 1, castleZ) == GRASS_BLOCK_ID) {
                castleY = y;
                break;
            }
        }

        if (castleY == 0)
            return;

        castleY -= 10;

        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int z = -RADIUS; z <= RADIUS; z++) {
                if (x*x + z*z >= RADIUS * RADIUS)
                    continue;

                for (int y = 0; y < HEIGHT; y++) {
                    chunk.setBlock(0, x + castleX, y + castleY, z + castleZ, false);
                }
            }
        }

        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int z = -RADIUS; z <= RADIUS; z++) {
                if (x*x + z*z >= RADIUS * RADIUS)
                    continue;

                chunk.setBlock(STONE_BRICK_ID, x + castleX, castleY, z + castleZ, false);
            }
        }

        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int z = -RADIUS; z <= RADIUS; z++) {
                if (Math.abs(x*x + z*z - RADIUS * RADIUS) > 4)
                    continue;

                for (int y = 0; y < HEIGHT; y++) {
                    if (random.nextFloat() > 0.05)
                        chunk.setBlock(STONE_BRICK_ID, x + castleX, y + castleY, z + castleZ, false);
                }
            }
        }

        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int z = -RADIUS; z <= RADIUS; z++) {
                if (x*x + z*z >= RADIUS * RADIUS)
                    continue;

                for (int y = 0; y < HEIGHT; y += 10) {
                    chunk.setBlock(STONE_BRICK_ID, x + castleX, y + castleY, z + castleZ, false);
                }

            }
        }

        for (int y = 0; y < HEIGHT; y++) {
            int stepX = (int) ((RADIUS - 1) * Math.sin(y * 0.25));
            int stepZ = (int) ((RADIUS - 1) * Math.cos(y * 0.25));

            chunk.setBlock(STONE_BRICK_ID, stepX + castleX, y + castleY, stepZ + castleZ, false);
        }

    }

}
