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
    }

    public void render() {
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

}
