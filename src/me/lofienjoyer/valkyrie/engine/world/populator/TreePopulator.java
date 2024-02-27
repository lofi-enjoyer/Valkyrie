package me.lofienjoyer.valkyrie.engine.world.populator;

import me.lofienjoyer.valkyrie.engine.utils.PerlinNoise;
import me.lofienjoyer.valkyrie.engine.world.BlockRegistry;
import me.lofienjoyer.valkyrie.engine.world.Chunk;

import java.util.Random;

import static me.lofienjoyer.valkyrie.engine.world.World.CHUNK_HEIGHT;
import static me.lofienjoyer.valkyrie.engine.world.World.CHUNK_WIDTH;

public class TreePopulator extends Populator {

    private static final int TREE_SIDE = 3;
    private static final int TREE_MIN_HEIGHT = 4;
    private static final int TREE_MAX_HEIGHT = 15;
    private static final int LEAVES_HEIGHT = 11;

    private final int LEAVES_ID;
    private final int APPLE_LEAVES_ID;
    private final int CHERRY_LEAVES_ID;
    private final int LOG_ID;
    private final int GRASS_BLOCK_ID;
    private final int DIRT_ID;

    private final int[] LEAVES_ARRAY;

    public TreePopulator(PerlinNoise noise) {
        super(noise);

        this.LEAVES_ID = BlockRegistry.getBlock("leaves").getId();
        this.APPLE_LEAVES_ID = BlockRegistry.getBlock("apple_leaves").getId();
        this.CHERRY_LEAVES_ID = BlockRegistry.getBlock("cherry_leaves").getId();
        this.GRASS_BLOCK_ID = BlockRegistry.getBlock("grass_block").getId();
        this.DIRT_ID = BlockRegistry.getBlock("dirt").getId();
        this.LOG_ID = BlockRegistry.getBlock("wood_log").getId();

        LEAVES_ARRAY = new int[] {
                LEAVES_ID, LEAVES_ID, LEAVES_ID, LEAVES_ID, LEAVES_ID, LEAVES_ID, LEAVES_ID, LEAVES_ID, LEAVES_ID, CHERRY_LEAVES_ID, APPLE_LEAVES_ID, APPLE_LEAVES_ID
        };
    }

    @Override
    public void populate(Chunk chunk) {
        Random random = new Random();
        var treeAmount = noise.noise(chunk.getPosition().x * 32, chunk.getPosition().y * 32);
        treeAmount = Math.sqrt(treeAmount) * 20;
        for (int i = 0; i < treeAmount; i++) {
            int treeX = random.nextInt(CHUNK_WIDTH);
            int treeZ = random.nextInt(CHUNK_WIDTH);

            int height = random.nextInt(TREE_MAX_HEIGHT - TREE_MIN_HEIGHT) + TREE_MIN_HEIGHT;

            int treeY = 0;
            for (int y = 1; y < CHUNK_HEIGHT - 15; y++) {
                if (chunk.getBlock(treeX, y, treeZ) == 0 && chunk.getBlock(treeX, y - 1, treeZ) == GRASS_BLOCK_ID) {
                    treeY = y;
                    break;
                }
            }

            if (treeY == 0) continue;

            var leavesType = LEAVES_ARRAY[random.nextInt(LEAVES_ARRAY.length)];

            for (int x = -TREE_SIDE; x <= TREE_SIDE; x++) {
                for (int z = -TREE_SIDE; z <= TREE_SIDE; z++) {
                    for (int y = 0; y < LEAVES_HEIGHT; y++) {
                        var distance = x * x + z * z;
                        if (random.nextFloat() < (1.25 - y/14f - distance/16f) && chunk.getBlock(x + treeX, y + treeY + height, z + treeZ) == 0) {
                            chunk.setBlock(leavesType, x + treeX, y + treeY + height, z + treeZ, false);
                        }
                    }
                }
            }

            for (int y = 0; y < height + 5; y++) {
                chunk.setBlock(LOG_ID, treeX, y + treeY, treeZ, false);
            }

            chunk.setBlock(DIRT_ID, treeX, treeY - 1, treeZ, false);
        }
    }

}
