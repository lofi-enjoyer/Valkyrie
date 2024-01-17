package me.lofienjoyer.valkyrie.engine.graphics.mesh;

import me.lofienjoyer.valkyrie.Valkyrie;

public class Mesh {

    private final int vaoId;
    private int vertexCount;

    public Mesh(float[] positions, int[] indices, float[] uvs, float[] normals) {
        this(Valkyrie.LOADER.loadToVAO(positions, indices, uvs, normals), indices.length);
    }

    public Mesh(float[] positions, int[] indices, float[] uvs) {
        this(Valkyrie.LOADER.loadToVAO(positions, indices, uvs), indices.length);
    }

    public Mesh(float[] positions, int[] indices) {
        this(Valkyrie.LOADER.loadToVAO(positions, indices), indices.length);
    }

    public Mesh(int[] positions, int[] indices) {
        this(Valkyrie.LOADER.loadToVAO(positions, indices), indices.length);
    }

    public Mesh(int[] positions, int[] indices, int size) {
        this(Valkyrie.LOADER.loadToVAO(positions, indices, size), indices.length);
    }

    public Mesh(float[] positions) {
        this(Valkyrie.LOADER.loadToVAO(positions), positions.length / 3);
    }

    public Mesh(int vaoId, int vertexCount) {
        this.vaoId = vaoId;
        this.vertexCount = vertexCount;
    }

    public void updateMesh(int[] positions, int[] indices, int size) {
        Valkyrie.LOADER.updateVAO(vaoId, positions, indices, size);
        this.vertexCount = indices.length;
    }

    public void updateMesh(int[] positions, int[] indices) {
        Valkyrie.LOADER.updateVAO(vaoId, positions, indices);
        this.vertexCount = indices.length;
    }

    public void updateMesh(float[] positions, int[] indices) {
        Valkyrie.LOADER.updateVAO(vaoId, positions, indices);
        this.vertexCount = indices.length;
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

}
