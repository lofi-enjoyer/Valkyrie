package me.aurgiyalgo.nublada.graphics.model;

import me.aurgiyalgo.nublada.graphics.texture.Texture;
import me.aurgiyalgo.nublada.graphics.texture.TextureArray;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

public class ModelLoader {

    private List<Integer> vaoList, vboList, textureList;

    public ModelLoader() {
        this.vaoList = new ArrayList<>();
        this.vboList = new ArrayList<>();
        this.textureList = new ArrayList<>();
    }

    public Model loadToVAO(float[] positions, float[] textureCoords, int[] indices) {
        int vao = createVAO();
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        bindIndicesBuffer(indices);
        unbindVAO();
        return new Model(vao, indices.length);
    }

    public Model loadToVAO(float[] positions, int[] indices, float[] colors) {
        int vao = createVAO();
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 3, colors);
        bindIndicesBuffer(indices);
        unbindVAO();
        return new Model(vao, indices.length);
    }

    public Model loadToVAO(float[] positions, int[] indices) {
        int vao = createVAO();
        storeDataInAttributeList(0, 3, positions);
        bindIndicesBuffer(indices);
        unbindVAO();
        return new Model(vao, indices.length);
    }

    private int createVAO() {
        int vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);
        vaoList.add(vaoId);
        return vaoId;
    }

    public int loadTexture(String fileName) {
        Texture texture = null;
        try {
            texture = new Texture(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        int textureId = texture.getId();
        textureList.add(textureId);
        return textureId;
    }

    public int loadTextureArray(String... fileName) {
        TextureArray texture = null;
        try {
            texture = new TextureArray(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        int textureId = texture.getId();
        textureList.add(textureId);
        return textureId;
    }

    private void storeDataInAttributeList(int attributeNumber, int size, float[] data) {
        int vboId = GL30.glGenBuffers();
        vboList.add(vboId);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboId);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, data, GL30.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(attributeNumber, size, GL30.GL_FLOAT, false, 0, 0);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
    }

    private void bindIndicesBuffer(int[] indices) {
        int vboId = GL30.glGenBuffers();
        vboList.add(vboId);
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, vboId);
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indices, GL30.GL_STATIC_DRAW);
    }

    private void unbindVAO() {
        GL30.glBindVertexArray(0);
    }

    public void dispose() {
        vaoList.forEach(GL30::glDeleteVertexArrays);
        vboList.forEach(GL30::glDeleteBuffers);
        textureList.forEach(GL30::glDeleteTextures);
    }

}
