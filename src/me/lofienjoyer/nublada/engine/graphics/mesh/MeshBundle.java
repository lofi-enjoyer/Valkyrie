package me.lofienjoyer.nublada.engine.graphics.mesh;

import me.lofienjoyer.nublada.Nublada;
import me.lofienjoyer.nublada.engine.world.Chunk;
import me.lofienjoyer.nublada.engine.world.ChunkPreMeshData;

import static me.lofienjoyer.nublada.engine.world.World.*;

/**
 * Contains the solid and transparent meshes of a chunk
 */
public class MeshBundle {

    private final Chunk chunk;

    private final Mesh[] solidMesh;
    private final Mesh[] transparentMesh;

    private final Mesher[] greedyMesher;
    private final Mesher[] dynamicMesher;

    private boolean loaded = false;

    public MeshBundle(Chunk chunk) {
        this.chunk = chunk;

        this.solidMesh = new Mesh[CHUNK_HEIGHT / CHUNK_SECTION_HEIGHT];
        for (int i = 0; i < solidMesh.length; i++) {
            solidMesh[i] = Nublada.LOADER.allocateMesh();
        }

        this.transparentMesh = new Mesh[CHUNK_HEIGHT / CHUNK_SECTION_HEIGHT];
        for (int i = 0; i < transparentMesh.length; i++) {
            transparentMesh[i] = Nublada.LOADER.allocateMesh();
        }

        this.greedyMesher = new Mesher[CHUNK_HEIGHT / CHUNK_SECTION_HEIGHT];
        this.dynamicMesher = new Mesher[CHUNK_HEIGHT / CHUNK_SECTION_HEIGHT];
    }

    public void compute(int section) {
        ChunkPreMeshData chunkPreMeshData = new ChunkPreMeshData(chunk);

        greedyMesher[section] = new GreedyMesher(chunkPreMeshData, section).compute();
        dynamicMesher[section] = new DynamicMesher(chunkPreMeshData, section).compute();
    }

    public MeshBundle loadMeshToGpu(int section) {
        if (greedyMesher[section] == null || dynamicMesher[section] == null)
            return null;

        greedyMesher[section].loadToGpu(solidMesh[section]);
        dynamicMesher[section].loadToGpu(transparentMesh[section]);

        greedyMesher[section] = null;
        dynamicMesher[section] = null;
        this.loaded = true;
        return this;
    }

    public Mesh getSolidMeshes(int section) {
        return solidMesh[section];
    }

    public Mesh getTransparentMeshes(int section) {
        return transparentMesh[section];
    }

    public boolean isLoaded() {
        return loaded;
    }

}
