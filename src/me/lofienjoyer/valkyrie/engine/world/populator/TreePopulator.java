package me.lofienjoyer.valkyrie.engine.world.populator;

import me.lofienjoyer.valkyrie.engine.utils.PerlinNoise;
import me.lofienjoyer.valkyrie.engine.world.Chunk;

import java.util.Random;

import static me.lofienjoyer.valkyrie.engine.world.World.CHUNK_HEIGHT;
import static me.lofienjoyer.valkyrie.engine.world.World.CHUNK_WIDTH;

public class TreePopulator extends Populator {

    public TreePopulator(PerlinNoise noise) {
        super(noise);
    }

    @Override
    public void populate(Chunk chunk) {
        Random random = new Random();
        var treeAmount = noise.noise(chunk.getPosition().x * 32, chunk.getPosition().y * 32) * 50;
        for (int i = 0; i < treeAmount; i++) {
            int treeX = random.nextInt(CHUNK_WIDTH);
            int treeZ = random.nextInt(CHUNK_WIDTH);

            int height = random.nextInt(8) + 3;

            int treeY = 0;
            for (int y = 1; y < CHUNK_HEIGHT - 15; y++) {
                if (chunk.getBlock(treeX, y, treeZ) == 0 && chunk.getBlock(treeX, y - 1, treeZ) == 1) {
                    treeY = y;
                    break;
                }
            }

            if (treeY == 0) continue;

            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = 0; z < 5; z++) {
                        if (chunk.getBlock(x + treeX - 2, y + treeY + height, z + treeZ - 2) == 0)
                            chunk.setBlock(6, x + treeX - 2, y + treeY + height, z + treeZ - 2, false);
                    }
                }
            }

            chunk.setBlock(6, treeX, treeY + height + 3, treeZ, false);

            for (int y = 0; y < height + 2; y++) {
                chunk.setBlock(3, treeX, y + treeY, treeZ, false);
            }

            chunk.setBlock(8, treeX, treeY - 1, treeZ, false);
        }
    }

}
