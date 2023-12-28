package me.lofienjoyer.valkyrie.engine.graphics.mesh;

import static org.lwjgl.opengl.GL46.*;

public class BatchMesh {

    private final int maxSize;
    private int vaoId, vboId;

    public BatchMesh(int maxSize) {
        this.maxSize = maxSize;

        setup();
    }

    private void setup() {
        this.vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        this.vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);

        glBufferData(GL_ARRAY_BUFFER, maxSize, GL_STREAM_DRAW);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void updateMesh(float[] data) {
        glBindVertexArray(vaoId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);

        glBufferSubData(GL_ARRAY_BUFFER, 0, data);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public int getVaoId() {
        return vaoId;
    }
}
