package me.aurgiyalgo.nublada.game.blocks;

import me.aurgiyalgo.nublada.world.Block;

public class Dirt extends Block {

    @Override
    public int getTopTexture() {
        return 2;
    }

    @Override
    public int getSideTexture() {
        return 1;
    }

}
