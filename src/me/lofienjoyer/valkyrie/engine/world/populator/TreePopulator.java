package me.lofienjoyer.valkyrie.engine.world.populator;

import me.lofienjoyer.valkyrie.engine.utils.PerlinNoise;
import me.lofienjoyer.valkyrie.engine.world.Chunk;

import java.util.Random;

import static me.lofienjoyer.valkyrie.engine.world.World.CHUNK_HEIGHT;
import static me.lofienjoyer.valkyrie.engine.world.World.CHUNK_WIDTH;

public class TreePopulator extends Populator {

    private static final int TREE_SIDE = 3;
    private static final int TREE_MIN_HEIGHT = 4;
    private static final int TREE_MAX_HEIGHT = 15;
    private static final int LEAVES_HEIGHT = 11;

    private static final int[] LEAVES_ARRAY = new int[] {
            6, 6, 6, 6, 6, 6, 6, 6, 6, 18, 19, 19
    };

    public TreePopulator(PerlinNoise noise) {
        super(noise);
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
                if (chunk.getBlock(treeX, y, treeZ) == 0 && chunk.getBlock(treeX, y - 1, treeZ) == 1) {
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
                chunk.setBlock(3, treeX, y + treeY, treeZ, false);
            }

            chunk.setBlock(8, treeX, treeY - 1, treeZ, false);
        }
    }

}
