package me.lofienjoyer.valkyrie.engine.world.populator;

import me.lofienjoyer.valkyrie.engine.utils.PerlinNoise;
import me.lofienjoyer.valkyrie.engine.world.Chunk;

import java.util.Random;

import static me.lofienjoyer.valkyrie.engine.world.World.CHUNK_HEIGHT;

public class CastlePopulator extends Populator {

    private static final int WIDTH = 9;
    private static final int HEIGHT = 7;
    private static final int MAX_JAILS = 7;
    private static final int MIN_JAILS = 2;

    private static final int FREQUENCY = 1;

    public CastlePopulator(PerlinNoise noise) {
        super(noise);
    }

    @Override
    public void populate(Chunk chunk) {
        double value = noise.noise(chunk.getPosition().x / (float)FREQUENCY, chunk.getPosition().y / (float)FREQUENCY);
        value = (value + 1) / 2f;

        if (value < 0.8)
            return;

        System.out.println(chunk.getPosition());

        Random random = new Random();
        int castleX = random.nextInt(16);
        int castleZ = random.nextInt(16);

        int castleY = 0;

        for (int y = 1; y < CHUNK_HEIGHT - 100; y++) {
            if (chunk.getBlock(castleX, y, castleZ) == 0 && chunk.getBlock(castleX, y - 1, castleZ) == 1) {
                castleY = y;
                break;
            }
        }

        if (castleY == 0)
            return;

        for (int x = 0; x < WIDTH; x++) {
            for (int z = 0; z < WIDTH; z++) {
                chunk.setBlock(2, castleX + x, castleY, castleZ + z, false);
            }
        }

        for (int x = 0; x < WIDTH; x++) {
            for (int z = 0; z < WIDTH; z++) {
                for (int y = 1; y < HEIGHT; y++) {
                    chunk.setBlock(15, castleX + x, castleY + y, castleZ + z, false);
                }
            }
        }

        for (int x = 1; x < WIDTH - 1; x++) {
            for (int z = 1; z < WIDTH - 1; z++) {
                for (int y = 1; y < HEIGHT - 1; y++) {
                    chunk.setBlock(0, castleX + x, castleY + y, castleZ + z, false);
                }
            }
        }

        for (int x = 3; x < WIDTH - 3; x++) {
            for (int z = 3; z < WIDTH - 3; z++) {
                chunk.setBlock(5, castleX + x, castleY + HEIGHT - 1, castleZ + z, false);
            }
        }

        for (int i = 0; i < random.nextInt(MAX_JAILS - MIN_JAILS) + MIN_JAILS; i++) {
            var jailX = random.nextInt(WIDTH - 2) + 1;
            var jailZ = random.nextInt(WIDTH - 2) + 1;
            chunk.setBlock(9, castleX + jailX, castleY + 1, castleZ + jailZ, false);
        }
    }

}
