package me.aurgiyalgo.nublada.engine.graphics.mesh;

import me.aurgiyalgo.nublada.engine.world.Chunk;

/**
 * Contains the solid and transparent meshes of a chunk
 */
public class MeshBundle {

    private Mesh solidMesh;
    private Mesh[] transparentMesh;

    private GreedyMesher greedyMesher;
    private DynamicMesher[] dynamicMesher;

    public MeshBundle(Chunk chunk) {
        this.greedyMesher = new GreedyMesher(chunk);

        this.dynamicMesher = new DynamicMesher[2];
        this.dynamicMesher[0] = new DynamicMesher(chunk, 1);
        this.dynamicMesher[1] = new DynamicMesher(chunk, 2);
    }

    public MeshBundle loadMeshToGpu() {
        solidMesh = greedyMesher.loadMeshToGpu();

        transparentMesh = new Mesh[2];
        transparentMesh[0] = dynamicMesher[0].loadMeshToGpu();
        transparentMesh[1] = dynamicMesher[1].loadMeshToGpu();

        greedyMesher = null;
        dynamicMesher = null;
        return this;
    }

    public Mesh getSolidMesh() {
        return solidMesh;
    }

    public Mesh[] getTransparentMeshes() {
        return transparentMesh;
    }
}
