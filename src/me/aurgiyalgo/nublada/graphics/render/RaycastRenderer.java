package me.aurgiyalgo.nublada.graphics.render;

import me.aurgiyalgo.nublada.Nublada;
import me.aurgiyalgo.nublada.graphics.mesh.Mesh;
import org.lwjgl.opengl.GL30;

public class RaycastRenderer {

    private final Mesh mesh;

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
                0, 1,
                1, 2,
                2, 3,
                3, 0,

                0, 4,
                4, 5,
                5, 1,
                1, 0,

                1, 5,
                5, 6,
                6, 2,
                2, 1,

                0, 3,
                3, 7,
                7, 4,
                4, 0,

                6, 7,
                7, 3,
                3, 2,
                2, 6,

                5, 4,
                4, 7,
                7, 6,
                6, 5
        };

        this.mesh = Nublada.LOADER.loadToVAO(positions, indices);
    }

    public void render() {
        GL30.glLineWidth(4);

        GL30.glBindVertexArray(mesh.getVao());
        GL30.glEnableVertexAttribArray(0);

        GL30.glDrawElements(GL30.GL_LINES, mesh.getVertexCount(), GL30.GL_UNSIGNED_INT, 0);

        GL30.glBindVertexArray(0);

        GL30.glLineWidth(1);
    }

}
