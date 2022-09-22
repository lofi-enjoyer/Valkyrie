package me.lofienjoyer.nublada.engine.graphics.mesh;

import me.lofienjoyer.nublada.engine.world.Chunk;

/**
 * Contains the solid and transparent meshes of a chunk
 */
public class MeshBundle {

    private Mesh solidMesh;
    private Mesh transparentMesh;

    private Mesher greedyMesher;
    private Mesher dynamicMesher;

    public MeshBundle(Chunk chunk) {
        this.greedyMesher = new GreedyMesher(chunk).compute();

        this.dynamicMesher = new DynamicMesher(chunk).compute();
    }

    public MeshBundle loadMeshToGpu() {
        solidMesh = greedyMesher.loadToGpu();
        transparentMesh = dynamicMesher.loadToGpu();

        greedyMesher = null;
        dynamicMesher = null;
        return this;
    }

    public Mesh getSolidMeshes() {
        return solidMesh;
    }

    public Mesh getTransparentMeshes() {
        return transparentMesh;
    }
}
