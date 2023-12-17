package me.lofienjoyer.valkyrie.engine.graphics.mesh;

import org.lwjgl.opengl.GL30;

public class QuadMesh {

    private static final float[] quadVertices = { // vertex attributes for a quad that fills the entire screen in Normalized Device Coordinates.
            // positions   // texCoords
            -1.0f,  1.0f,  0.0f, 1.0f,
            -1.0f, -1.0f,  0.0f, 0.0f,
            1.0f, -1.0f,  1.0f, 0.0f,

            -1.0f,  1.0f,  0.0f, 1.0f,
            1.0f, -1.0f,  1.0f, 0.0f,
            1.0f,  1.0f,  1.0f, 1.0f
    };

    int vaoId, vboId;

    public QuadMesh() {
        this.vaoId = GL30.glGenVertexArrays();
        this.vboId = GL30.glGenBuffers();
        GL30.glBindVertexArray(vaoId);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboId);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, quadVertices, GL30.GL_STATIC_DRAW);
        GL30.glEnableVertexAttribArray(0);
        GL30.glVertexAttribPointer(0, 2, GL30.GL_FLOAT, false, 4 * Float.BYTES, 0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glVertexAttribPointer(1, 2, GL30.GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
    }

    public int getVaoId() {
        return vaoId;
    }

}
