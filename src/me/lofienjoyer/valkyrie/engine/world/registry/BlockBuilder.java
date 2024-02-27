package me.lofienjoyer.valkyrie.engine.world.registry;

import me.lofienjoyer.valkyrie.engine.graphics.mesh.BlockMesh;
import me.lofienjoyer.valkyrie.engine.graphics.mesh.BlockMeshBuilder;
import me.lofienjoyer.valkyrie.engine.graphics.mesh.BlockMeshType;
import me.lofienjoyer.valkyrie.engine.world.Block;

public class BlockBuilder {

    private String name;

    private int topTexture;
    private int bottomTexture;
    private int northTexture;
    private int southTexture;
    private int eastTexture;
    private int westTexture;

    private boolean isTransparent;
    private boolean shouldDrawBetween;
    private boolean hasCollision = true;
    private boolean customModel;

    private float movementResistance = 0f;

    private BlockMeshType meshType = BlockMeshType.FULL;

    public Block toBlock(int id) {
        BlockMesh mesh;
        switch (meshType) {
            case X:
                mesh = BlockMeshBuilder.buildXMesh(eastTexture);
                break;
            case FULL:
            default:
                mesh = BlockMeshBuilder.buildFullBlockMesh(southTexture, eastTexture, topTexture);
                break;
        }

        var block = new Block(
                id,
                name,
                topTexture,
                bottomTexture,
                northTexture,
                southTexture,
                eastTexture,
                westTexture,
                mesh,
                meshType,
                isTransparent,
                shouldDrawBetween,
                customModel,
                movementResistance,
                hasCollision
        );

        return block;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTexture(int texture) {
        this.bottomTexture = texture;
        this.topTexture = texture;
        this.eastTexture = texture;
        this.westTexture = texture;
        this.northTexture = texture;
        this.southTexture = texture;
    }

    public int getTopTexture() {
        return topTexture;
    }

    public void setTopTexture(int topTexture) {
        this.topTexture = topTexture;
    }

    public int getBottomTexture() {
        return bottomTexture;
    }

    public void setBottomTexture(int bottomTexture) {
        this.bottomTexture = bottomTexture;
    }

    public int getNorthTexture() {
        return northTexture;
    }

    public void setNorthTexture(int northTexture) {
        this.northTexture = northTexture;
    }

    public int getSouthTexture() {
        return southTexture;
    }

    public void setSouthTexture(int southTexture) {
        this.southTexture = southTexture;
    }

    public int getEastTexture() {
        return eastTexture;
    }

    public void setEastTexture(int eastTexture) {
        this.eastTexture = eastTexture;
    }

    public int getWestTexture() {
        return westTexture;
    }

    public void setWestTexture(int westTexture) {
        this.westTexture = westTexture;
    }

    public boolean isTransparent() {
        return isTransparent;
    }

    public void setTransparent(boolean transparent) {
        isTransparent = transparent;
    }

    public boolean isShouldDrawBetween() {
        return shouldDrawBetween;
    }

    public void setShouldDrawBetween(boolean shouldDrawBetween) {
        this.shouldDrawBetween = shouldDrawBetween;
    }

    public boolean isHasCollision() {
        return hasCollision;
    }

    public void setHasCollision(boolean hasCollision) {
        this.hasCollision = hasCollision;
    }

    public boolean isCustomModel() {
        return customModel;
    }

    public void setCustomModel(boolean customModel) {
        this.customModel = customModel;
    }

    public float getMovementResistance() {
        return movementResistance;
    }

    public void setMovementResistance(float movementResistance) {
        this.movementResistance = movementResistance;
    }

    public BlockMeshType getMeshType() {
        return meshType;
    }

    public void setMeshType(BlockMeshType meshType) {
        this.meshType = meshType;
    }
}
