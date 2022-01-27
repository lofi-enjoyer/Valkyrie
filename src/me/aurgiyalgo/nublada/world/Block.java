package me.aurgiyalgo.nublada.world;

public class Block {

    private final int id;

    private int topTexture;
    private int bottomTexture;
    private int northTexture;
    private int southTexture;
    private int eastTexture;
    private int westTexture;

    private boolean isTransparent;

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

    public void setTopTexture(int topTexture) {
        this.topTexture = topTexture;
    }

    public void setBottomTexture(int bottomTexture) {
        this.bottomTexture = bottomTexture;
    }

    public void setNorthTexture(int northTexture) {
        this.northTexture = northTexture;
    }

    public void setSouthTexture(int southTexture) {
        this.southTexture = southTexture;
    }

    public void setEastTexture(int eastTexture) {
        this.eastTexture = eastTexture;
    }

    public void setWestTexture(int westTexture) {
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

    public boolean isTransparent() {
        return isTransparent;
    }

    public void setTransparent(boolean transparent) {
        isTransparent = transparent;
    }

    public int getId() {
        return id;
    }
}
