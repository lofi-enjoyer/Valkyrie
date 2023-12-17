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

    private final Mesh[] solidMesh;
    private final Mesh[] transparentMesh;

    private final Mesher[] greedyMesher;
    private final Mesher[] dynamicMesher;

    private boolean loaded = false;

    public MeshBundle(Chunk chunk) {
        this.chunk = chunk;

        this.solidMesh = new Mesh[CHUNK_HEIGHT / CHUNK_SECTION_HEIGHT];
        for (int i = 0; i < solidMesh.length; i++) {
            solidMesh[i] = Valkyrie.LOADER.allocateMesh();
        }

        this.transparentMesh = new Mesh[CHUNK_HEIGHT / CHUNK_SECTION_HEIGHT];
        for (int i = 0; i < transparentMesh.length; i++) {
            transparentMesh[i] = Valkyrie.LOADER.allocateMesh();
        }

        this.greedyMesher = new Mesher[CHUNK_HEIGHT / CHUNK_SECTION_HEIGHT];
        this.dynamicMesher = new Mesher[CHUNK_HEIGHT / CHUNK_SECTION_HEIGHT];
    }

    public void compute(int section) {
        ChunkPreMeshData chunkPreMeshData = new ChunkPreMeshData(chunk);

        greedyMesher[section] = new GreedyMesher(chunkPreMeshData, section).compute();
        dynamicMesher[section] = new DynamicMesher(chunkPreMeshData, section).compute();
    }

    public boolean loadMeshToGpu(int section) {
        if (greedyMesher[section] == null || dynamicMesher[section] == null)
            return false;

        greedyMesher[section].loadToGpu(solidMesh[section]);
        dynamicMesher[section].loadToGpu(transparentMesh[section]);

        greedyMesher[section] = null;
        dynamicMesher[section] = null;
        this.loaded = true;

        if (solidMesh[section].getVertexCount() == 0 && transparentMesh[section].getVertexCount() == 0)
            return false;

        return true;
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
