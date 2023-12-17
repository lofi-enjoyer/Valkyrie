package me.lofienjoyer.valkyrie.engine.graphics.framebuffer;

import me.lofienjoyer.valkyrie.Valkyrie;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL45.*;

public class ColorFramebuffer implements Framebuffer {

    private final int id, colorTextureId, rboId;

    private int width, height;

    public ColorFramebuffer(int width, int height) {
        this.id = glGenFramebuffers();
        bind();

        this.colorTextureId = glGenTextures();
        this.rboId = glGenRenderbuffers();

        resize(width, height);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            Valkyrie.LOG.severe("Error while creating framebuffer!");
        }

        unbind();
    }

    @Override
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;

        glBindTexture(GL_TEXTURE_2D, colorTextureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorTextureId, 0);
        glBindTexture(GL_TEXTURE_2D, 0);

        glBindRenderbuffer(GL_RENDERBUFFER, rboId);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rboId);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
    }

    @Override
    public void bind() {
        glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
    }

    @Override
    public void unbind() {
        glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getColorTextureId() {
        return colorTextureId;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

}
