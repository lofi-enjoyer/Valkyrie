package me.lofienjoyer.nublada.engine.graphics.texture;

import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class TextureArray {

    private final int id;

    public TextureArray(String... fileName) throws Exception {
        this(loadTexture(fileName));
    }

    public TextureArray(int id) {
        this.id = id;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public int getId() {
        return id;
    }

    private static int loadTexture(String... fileName) throws Exception {
        int id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D_ARRAY, id);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
        glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_RGBA, 8, 8, fileName.length, 0, GL_RGBA,
                GL_UNSIGNED_BYTE, (ByteBuffer) null);

        for (int i = 0; i < fileName.length; i++) {
            ByteBuffer buf;

            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer channels = stack.mallocInt(1);

                buf = stbi_load(fileName[i], w, h, channels, 4);
                if (buf == null) {
                    throw new Exception("Image file [" + fileName[i]  + "] not loaded: " + stbi_failure_reason());
                }

                GL30.glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, i, 8, 8, 1, GL_RGBA, GL_UNSIGNED_BYTE, buf);

                stbi_image_free(buf);
            }
        }

        glGenerateMipmap(GL_TEXTURE_2D_ARRAY);
        glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
        return id;
    }

    public void cleanup() {
        glDeleteTextures(id);
    }
}

