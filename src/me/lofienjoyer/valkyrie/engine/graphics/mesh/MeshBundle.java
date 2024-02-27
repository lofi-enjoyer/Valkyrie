package me.lofienjoyer.valkyrie.engine.graphics.mesh;

import me.lofienjoyer.valkyrie.Valkyrie;
import me.lofienjoyer.valkyrie.engine.world.Chunk;
import me.lofienjoyer.valkyrie.engine.world.ChunkPreMeshData;

import static me.lofienjoyer.valkyrie.engine.world.World.*;

/**
 * Contains the solid and transparent meshes of a chunk
 */
public class MeshBundle {

    private final Chunk chunk;

    private final Mesh solidMesh;
    private final Mesh transparentMesh;

    private Mesher greedyMesher;
    private Mesher dynamicMesher;

    private boolean loaded = false;

    public MeshBundle(Chunk chunk) {
        this.chunk = chunk;

        this.solidMesh = Valkyrie.LOADER.allocateMesh();

        this.transparentMesh = Valkyrie.LOADER.allocateMesh();
    }

    public void compute() {
        ChunkPreMeshData chunkPreMeshData = new ChunkPreMeshData(chunk);

        greedyMesher = new GreedyMesher(chunkPreMeshData).compute();
        dynamicMesher = new DynamicMesher(chunkPreMeshData).compute();
    }

    public boolean loadMeshToGpu() {
        if (greedyMesher == null || dynamicMesher == null)
            return false;

        greedyMesher.loadToGpu(solidMesh);
        dynamicMesher.loadToGpu(transparentMesh);

        greedyMesher = null;
        dynamicMesher = null;
        setLoaded(true);

        if (solidMesh.getVertexCount() == 0 && transparentMesh.getVertexCount() == 0)
            return false;

        return true;
    }

    public Mesh getSolidMeshes() {
        return solidMesh;
    }

    public Mesh getTransparentMeshes() {
        return transparentMesh;
    }

    public synchronized boolean isLoaded() {
        return loaded;
    }

    private synchronized void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

}
