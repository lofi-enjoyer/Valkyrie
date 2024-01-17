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

    private final int section;

    public DynamicMesher(ChunkPreMeshData chunkPreMeshData, int section) {
        this.chunkData = chunkPreMeshData;
        this.section = section;
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
            for (int y = 0; y < CHUNK_SECTION_HEIGHT; y++) {
                for (int z = 0; z < CHUNK_WIDTH; z++) {
                    currentVoxel = chunkData.getBlock(x, y + (CHUNK_SECTION_HEIGHT * section), z);
                    if (currentVoxel == 0 || !BlockRegistry.getBlock(currentVoxel).isCustomModel()) continue;

                    meshCustomModel(x, y, z, BlockRegistry.getBlock(currentVoxel));
                }
            }
        }
    }

    int passes = 0;
    private static final float diagonal = (float) (Math.sqrt(0.5) / 2);
    private static final float diagonalM = 0.5f + diagonal;
    private static final float diagonalP = 0.5f - diagonal;

    private void meshCustomModel(int x, int y, int z, Block block) {
        var blockPositions = block.getMesh().getPositions();
        var blockUvs = block.getMesh().getUvs();
        for (int i = 0; i < blockPositions.size() / 3; i++) {
            positions.add(x + blockPositions.get(i * 3));
            positions.add(y + blockPositions.get(i * 3 + 1));
            positions.add(z + blockPositions.get(i * 3 + 2));
            positions.add((float) compressData((int)(float)blockUvs.get(i * 3), (int)(float)blockUvs.get(i * 3 + 1), (int)(float)blockUvs.get(i * 3 + 2), 2, 1, 0));
        }

        var blockIndices = block.getMesh().getIndices();
        for (int i = 0; i < blockIndices.size(); i++) {
            indices.add(blockIndices.get(i) + passes);
        }

        passes += blockPositions.size() / 3;
    }

    private int compressData(int xUv, int yUv, int texture, int normal, int cull, int wave) {
        return xUv | yUv << 1 | normal << 2 | cull << 5 | wave << 6 | texture << 7;
    }

}
