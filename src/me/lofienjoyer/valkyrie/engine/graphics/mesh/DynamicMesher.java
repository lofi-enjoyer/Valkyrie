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
                    if (currentVoxel == 0) continue;

                    meshBlock(x, y, z, BlockRegistry.getBlock(currentVoxel));
                }
            }
        }
    }

    int passes = 0;

    private void meshBlock(int x, int y, int z, Block block) {
        int westBlock = chunkData.getBlock(x + 1, y + (CHUNK_SECTION_HEIGHT * section), z);
        if (westBlock == 0 || block.shouldDrawBetween() || (block.getId() != westBlock && BlockRegistry.getBlock(westBlock).isTransparent())) {
            positions.addAll(List.of(x + 1f, y + 0f, z + 0f, (float)compressData(1, 1, block.getWestTexture(), 4)));
            positions.addAll(List.of(x + 1f, y + 1f, z + 0f, (float)compressData(1, 0, block.getWestTexture(), 4)));
            positions.addAll(List.of(x + 1f, y + 1f, z + 1f, (float)compressData(0, 0, block.getWestTexture(), 4)));
            positions.addAll(List.of(x + 1f, y + 0f, z + 1f, (float)compressData(0, 1, block.getWestTexture(), 4)));

            indices.addAll(List.of(
                    0 + passes,
                    1 + passes,
                    2 + passes,
                    2 + passes,
                    3 + passes,
                    0 + passes
            ));
            passes += 4;
        }

        int eastBlock = chunkData.getBlock(x - 1, y + (CHUNK_SECTION_HEIGHT * section), z);
        if (eastBlock == 0 || block.shouldDrawBetween() || (block.getId() != eastBlock && BlockRegistry.getBlock(eastBlock).isTransparent())) {
            positions.addAll(List.of(x + 0f, y + 0f, z + 0f, (float)compressData(1, 1, block.getEastTexture(), 5)));
            positions.addAll(List.of(x + 0f, y + 1f, z + 0f, (float)compressData(1, 0, block.getEastTexture(), 5)));
            positions.addAll(List.of(x + 0f, y + 1f, z + 1f, (float)compressData(0, 0, block.getEastTexture(), 5)));
            positions.addAll(List.of(x + 0f, y + 0f, z + 1f, (float)compressData(0, 1, block.getEastTexture(), 5)));

            indices.addAll(List.of(
                    2 + passes,
                    1 + passes,
                    0 + passes,
                    0 + passes,
                    3 + passes,
                    2 + passes
            ));
            passes += 4;
        }

        int northBlock = chunkData.getBlock(x, y + (CHUNK_SECTION_HEIGHT * section), z - 1);
        if (northBlock == 0 || block.shouldDrawBetween() || (block.getId() != northBlock && BlockRegistry.getBlock(northBlock).isTransparent())) {
            positions.addAll(List.of(x + 0f, y + 0f, z + 0f, (float)compressData(1, 1, block.getNorthTexture(), 0)));
            positions.addAll(List.of(x + 0f, y + 1f, z + 0f, (float)compressData(1, 0, block.getNorthTexture(), 0)));
            positions.addAll(List.of(x + 1f, y + 1f, z + 0f, (float)compressData(0, 0, block.getNorthTexture(), 0)));
            positions.addAll(List.of(x + 1f, y + 0f, z + 0f, (float)compressData(0, 1, block.getNorthTexture(), 0)));

            indices.addAll(List.of(
                    0 + passes,
                    1 + passes,
                    2 + passes,
                    2 + passes,
                    3 + passes,
                    0 + passes
            ));
            passes += 4;
        }

        int southBlock = chunkData.getBlock(x, y + (CHUNK_SECTION_HEIGHT * section), z + 1);
        if (southBlock == 0 || block.shouldDrawBetween() || (block.getId() != southBlock && BlockRegistry.getBlock(southBlock).isTransparent())) {
            positions.addAll(List.of(x + 0f, y + 0f, z + 1f, (float)compressData(1, 1, block.getSouthTexture(), 1)));
            positions.addAll(List.of(x + 0f, y + 1f, z + 1f, (float)compressData(1, 0, block.getSouthTexture(), 1)));
            positions.addAll(List.of(x + 1f, y + 1f, z + 1f, (float)compressData(0, 0, block.getSouthTexture(), 1)));
            positions.addAll(List.of(x + 1f, y + 0f, z + 1f, (float)compressData(0, 1, block.getSouthTexture(), 1)));

            indices.addAll(List.of(
                    2 + passes,
                    1 + passes,
                    0 + passes,
                    0 + passes,
                    3 + passes,
                    2 + passes
            ));
            passes += 4;
        }

        int upBlock = chunkData.getBlock(x, y + 1 + (CHUNK_SECTION_HEIGHT * section), z);
        if (upBlock == 0 || block.shouldDrawBetween() || (block.getId() != upBlock && BlockRegistry.getBlock(upBlock).isTransparent())) {
            positions.addAll(List.of(x + 0f, y + 1f, z + 0f, (float)compressData(1, 1, block.getTopTexture(), 2)));
            positions.addAll(List.of(x + 0f, y + 1f, z + 1f, (float)compressData(1, 0, block.getTopTexture(), 2)));
            positions.addAll(List.of(x + 1f, y + 1f, z + 1f, (float)compressData(0, 0, block.getTopTexture(), 2)));
            positions.addAll(List.of(x + 1f, y + 1f, z + 0f, (float)compressData(0, 1, block.getTopTexture(), 2)));

            indices.addAll(List.of(
                    0 + passes,
                    1 + passes,
                    2 + passes,
                    2 + passes,
                    3 + passes,
                    0 + passes
            ));
            passes += 4;
        }

        int bottomBlock = chunkData.getBlock(x, y - 1 + (CHUNK_SECTION_HEIGHT * section), z);
        if (bottomBlock == 0 || block.shouldDrawBetween() || (block.getId() != bottomBlock && BlockRegistry.getBlock(bottomBlock).isTransparent())) {
            positions.addAll(List.of(x + 0f, y + 0f, z + 0f, (float)compressData(1, 1, block.getBottomTexture(), 3)));
            positions.addAll(List.of(x + 0f, y + 0f, z + 1f, (float)compressData(1, 0, block.getBottomTexture(), 3)));
            positions.addAll(List.of(x + 1f, y + 0f, z + 1f, (float)compressData(0, 0, block.getBottomTexture(), 3)));
            positions.addAll(List.of(x + 1f, y + 0f, z + 0f, (float)compressData(0, 1, block.getBottomTexture(), 3)));

            indices.addAll(List.of(
                    2 + passes,
                    1 + passes,
                    0 + passes,
                    0 + passes,
                    3 + passes,
                    2 + passes
            ));
            passes += 4;
        }
    }

    private int getVertex(int x, int y, int z, int xUv, int yUv, int zUv) {
        return zUv | yUv << 7 | xUv << 8 | z << 9 | x << 16 | y << 23;
    }

    private int compressData(int xUv, int yUv, int texture, int normal) {
        return xUv | yUv << 1 | normal << 2 | texture << 5;
    }

}
