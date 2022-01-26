package me.aurgiyalgo.nublada.world;

public class Block {

    private final int id;

    private final int topTexture;
    private final int sideTexture;

    public Block(int id, int topTexture, int sideTexture) {
        this.id = id;
        this.topTexture = topTexture;
        this.sideTexture = sideTexture;
    }

    public int getTopTexture() {
        return topTexture;
    }

    public int getSideTexture() {
        return sideTexture;
    }

    public int getId() {
        return id;
    }
}
