package me.lofienjoyer.nublada.engine.graphics.render;

import me.lofienjoyer.nublada.engine.world.Chunk;
import me.lofienjoyer.nublada.engine.world.World;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class FrustumCullingTester {

    private final Matrix4f prjViewMatrix;

    private final FrustumIntersection frustumIntersection;

    public FrustumCullingTester() {
        this.prjViewMatrix = new Matrix4f();

        this.frustumIntersection = new FrustumIntersection();
    }

    public boolean isChunkInside(Chunk chunk, float y) {
        return frustumIntersection.testAab(
                new Vector3f(chunk.getPosition().x * World.CHUNK_WIDTH, 0, chunk.getPosition().y * World.CHUNK_WIDTH),
                new Vector3f(chunk.getPosition().x * World.CHUNK_WIDTH + World.CHUNK_WIDTH, World.CHUNK_HEIGHT, chunk.getPosition().y * World.CHUNK_WIDTH + World.CHUNK_WIDTH)
        );
    }

    public void updateFrustum(Matrix4f projMatrix, Matrix4f viewMatrix) {
        prjViewMatrix.set(projMatrix);
        prjViewMatrix.mul(viewMatrix);

        frustumIntersection.set(prjViewMatrix);
    }

}
