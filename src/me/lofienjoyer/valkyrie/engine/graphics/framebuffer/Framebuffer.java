package me.lofienjoyer.valkyrie.engine.graphics.framebuffer;

public interface Framebuffer {

    void bind();

    void unbind();

    void resize(int width, int height);

    int getId();

    int getColorTextureId();

    int getWidth();

    int getHeight();

}
