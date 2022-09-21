package me.lofienjoyer.nublada.engine.graphics.mesh;

import me.lofienjoyer.nublada.Nublada;

public class Mesh {

    private final int vaoId;
    private final int vertexCount;
    private int textureId;

    public Mesh(float[] positions, int[] indices, float[] uvs, float[] normals) {
        this(Nublada.LOADER.loadToVAO(positions, indices, uvs, normals), indices.length);
    }

    public Mesh(float[] positions, int[] indices, float[] uvs) {
        this(Nublada.LOADER.loadToVAO(positions, indices, uvs), indices.length);
    }

    public Mesh(float[] positions, int[] indices) {
        this(Nublada.LOADER.loadToVAO(positions, indices), indices.length);
    }

    public Mesh(float[] positions) {
        this(Nublada.LOADER.loadToVAO(positions), positions.length / 3);
    }

    private Mesh(int vaoId, int vertexCount) {
        this.vaoId = vaoId;
        this.vertexCount = vertexCount;
    }

    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getTextureId() {
        return textureId;
    }
}
