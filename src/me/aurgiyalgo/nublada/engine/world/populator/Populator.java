package me.aurgiyalgo.nublada.engine.world.populator;

import me.aurgiyalgo.nublada.engine.utils.PerlinNoise;
import me.aurgiyalgo.nublada.engine.world.Chunk;

public abstract class Populator {

    protected final PerlinNoise noise;

    public Populator(PerlinNoise noise) {
        this.noise = noise;
    }

    public abstract void populate(Chunk chunk);

}
