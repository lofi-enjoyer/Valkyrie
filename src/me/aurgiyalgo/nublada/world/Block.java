package me.aurgiyalgo.nublada.world;

public class Block {

    private final int id;

    private final int topTexture;
    private final int bottomTexture;
    private final int northTexture;
    private final int southTexture;
    private final int eastTexture;
    private final int westTexture;

    public Block(int id, int topTexture, int sideTexture) {
        this.id = id;
        this.topTexture = topTexture;
        this.bottomTexture = topTexture;
        this.northTexture = sideTexture;
        this.southTexture = sideTexture;
        this.eastTexture = sideTexture;
        this.westTexture = sideTexture;
    }

    public Block(int id, int topTexture, int bottomTexture, int northTexture, int southTexture, int eastTexture, int westTexture) {
        this.id = id;
        this.topTexture = topTexture;
        this.bottomTexture = bottomTexture;
        this.northTexture = northTexture;
        this.southTexture = southTexture;
        this.eastTexture = eastTexture;
        this.westTexture = westTexture;
    }

    public int getTopTexture() {
        return topTexture;
    }

    public int getNorthTexture() {
        return northTexture;
    }

    public int getBottomTexture() {
        return bottomTexture;
    }

    public int getSouthTexture() {
        return southTexture;
    }

    public int getEastTexture() {
        return eastTexture;
    }

    public int getWestTexture() {
        return westTexture;
    }

    public int getId() {
        return id;
    }
}
