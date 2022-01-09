package me.aurgiyalgo.nublada.graphics.render;

import me.aurgiyalgo.nublada.world.Chunk;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static me.aurgiyalgo.nublada.world.World.CHUNK_WIDTH;
import static me.aurgiyalgo.nublada.world.World.CHUNK_HEIGHT;

public class FrustumCullingTester {

    private final Matrix4f prjViewMatrix;

    private FrustumIntersection frustumIntersection;

    public FrustumCullingTester() {
        this.prjViewMatrix = new Matrix4f();

        this.frustumIntersection = new FrustumIntersection();
    }

    public boolean isChunkInside(Chunk chunk, float y) {
        return frustumIntersection.testAab(
                new Vector3f(chunk.getPosition().x * CHUNK_WIDTH, 0, chunk.getPosition().y * CHUNK_WIDTH),
                new Vector3f(chunk.getPosition().x * CHUNK_WIDTH + CHUNK_WIDTH, CHUNK_HEIGHT, chunk.getPosition().y * CHUNK_WIDTH + CHUNK_WIDTH)
        );
    }

    public void updateFrustum(Matrix4f projMatrix, Matrix4f viewMatrix) {
        prjViewMatrix.set(projMatrix);
        prjViewMatrix.mul(viewMatrix);

        frustumIntersection.set(prjViewMatrix);
    }

}
