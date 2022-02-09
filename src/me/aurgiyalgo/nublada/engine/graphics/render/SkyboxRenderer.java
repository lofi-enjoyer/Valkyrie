package me.aurgiyalgo.nublada.engine.graphics.render;

import me.aurgiyalgo.nublada.Nublada;
import me.aurgiyalgo.nublada.engine.graphics.camera.Camera;
import me.aurgiyalgo.nublada.engine.graphics.mesh.Mesh;
import me.aurgiyalgo.nublada.engine.graphics.shaders.SkyboxShader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

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

    private Mesh mesh;
    private int texture;
    private SkyboxShader shader;
    private Matrix4f projectionMatrix;

    public SkyboxRenderer() {
        this.mesh = new Mesh(VERTICES);
        this.texture = Nublada.LOADER.loadCubeMap(TEXTURE_FILES);
        this.shader = new SkyboxShader();
        setupProjectionMatrix(640, 360);
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.loadFogColor(new Vector3f(0.45f, 0.71f, 1.00f));
    }

    public void render(Camera camera) {
        GL30.glDisable(GL30.GL_DEPTH_TEST);

        shader.start();
        shader.loadViewMatrix(camera);

        GL30.glActiveTexture(GL13.GL_TEXTURE0);
        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, texture);

        GL30.glBindVertexArray(mesh.getVaoId());
        GL30.glEnableVertexAttribArray(0);
        GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, mesh.getVertexCount());

        GL30.glBindVertexArray(0);

        GL30.glEnable(GL30.GL_DEPTH_TEST);
    }

    public void setupProjectionMatrix(int width, int height) {
        this.projectionMatrix = new Matrix4f();
        this.projectionMatrix.perspective(70, width / (float)height, 0.01f, 5000f);
    }

}
