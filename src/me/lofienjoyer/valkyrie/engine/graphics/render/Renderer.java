package me.lofienjoyer.valkyrie.engine.graphics.render;

import static org.lwjgl.opengl.GL46.*;

public class Renderer {

    public static void enableDepthTest() {
        glEnable(GL_DEPTH_TEST);
    }

    public static void disableDepthTest() {
        glDisable(GL_DEPTH_TEST);
    }

    public static void enableCullFace() {
        glEnable(GL_CULL_FACE);
    }

    public static void disableCullFace() {
        glDisable(GL_CULL_FACE);
    }

    public static void setCullFace(boolean isFront) {
        glCullFace(isFront ? GL_FRONT : GL_BACK);
    }

    public static void enableBlend() {
        glEnable(GL_BLEND);
    }

    public static void disableBlend() {
        glDisable(GL_BLEND);
    }

    public static void bindTexture2D(int textureId) {
        glBindTexture(GL_TEXTURE_2D, textureId);
    }

}
