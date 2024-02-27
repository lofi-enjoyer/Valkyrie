package me.lofienjoyer.valkyrie.engine.graphics.render.gui;

import me.lofienjoyer.valkyrie.Valkyrie;
import me.lofienjoyer.valkyrie.engine.graphics.mesh.QuadMesh;
import me.lofienjoyer.valkyrie.engine.graphics.render.Renderer;
import me.lofienjoyer.valkyrie.engine.graphics.shaders.Shader;
import me.lofienjoyer.valkyrie.engine.resources.ResourceLoader;
import me.lofienjoyer.valkyrie.engine.utils.Maths;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL46.*;

public class CrosshairRenderer {

    private static int texture;
    private static QuadMesh mesh;
    private static Shader shader;

    private static boolean loaded;

    private CrosshairRenderer() {

    }

    public static void init() {
        if (loaded)
            return;

        texture = Valkyrie.LOADER.loadTexture("res/textures/gui/crosshair.png");
        mesh = new QuadMesh();
        shader = ResourceLoader.loadShader("crosshair-shader", "res/shaders/crosshair_vertex.glsl", "res/shaders/crosshair_fragment.glsl");

        var crosshairTransform = Maths.createTransformationMatrix(new Vector3f(0), 0);
        shader.bind();
        shader.loadMatrix("transformationMatrix", crosshairTransform);

        loaded = true;
    }

    public static void render() {
        shader.bind();
        Renderer.enableBlend();
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        Renderer.disableDepthTest();
        glBindVertexArray(mesh.getVaoId());
        glBindTexture(GL_TEXTURE_2D, texture);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        Renderer.disableBlend();
        Renderer.enableDepthTest();
    }

    public static void dispose() {
        if (!loaded)
            return;

        shader.dispose();

        loaded = false;
    }

    public static void setupProjectionMatrix(int width, int height) {
        var crosshairProjection = new Matrix4f();
        crosshairProjection.ortho(-width / 32f, width / 32f, -height / 32f, height / 32f, -50, 50);

        shader.bind();
        shader.loadMatrix("projectionMatrix", crosshairProjection);
    }

}
