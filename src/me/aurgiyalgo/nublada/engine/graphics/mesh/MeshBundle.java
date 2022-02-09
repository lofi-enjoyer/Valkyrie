package me.aurgiyalgo.nublada.engine.graphics.mesh;

import me.aurgiyalgo.nublada.engine.world.Chunk;

public class MeshBundle {

    private Mesh solidMesh;
    private Mesh transparentMesh;

    private GreedyMesher greedyMesher;
    private DynamicMesher dynamicMesher;

    public MeshBundle(Chunk chunk) {
        this.greedyMesher = new GreedyMesher(chunk);
        this.dynamicMesher = new DynamicMesher(chunk);
    }

    public MeshBundle loadMeshToGpu() {
        solidMesh = greedyMesher.loadMeshToGpu();
        transparentMesh = dynamicMesher.loadMeshToGpu();

        greedyMesher = null;
        dynamicMesher = null;
        return this;
    }

    public Mesh getSolidMesh() {
        return solidMesh;
    }

    public Mesh getTransparentMesh() {
        return transparentMesh;
    }
}
