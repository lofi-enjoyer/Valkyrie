package me.lofienjoyer.valkyrie.engine.graphics.render;

import me.lofienjoyer.valkyrie.Valkyrie;
import me.lofienjoyer.valkyrie.engine.graphics.camera.Camera;
import me.lofienjoyer.valkyrie.engine.graphics.mesh.Mesh;
import me.lofienjoyer.valkyrie.engine.graphics.shaders.Shader;
import me.lofienjoyer.valkyrie.engine.resources.ResourceLoader;
import me.lofienjoyer.valkyrie.engine.utils.Maths;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL45.*;

public class SkyboxRenderer {

    private static SkyboxRenderer instance;

    private static final float SIZE = 10f;

    private static final float[] VERTICES = {
            -SIZE,  SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
             SIZE, -SIZE, -SIZE,
             SIZE, -SIZE, -SIZE,
             SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

             SIZE, -SIZE, -SIZE,
             SIZE, -SIZE,  SIZE,
             SIZE,  SIZE,  SIZE,
             SIZE,  SIZE,  SIZE,
             SIZE,  SIZE, -SIZE,
             SIZE, -SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
             SIZE,  SIZE,  SIZE,
             SIZE,  SIZE,  SIZE,
             SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            -SIZE,  SIZE, -SIZE,
             SIZE,  SIZE, -SIZE,
             SIZE,  SIZE,  SIZE,
             SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
             SIZE, -SIZE, -SIZE,
             SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
             SIZE, -SIZE,  SIZE
    };

    private static final String[] TEXTURE_FILES = { "right", "left", "top", "bottom", "back", "front" };

    private static Mesh mesh;
    private static int texture;
    private static Shader shader;
    private static Vector3f fogColor;

    private static boolean loaded;

    private SkyboxRenderer() {

    }

    public static void init() {
        if (loaded)
            return;

        // TODO: 9/1/24 Dispose mesh and texture
        mesh = new Mesh(VERTICES);
        texture = Valkyrie.LOADER.loadCubeMap(TEXTURE_FILES);
        shader = ResourceLoader.loadShader("Skybox Shader", "res/shaders/skybox/skybox_vert.glsl", "res/shaders/skybox/skybox_frag.glsl");
        fogColor = new Vector3f();

        loaded = true;
    }

    public static void render(Camera camera) {
        Renderer.disableDepthTest();

        shader.bind();
        shader.loadMatrix("viewMatrix", Maths.createViewMatrixWithNoRotation(camera));
        shader.loadVector("fogColor", fogColor);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, texture);

        glBindVertexArray(mesh.getVaoId());
        glEnableVertexAttribArray(0);
        glDrawArrays(GL_TRIANGLES, 0, mesh.getVertexCount());

        glBindVertexArray(0);

        Renderer.enableDepthTest();
    }

    public static void dispose() {
        if (!loaded)
            return;

        shader.dispose();
        fogColor = null;

        loaded = false;
    }

    public static void setFogColor(float r, float g, float b) {
        fogColor.x = r;
        fogColor.y = g;
        fogColor.z = b;
    }

    public static void setFogColor(Vector3f color) {
        setFogColor(color.x, color.y, color.z);
    }

    public static void setupProjectionMatrix(int width, int height) {
        Matrix4f projectionMatrix = new Matrix4f();
        projectionMatrix.perspective(Valkyrie.FOV, width / (float)height, 0.01f, 5000f);

        shader.bind();
        shader.loadMatrix("projectionMatrix", projectionMatrix);
    }
}
