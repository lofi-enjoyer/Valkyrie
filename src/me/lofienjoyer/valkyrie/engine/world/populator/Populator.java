package me.lofienjoyer.valkyrie.engine.world.populator;

import me.lofienjoyer.valkyrie.engine.utils.PerlinNoise;
import me.lofienjoyer.valkyrie.engine.world.Chunk;

public abstract class Populator {

    protected final PerlinNoise noise;

    public Populator(PerlinNoise noise) {
        this.noise = noise;
    }

    public abstract void populate(Chunk chunk);

}
