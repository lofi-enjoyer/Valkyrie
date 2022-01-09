package me.aurgiyalgo.nublada.world;

import me.aurgiyalgo.nublada.game.blocks.Dirt;
import me.aurgiyalgo.nublada.game.blocks.Stone;
import me.aurgiyalgo.nublada.game.blocks.Wood;

import java.util.ArrayList;
import java.util.List;

public class BlockRegistry {

    private static List<Block> blocks;

    public static void setup() {
        blocks = new ArrayList<>();

        blocks.add(new Dirt());
        blocks.add(new Stone());
        blocks.add(new Wood());
    }

    public static Block getBLock(int id) {
        return blocks.get(id - 1);
    }

}
