package me.lofienjoyer.valkyrie.engine.graphics.render;

import me.lofienjoyer.valkyrie.Valkyrie;
import me.lofienjoyer.valkyrie.engine.graphics.camera.Camera;
import me.lofienjoyer.valkyrie.engine.graphics.mesh.Mesh;
import me.lofienjoyer.valkyrie.engine.graphics.shaders.Shader;
import me.lofienjoyer.valkyrie.engine.resources.ResourceLoader;
import me.lofienjoyer.valkyrie.engine.utils.Maths;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL45.*;

public class RaycastRenderer {

    private final Mesh mesh;
    private final Shader selectorShader;

    int[] indirectData = {
            36, 1, 0, 0, 0
    };

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

        int iboId = glGenBuffers();
        glBindBuffer(GL_DRAW_INDIRECT_BUFFER, iboId);

        IntBuffer ibuf = BufferUtils.createIntBuffer(indirectData.length);
        ibuf.put(indirectData).flip();
        glBufferData(GL_DRAW_INDIRECT_BUFFER, ibuf, GL_STATIC_DRAW);

        this.mesh = new Mesh(positions, indices);

        this.selectorShader = ResourceLoader.loadShader("Raycast Shader", "res/shaders/world/raycast_vert.glsl", "res/shaders/world/raycast_frag.glsl");
    }

    public void render(Camera camera, Vector3f hitPosition) {
        selectorShader.bind();
        selectorShader.loadMatrix("viewMatrix", Maths.createViewMatrix(camera));
        selectorShader.loadFloat("time", (float) GLFW.glfwGetTime());
        selectorShader.loadMatrix("transformationMatrix", Maths.createTransformationMatrix(hitPosition, 0));

        glLineWidth(4);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDisable(GL_CULL_FACE);

        glBindVertexArray(mesh.getVaoId());
        glEnableVertexAttribArray(0);

        glFinish();
        glMultiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, GL_ZERO, indirectData.length / 5, GL_ZERO);

        glBindVertexArray(0);

        glLineWidth(1);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        glEnable(GL_CULL_FACE);
    }

    /**
     * Updates the projection matrix based on the screen resolution
     * @param width Screen width
     * @param height Screen height
     */
    public void setupProjectionMatrix(int width, int height) {
        Matrix4f projectionMatrix = new Matrix4f();
        projectionMatrix.perspective(Valkyrie.FOV, width / (float)height, 0.01f, 5000f);

        selectorShader.bind();
        selectorShader.loadMatrix("projectionMatrix", projectionMatrix);
    }

}
