package me.lofienjoyer.valkyrie.engine.world.populator;

import me.lofienjoyer.valkyrie.engine.utils.PerlinNoise;
import me.lofienjoyer.valkyrie.engine.world.Chunk;

import java.util.Random;

import static me.lofienjoyer.valkyrie.engine.world.World.CHUNK_HEIGHT;

public class CastlePopulator extends Populator{

    public CastlePopulator(PerlinNoise noise) {
        super(noise);
    }

    @Override
    public void populate(Chunk chunk) {
        double value = noise.noise(chunk.getPosition().x * 32, chunk.getPosition().y * 32);
        value = (value + 1) / 2f;

        if (value < 0.7)
            return;

        Random random = new Random();
        int castleX = random.nextInt(16);
        int castleZ = random.nextInt(16);

        int castleY = 200;

        for (int y = 1; y < CHUNK_HEIGHT - 8; y++) {
            if (chunk.getBlock(castleX, y, castleZ) == 0 && chunk.getBlock(castleX, y - 1, castleZ) == 1) {
                castleY = y;
                break;
            }
        }

        for (int x = 0; x < 9; x++) {
            for (int z = 0; z < 9; z++) {
                for (int y = 0; y < 5; y++) {
                    chunk.setBlock(2, castleX + x, castleY + y, castleZ + z, false);
                }
            }
        }
    }

}
