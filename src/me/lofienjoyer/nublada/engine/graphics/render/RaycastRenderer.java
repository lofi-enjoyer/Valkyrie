package me.lofienjoyer.nublada.engine.graphics.render;

import me.lofienjoyer.nublada.engine.graphics.camera.Camera;
import me.lofienjoyer.nublada.engine.graphics.mesh.Mesh;
import me.lofienjoyer.nublada.engine.graphics.shaders.SelectorShader;
import me.lofienjoyer.nublada.engine.utils.Maths;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;

public class RaycastRenderer {

    private final Mesh mesh;
    private final SelectorShader selectorShader;

    private Matrix4f projectionMatrix;

    public RaycastRenderer() {
        float[] positions = {
                0, 0, 0,
                0, 1, 0,
                0, 1, 1,
                0, 0, 1,
                1, 0, 0,
                1, 1, 0,
                1, 1, 1,
                1, 0, 1
        };

        int[] indices = {
                0, 1, 2,
                2, 3, 0,

                4, 5, 1,
                1, 0, 4,

                1, 5, 6,
                6, 2, 1,

                7, 6, 5,
                5, 4, 7,

                7, 4, 0,
                0, 3, 7,

                3, 2, 6,
                6, 7, 3
        };

        this.mesh = new Mesh(positions, indices);

        this.selectorShader = new SelectorShader();
    }

    public void render(Camera camera, Vector3f hitPosition) {
        // Highlights the voxel the player is looking at
        selectorShader.start();
        selectorShader.loadViewMatrix(camera);
        selectorShader.loadTime((float) GLFW.glfwGetTime());
        selectorShader.loadTransformationMatrix(Maths.createTransformationMatrix(hitPosition, 0));

        GL30.glLineWidth(4);
        GL30.glDisable(GL30.GL_DEPTH_TEST);
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glDisable(GL30.GL_CULL_FACE);

        GL30.glBindVertexArray(mesh.getVaoId());
        GL30.glEnableVertexAttribArray(0);

        GL30.glDrawElements(GL30.GL_TRIANGLES, mesh.getVertexCount(), GL30.GL_UNSIGNED_INT, 0);

        GL30.glBindVertexArray(0);

        GL30.glLineWidth(1);
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        GL30.glDisable(GL30.GL_BLEND);
        GL30.glEnable(GL30.GL_CULL_FACE);
    }

    /**
     * Updates the projection matrix based on the screen resolution
     * @param width Screen width
     * @param height Screen height
     */
    public void setupProjectionMatrix(int width, int height) {
        this.projectionMatrix = new Matrix4f();
        this.projectionMatrix.perspective(70, width / (float)height, 0.01f, 5000f);

        selectorShader.start();
        selectorShader.loadProjectionMatrix(projectionMatrix);
    }

}
