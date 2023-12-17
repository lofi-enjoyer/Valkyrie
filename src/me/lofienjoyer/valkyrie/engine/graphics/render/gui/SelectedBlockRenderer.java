package me.lofienjoyer.valkyrie.engine.graphics.render.gui;

import me.lofienjoyer.valkyrie.engine.graphics.mesh.Mesh;
import me.lofienjoyer.valkyrie.engine.graphics.shaders.gui.SelectedBlockShader;
import me.lofienjoyer.valkyrie.engine.utils.Maths;
import me.lofienjoyer.valkyrie.engine.world.Block;
import me.lofienjoyer.valkyrie.engine.world.BlockRegistry;
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

        this.mesh = new Mesh(positions, indices);
        this.shader = new SelectedBlockShader();
        this.transformationMatrix = Maths.createTransformationMatrix(new Vector2f(1, 0.5f), new Vector3f(30, 45, 0));
    }

    public void render(int id) {
        Block block = BlockRegistry.getBlock(id);
        if (block == null) return;

        GL30.glEnable(GL30.GL_BLEND);

        GL30.glBindTexture(GL30.GL_TEXTURE_2D, BlockRegistry.TILESET_ID);

        shader.start();

        GL30.glBindVertexArray(block.getMesh().getVaoId());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);

        GL30.glDrawElements(GL30.GL_TRIANGLES, mesh.getVertexCount(), GL30.GL_UNSIGNED_INT, 0);

        GL30.glBindVertexArray(0);

        GL30.glDisable(GL30.GL_BLEND);
    }

    public void setupProjectionMatrix(int width, int height) {
        float relation = width / (float)height;

        this.projectionMatrix = new Matrix4f();
        this.projectionMatrix.ortho(0, 9 * relation, 0, 9, -50, 50);

        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.loadTransformationMatrix(transformationMatrix);
    }

}
