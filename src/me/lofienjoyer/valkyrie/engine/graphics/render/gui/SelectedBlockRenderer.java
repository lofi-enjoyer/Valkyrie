package me.lofienjoyer.valkyrie.engine.graphics.render.gui;

import me.lofienjoyer.valkyrie.engine.graphics.mesh.Mesh;
import me.lofienjoyer.valkyrie.engine.graphics.render.Renderer;
import me.lofienjoyer.valkyrie.engine.graphics.shaders.Shader;
import me.lofienjoyer.valkyrie.engine.resources.ResourceLoader;
import me.lofienjoyer.valkyrie.engine.utils.Maths;
import me.lofienjoyer.valkyrie.engine.world.Block;
import me.lofienjoyer.valkyrie.engine.world.BlockRegistry;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;

public class SelectedBlockRenderer {

    // TODO: 9/1/24 Change this to use a QuadMesh
    private static final float[] positions = {
            0, 0, 0,
            0, 1, 0,
            0, 1, 1,
            0, 0, 1,
            1, 0, 0,
            1, 1, 0,
            1, 1, 1,
            1, 0, 1
    };

    private static final int[] indices = {
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

    private static Mesh mesh;
    private static Matrix4f transformationMatrix;
    private static Shader shader;

    private static boolean loaded;

    private SelectedBlockRenderer() {

    }

    public static void init() {
        if (loaded)
            return;

        mesh = new Mesh(positions, indices);
        shader = ResourceLoader.loadShader("Selected Block Shader",
           "res/shaders/gui/selected_block_vert.glsl",
           "res/shaders/gui/selected_block_frag.glsl");
        transformationMatrix = Maths.createTransformationMatrix(new Vector2f(1, 0.5f), new Vector3f(30, 30, 0));

        loaded = true;
    }

    public static void render(int id) {
        Block block = BlockRegistry.getBlock(id);
        if (block == null)
            return;

        Renderer.enableBlend();
        Renderer.enableDepthTest();

        Renderer.bindTexture2D(BlockRegistry.TILESET_TEXTURE_ID);

        shader.bind();

        GL30.glBindVertexArray(block.getMesh().getVaoId());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);

        GL30.glDrawElements(GL30.GL_TRIANGLES, mesh.getVertexCount(), GL30.GL_UNSIGNED_INT, 0);

        GL30.glBindVertexArray(0);

        Renderer.disableBlend();
    }

    public static void dispose() {
        if (!loaded)
            return;

        shader.dispose();
        transformationMatrix = null;

        loaded = false;
    }

    public static void setupProjectionMatrix(int width, int height) {
        float relation = width / (float)height;

        Matrix4f projectionMatrix = new Matrix4f();
        projectionMatrix.ortho(0, 9 * relation, 0, 9, -50, 50);

        shader.bind();
        shader.loadMatrix("projectionMatrix", projectionMatrix);
        shader.loadMatrix("transformationMatrix", transformationMatrix);
    }

}
