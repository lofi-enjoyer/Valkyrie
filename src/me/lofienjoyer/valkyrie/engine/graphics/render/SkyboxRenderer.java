package me.lofienjoyer.valkyrie.engine.graphics.render;

import me.lofienjoyer.valkyrie.Valkyrie;
import me.lofienjoyer.valkyrie.engine.graphics.camera.Camera;
import me.lofienjoyer.valkyrie.engine.graphics.mesh.Mesh;
import me.lofienjoyer.valkyrie.engine.graphics.shaders.SkyboxShader;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL45.*;

public class SkyboxRenderer {

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

    private static String[] TEXTURE_FILES = { "right", "left", "top", "bottom", "back", "front" };

    private final Mesh mesh;
    private final int texture;
    private final SkyboxShader shader;
    private Matrix4f projectionMatrix;

    public SkyboxRenderer() {
        this.mesh = new Mesh(VERTICES);
        this.texture = Valkyrie.LOADER.loadCubeMap(TEXTURE_FILES);
        this.shader = new SkyboxShader();
        setupProjectionMatrix(640, 360);
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.loadFogColor(new Vector3f(0.45f, 0.71f, 1.00f));
    }

    public void render(Camera camera) {
        glDisable(GL_DEPTH_TEST);

        shader.start();
        shader.loadViewMatrix(camera);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, texture);

        glBindVertexArray(mesh.getVaoId());
        glEnableVertexAttribArray(0);
        glDrawArrays(GL_TRIANGLES, 0, mesh.getVertexCount());

        glBindVertexArray(0);

        glEnable(GL_DEPTH_TEST);
    }

    public void setupProjectionMatrix(int width, int height) {
        this.projectionMatrix = new Matrix4f();
        this.projectionMatrix.perspective(Valkyrie.FOV, width / (float)height, 0.01f, 5000f);
    }

}
