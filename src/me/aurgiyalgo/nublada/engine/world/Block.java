package me.aurgiyalgo.nublada.engine.world;

import me.aurgiyalgo.nublada.engine.graphics.mesh.Mesh;

public class Block {

    private final int id;

    private int topTexture;
    private int bottomTexture;
    private int northTexture;
    private int southTexture;
    private int eastTexture;
    private int westTexture;

    private Mesh mesh;

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

    public Block(int id, int texture) {
        this.id = id;
        this.topTexture = texture;
        this.bottomTexture = texture;
        this.northTexture = texture;
        this.southTexture = texture;
        this.eastTexture = texture;
        this.westTexture = texture;
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

    public void setupMesh() {
        float[] positions = {
                0, 0, 0,
                0, 1, 0,
                0, 1, 1,
                0, 0, 1,

                1, 0, 1,
                1, 1, 1,
                0, 1, 1,
                0, 0, 1,

                0, 1, 0,
                1, 1, 0,
                1, 1, 1,
                0, 1, 1
        };

        float[] uvs = {
                0, 1, southTexture,
                0, 0, southTexture,
                1, 0, southTexture,
                1, 1, southTexture,

                0, 1, eastTexture,
                0, 0, eastTexture,
                1, 0, eastTexture,
                1, 1, eastTexture,

                1, 1, topTexture,
                1, 0, topTexture,
                0, 0, topTexture,
                0, 1, topTexture
        };

        int[] indices = {
                2, 1, 0,
                0, 3, 2,

                4, 5, 6,
                6, 7, 4,

                10, 9, 8,
                8, 11, 10
        };

        this.mesh = new Mesh(positions, indices, uvs);
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

    public Mesh getMesh() {
        return mesh;
    }

    public int getId() {
        return id;
    }
}
