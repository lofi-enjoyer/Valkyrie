package me.lofienjoyer.valkyrie.engine.graphics.mesh;

import me.lofienjoyer.valkyrie.engine.world.Block;
import me.lofienjoyer.valkyrie.engine.world.BlockRegistry;
import me.lofienjoyer.valkyrie.engine.world.ChunkPreMeshData;

import java.util.ArrayList;
import java.util.List;

import static me.lofienjoyer.valkyrie.engine.world.World.*;

/**
 * Generates the transparent mesh of a chunk
 */
public class DynamicMesher implements Mesher {

    private ChunkPreMeshData chunkData;

    private List<Object> positions;
    private List<Integer> indices;

    private float[] positionsArray;
    private int[] indicesArray;

    public DynamicMesher(ChunkPreMeshData chunkPreMeshData) {
        this.chunkData = chunkPreMeshData;
    }

    @Override
    public Mesher compute() {
        this.positions = new ArrayList<>(10000);
        this.indices = new ArrayList<>(6000);

        computeMesh();

        // IMPORTANT - DO NOT DELETE
        // De-references ChunkPreMeshData to avoid memory leaks
        this.chunkData = null;

        try {
            positionsArray = new float[positions.size()];
            for (int i = 0; i < positions.size(); i++) {
                positionsArray[i] = (float)positions.get(i);
            }
            positions = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        indicesArray = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }
        indices = null;

        return this;
    }

    @Override
    public Mesh loadToGpu(Mesh mesh) {
        mesh.updateMesh(positionsArray, indicesArray);
        positionsArray = null;
        indicesArray = null;
        return mesh;
    }

    private void computeMesh() {
        int currentVoxel;
        for (int x = 0; x < CHUNK_WIDTH; x++) {
            for (int y = 0; y < CHUNK_HEIGHT; y++) {
                for (int z = 0; z < CHUNK_WIDTH; z++) {
                    currentVoxel = chunkData.getBlock(x, y, z);
                    if (currentVoxel == 0 || !BlockRegistry.getBlock(currentVoxel).isCustomModel()) continue;

                    meshCustomModel(x, y, z, BlockRegistry.getBlock(currentVoxel));
                }
            }
        }
    }

    int passes = 0;

    private void meshCustomModel(int x, int y, int z, Block block) {
        var blockPositions = block.getMesh().getPositions();
        for (int i = 0; i < blockPositions.size() / 4; i++) {
            positions.add(x + blockPositions.get(i * 4));
            positions.add(y + blockPositions.get(i * 4 + 1));
            positions.add(z + blockPositions.get(i * 4 + 2));
            positions.add(blockPositions.get(i * 4 + 3));
        }

        var blockIndices = block.getMesh().getIndices();
        for (Integer blockIndex : blockIndices) {
            indices.add(blockIndex + passes);
        }

        passes += blockPositions.size() / 4;
    }

    private int compressData(int xUv, int yUv, int texture, int normal, int cull, int wave) {
        return xUv | yUv << 1 | normal << 2 | cull << 5 | wave << 6 | texture << 7;
    }

}
