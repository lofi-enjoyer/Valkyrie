package me.lofienjoyer.nublada.engine.graphics.mesh;

import me.lofienjoyer.nublada.Nublada;
import me.lofienjoyer.nublada.engine.world.Chunk;
import me.lofienjoyer.nublada.engine.world.ChunkPreMeshData;

/**
 * Contains the solid and transparent meshes of a chunk
 */
public class MeshBundle {

    private final Chunk chunk;

    private Mesh solidMesh;
    private Mesh transparentMesh;

    private Mesher greedyMesher;
    private Mesher dynamicMesher;

    private boolean loaded = false;

    public MeshBundle(Chunk chunk) {
        this.chunk = chunk;

        this.solidMesh = Nublada.LOADER.allocateMesh();
        this.transparentMesh = Nublada.LOADER.allocateMesh();
    }

    public void compute() {
        ChunkPreMeshData chunkPreMeshData = new ChunkPreMeshData(chunk);

        this.greedyMesher = new GreedyMesher(chunkPreMeshData).compute();
        this.dynamicMesher = new DynamicMesher(chunkPreMeshData).compute();
    }

    public MeshBundle loadMeshToGpu() {
        if (greedyMesher == null || dynamicMesher == null)
            return null;

        greedyMesher.loadToGpu(solidMesh);
        dynamicMesher.loadToGpu(transparentMesh);

        greedyMesher = null;
        dynamicMesher = null;
        this.loaded = true;
        return this;
    }

    public Mesh getSolidMeshes() {
        return solidMesh;
    }

    public Mesh getTransparentMeshes() {
        return transparentMesh;
    }

    public boolean isLoaded() {
        return loaded;
    }

}
