package me.lofienjoyer.valkyrie.engine.world;

import me.lofienjoyer.valkyrie.engine.graphics.mesh.BlockMesh;
import me.lofienjoyer.valkyrie.engine.graphics.mesh.BlockMeshBuilder;
import me.lofienjoyer.valkyrie.engine.graphics.mesh.BlockMeshType;
import me.lofienjoyer.valkyrie.engine.graphics.mesh.Mesh;

public class Block {

    private final int id;

    private int topTexture;
    private int bottomTexture;
    private int northTexture;
    private int southTexture;
    private int eastTexture;
    private int westTexture;

    private BlockMesh mesh;
    private BlockMeshType meshType = BlockMeshType.FULL;

    private boolean isTransparent;
    private boolean shouldDrawBetween;
    private boolean hasCollision = true;
    private boolean customModel;

    private float movementResistance = 0f;

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
        switch (meshType) {
            case FULL:
                this.mesh = BlockMeshBuilder.buildFullBlockMesh(southTexture, eastTexture, topTexture);
                break;
            case X:
                this.mesh = BlockMeshBuilder.buildXMesh(eastTexture);
                break;
        }
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

    public boolean shouldDrawBetween() {
        return shouldDrawBetween;
    }

    public void setShouldDrawBetween(boolean shouldDrawBetween) {
        this.shouldDrawBetween = shouldDrawBetween;
    }

    public boolean hasCollision() {
        return hasCollision;
    }

    public void setHasCollision(boolean hasCollision) {
        this.hasCollision = hasCollision;
    }

    public float getMovementResistance() {
        return movementResistance;
    }

    public void setMovementResistance(float movementResistance) {
        this.movementResistance = movementResistance;
    }

    public BlockMesh getMesh() {
        return mesh;
    }

    public int getId() {
        return id;
    }

    public boolean isCustomModel() {
        return customModel;
    }

    public void setCustomModel(boolean customModel) {
        this.customModel = customModel;
    }

    public BlockMeshType getMeshType() {
        return meshType;
    }

    public void setMeshType(BlockMeshType meshType) {
        this.meshType = meshType;
    }

}
