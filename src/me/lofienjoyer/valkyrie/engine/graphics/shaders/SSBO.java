package me.lofienjoyer.valkyrie.engine.graphics.shaders;

import static org.lwjgl.opengl.GL46.*;

public class SSBO {

    private final int id;

    public SSBO() {
        this.id = glGenBuffers();
    }

    public void setData(float[] data) {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, id);
        glBufferData(GL_SHADER_STORAGE_BUFFER, data, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    public void setSubData(float[] data, int offset) {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, id);
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, offset, data);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    public void resize(int sizeInBytes) {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, id);
        glBufferData(GL_SHADER_STORAGE_BUFFER, sizeInBytes, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    public void bind() {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, id);
    }

    public void bindIndex(int index) {
        bind();
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, index, id);
    }

    public void dispose() {
        glDeleteBuffers(id);
    }

    public int getId() {
        return id;
    }

}
