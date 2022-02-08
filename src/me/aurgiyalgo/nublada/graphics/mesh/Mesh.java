package me.aurgiyalgo.nublada.graphics.mesh;

public class Mesh {

    private final int vao;
    private final int vertexCount;
    private int textureId;

    public Mesh(int vao, int vertexCount) {
        this.vao = vao;
        this.vertexCount = vertexCount;
    }

    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }

    public int getVao() {
        return vao;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getTextureId() {
        return textureId;
    }
}
