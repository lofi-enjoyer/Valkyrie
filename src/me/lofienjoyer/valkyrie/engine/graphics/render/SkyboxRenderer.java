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

    private static final float SIZE = 1f;

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

    private final Mesh mesh;
    private final int texture;
    private final Shader shader;
    private final Vector3f fogColor;

    public SkyboxRenderer() {
        this.mesh = new Mesh(VERTICES);
        this.texture = Valkyrie.LOADER.loadCubeMap(TEXTURE_FILES);
        this.shader = ResourceLoader.loadShader("Skybox Shader", "res/shaders/skybox/skybox_vert.glsl", "res/shaders/skybox/skybox_frag.glsl");
        this.fogColor = new Vector3f();
    }

    public void render(Camera camera) {
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

    public void setFogColor(float r, float g, float b) {
        fogColor.x = r;
        fogColor.y = g;
        fogColor.z = b;
    }

    public void setFogColor(Vector3f color) {
        setFogColor(color.x, color.y, color.z);
    }

    public void setupProjectionMatrix(int width, int height) {
        Matrix4f projectionMatrix = new Matrix4f();
        projectionMatrix.perspective(Valkyrie.FOV, width / (float)height, 0.01f, 5000f);
    }

}
