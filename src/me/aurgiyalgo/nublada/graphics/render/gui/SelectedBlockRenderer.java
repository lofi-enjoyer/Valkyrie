package me.aurgiyalgo.nublada.graphics.render.gui;

import me.aurgiyalgo.nublada.Nublada;
import me.aurgiyalgo.nublada.graphics.mesh.Mesh;
import me.aurgiyalgo.nublada.graphics.shaders.gui.SelectedBlockShader;
import me.aurgiyalgo.nublada.utils.Maths;
import me.aurgiyalgo.nublada.world.Block;
import me.aurgiyalgo.nublada.world.BlockRegistry;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;

public class SelectedBlockRenderer {

    private final Mesh mesh;
    private Matrix4f projectionMatrix;
    private Matrix4f transformationMatrix;
    private SelectedBlockShader shader;

    public SelectedBlockRenderer() {
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

        this.mesh = Nublada.LOADER.loadToVAO(positions, indices);
        this.shader = new SelectedBlockShader();
        this.transformationMatrix = Maths.createTransformationMatrix(new Vector2f(1, 0.5f), new Vector3f(30, 45, 0));

        setupProjectionMatrix(16, 9);

        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.loadTransformationMatrix(transformationMatrix);
    }

    public void render(int id) {
        GL30.glEnable(GL30.GL_BLEND);

        Block block = BlockRegistry.getBLock(id);

        GL30.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, BlockRegistry.TEXTURE_ARRAY_ID);

        shader.start();

        GL30.glBindVertexArray(block.getMesh().getVao());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);

        GL30.glDrawElements(GL30.GL_TRIANGLES, mesh.getVertexCount(), GL30.GL_UNSIGNED_INT, 0);

        GL30.glBindVertexArray(0);

        GL30.glDisable(GL30.GL_BLEND);
    }

    private void setupProjectionMatrix(int width, int height) {
        this.projectionMatrix = new Matrix4f();
        this.projectionMatrix.ortho(0, width, 0, height, -50, 50);
    }

}
