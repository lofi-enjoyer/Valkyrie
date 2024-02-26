package me.lofienjoyer.valkyrie.engine.world;

import me.lofienjoyer.valkyrie.engine.graphics.mesh.BlockMesh;
import me.lofienjoyer.valkyrie.engine.graphics.mesh.BlockMeshBuilder;
import me.lofienjoyer.valkyrie.engine.graphics.mesh.BlockMeshType;
import me.lofienjoyer.valkyrie.engine.graphics.mesh.Mesh;

public class Block {

    private final int id;

    private String name;

    private final int topTexture;
    private final int bottomTexture;
    private final int northTexture;
    private final int southTexture;
    private final int eastTexture;
    private final int westTexture;

    private final BlockMesh mesh;
    private final BlockMeshType meshType;

    private final boolean isTransparent;
    private final boolean shouldDrawBetween;
    private final boolean hasCollision;
    private final boolean customModel;

    private final float movementResistance;

    public Block(int id, String name, int topTexture, int bottomTexture, int northTexture, int southTexture, int eastTexture, int westTexture, BlockMesh mesh, BlockMeshType meshType, boolean isTransparent, boolean shouldDrawBetween, boolean customModel, float movementResistance, boolean hasCollision) {
        this.id = id;
        this.name = name;
        this.topTexture = topTexture;
        this.bottomTexture = bottomTexture;
        this.northTexture = northTexture;
        this.southTexture = southTexture;
        this.eastTexture = eastTexture;
        this.westTexture = westTexture;
        this.mesh = mesh;
        this.meshType = meshType;
        this.isTransparent = isTransparent;
        this.shouldDrawBetween = shouldDrawBetween;
        this.customModel = customModel;
        this.movementResistance = movementResistance;
        this.hasCollision = hasCollision;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTopTexture() {
        return topTexture;
    }

    public int getBottomTexture() {
        return bottomTexture;
    }

    public int getNorthTexture() {
        return northTexture;
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

    public BlockMesh getMesh() {
        return mesh;
    }

    public BlockMeshType getMeshType() {
        return meshType;
    }

    public boolean isTransparent() {
        return isTransparent;
    }

    public boolean shouldDrawBetween() {
        return shouldDrawBetween;
    }

    public boolean hasCollision() {
        return hasCollision;
    }

    public boolean isCustomModel() {
        return customModel;
    }

    public float getMovementResistance() {
        return movementResistance;
    }

}
