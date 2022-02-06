package me.aurgiyalgo.nublada.graphics.loader;

import me.aurgiyalgo.nublada.Nublada;
import me.aurgiyalgo.nublada.graphics.mesh.Mesh;
import me.aurgiyalgo.nublada.graphics.texture.Texture;
import me.aurgiyalgo.nublada.graphics.texture.TextureArray;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load;

public class Loader {

    private final List<Integer> vaoList;
    private final List<Integer> vboList;
    private final List<Integer> textureList;

    public Loader() {
        this.vaoList = new ArrayList<>();
        this.vboList = new ArrayList<>();
        this.textureList = new ArrayList<>();
    }

    public Mesh loadToVAO(float[] positions, int[] indices, float[] uvs, float[] normals) {
        int vao = createVAO();
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 3, uvs);
        storeDataInAttributeList(2, 1, normals);
        bindIndicesBuffer(indices);
        unbindVAO();
        return new Mesh(vao, indices.length);
    }

    public Mesh loadToVAO(float[] positions, int[] indices, float[] uvs) {
        int vao = createVAO();
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 3, uvs);
        bindIndicesBuffer(indices);
        unbindVAO();
        return new Mesh(vao, indices.length);
    }

    public Mesh loadToVAO(float[] positions, int[] indices) {
        int vao = createVAO();
        storeDataInAttributeList(0, 3, positions);
        bindIndicesBuffer(indices);
        unbindVAO();
        return new Mesh(vao, indices.length);
    }

    public Mesh loadToVAO(float[] positions) {
        int vao = createVAO();
        storeDataInAttributeList(0, 3, positions);
        unbindVAO();
        return new Mesh(vao, positions.length / 3);
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

    public int loadCubeMap(String[] textureFiles) {
        int textureId = GL30.glGenTextures();
        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, textureId);

        for (int i = 0; i < textureFiles.length; i++) {
            ByteBuffer buf;
            int width;
            int height;

            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer channels = stack.mallocInt(1);

                buf = stbi_load("res/skybox/" + textureFiles[i] + ".png", w, h, channels, 4);
                if (buf == null) {
                    Nublada.LOG.severe("Image file [" + textureFiles[i]  + "] not loaded: " + stbi_failure_reason());
                    return -1;
                }

                width = w.get();
                height = h.get();
            }

            GL30.glTexImage2D(GL30.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL30.GL_RGBA, width, height, 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, buf);
        }

        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
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

    private void storeDataInAttributeList(int attributeNumber, int size, int[] data) {
        int vboId = GL30.glGenBuffers();
        vboList.add(vboId);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboId);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, data, GL30.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(attributeNumber, size, GL30.GL_INT, false, 0, 0);
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
