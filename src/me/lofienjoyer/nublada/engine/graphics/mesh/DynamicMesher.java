package me.lofienjoyer.nublada.engine.graphics.mesh;

import me.lofienjoyer.nublada.engine.world.Block;
import me.lofienjoyer.nublada.engine.world.BlockRegistry;
import me.lofienjoyer.nublada.engine.world.Chunk;

import java.util.ArrayList;
import java.util.List;

import static me.lofienjoyer.nublada.engine.world.World.CHUNK_WIDTH;
import static me.lofienjoyer.nublada.engine.world.World.CHUNK_HEIGHT;

/**
 * Generates the transparent mesh of a chunk
 */
public class DynamicMesher implements Mesher {

    private final Chunk chunk;

    private List<Integer> positions;
    private List<Integer> indices;

    private int[] positionsArray;
    private int[] indicesArray;

    public DynamicMesher(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public Mesher compute() {
        this.positions = new ArrayList<>(10000);
        this.indices = new ArrayList<>(6000);

        computeMesh();

        positionsArray = new int[positions.size()];
        for (int i = 0; i < positions.size(); i++) {
            positionsArray[i] = positions.get(i);
        }
        positions = null;

        indicesArray = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }
        indices = null;

        return this;
    }

    @Override
    public Mesh loadToGpu() {
        Mesh mesh = new Mesh(positionsArray, indicesArray);
        positionsArray = null;
        indicesArray = null;
        return mesh;
    }

    private void computeMesh() {
        int currentVoxel;
        for (int x = 0; x < CHUNK_WIDTH; x++) {
            for (int y = 0; y < CHUNK_HEIGHT; y++) {
                for (int z = 0; z < CHUNK_WIDTH; z++) {
                    currentVoxel = chunk.getBlock(x, y, z);
                    if (currentVoxel == 0 || !BlockRegistry.getBLock(currentVoxel).isTransparent()) continue;

                    meshBlock(x, y, z, BlockRegistry.getBLock(currentVoxel));
                }
            }
        }
    }

    int passes = 0;

    private void meshBlock(int x, int y, int z, Block block) {
        int westBlock = chunk.getBlock(x + 1, y, z);
        if (westBlock == 0 || (block.getId() != westBlock && BlockRegistry.getBLock(westBlock).isTransparent())) {
            int[] vertices = new int[4];
            vertices[0] = getVertex(x + 1, y + 0, z + 0, 1, 1, block.getWestTexture());
            vertices[1] = getVertex(x + 1, y + 1, z + 0, 1, 0, block.getWestTexture());
            vertices[2] = getVertex(x + 1, y + 1, z + 1, 0, 0, block.getWestTexture());
            vertices[3] = getVertex(x + 1, y + 0, z + 1, 0, 1, block.getWestTexture());
            positions.add(vertices[0]);
            positions.add(vertices[1]);
            positions.add(vertices[2]);
            positions.add(vertices[3]);

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

        int eastBlock = chunk.getBlock(x - 1, y, z);
        if (eastBlock == 0 || (block.getId() != eastBlock && BlockRegistry.getBLock(eastBlock).isTransparent())) {
            int[] vertices = new int[4];
            vertices[0] = getVertex(x + 0, y + 0, z + 0, 1, 1, block.getEastTexture());
            vertices[1] = getVertex(x + 0, y + 1, z + 0, 1, 0, block.getEastTexture());
            vertices[2] = getVertex(x + 0, y + 1, z + 1, 0, 0, block.getEastTexture());
            vertices[3] = getVertex(x + 0, y + 0, z + 1, 0, 1, block.getEastTexture());
            positions.add(vertices[0]);
            positions.add(vertices[1]);
            positions.add(vertices[2]);
            positions.add(vertices[3]);

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

        int northBlock = chunk.getBlock(x, y, z - 1);
        if (northBlock == 0 || (block.getId() != northBlock && BlockRegistry.getBLock(northBlock).isTransparent())) {
            int[] vertices = new int[4];
            vertices[0] = getVertex(x + 0, y + 0, z + 0, 1, 1, block.getNorthTexture());
            vertices[1] = getVertex(x + 0, y + 1, z + 0, 1, 0, block.getNorthTexture());
            vertices[2] = getVertex(x + 1, y + 1, z + 0, 0, 0, block.getNorthTexture());
            vertices[3] = getVertex(x + 1, y + 0, z + 0, 0, 1, block.getNorthTexture());
            positions.add(vertices[0]);
            positions.add(vertices[1]);
            positions.add(vertices[2]);
            positions.add(vertices[3]);

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

        int southBlock = chunk.getBlock(x, y, z + 1);
        if (southBlock == 0 || (block.getId() != southBlock && BlockRegistry.getBLock(southBlock).isTransparent())) {
            int[] vertices = new int[4];
            vertices[0] = getVertex(x + 0, y + 0, z + 1, 1, 1, block.getSouthTexture());
            vertices[1] = getVertex(x + 0, y + 1, z + 1, 1, 0, block.getSouthTexture());
            vertices[2] = getVertex(x + 1, y + 1, z + 1, 0, 0, block.getSouthTexture());
            vertices[3] = getVertex(x + 1, y + 0, z + 1, 0, 1, block.getSouthTexture());
            positions.add(vertices[0]);
            positions.add(vertices[1]);
            positions.add(vertices[2]);
            positions.add(vertices[3]);

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

        int upBlock = chunk.getBlock(x, y + 1, z);
        if (upBlock == 0 || (block.getId() != upBlock && BlockRegistry.getBLock(upBlock).isTransparent())) {
            int[] vertices = new int[4];
            vertices[0] = getVertex(x + 0, y + 1, z + 0, 1, 1, block.getTopTexture());
            vertices[1] = getVertex(x + 0, y + 1, z + 1, 1, 0, block.getTopTexture());
            vertices[2] = getVertex(x + 1, y + 1, z + 1, 0, 0, block.getTopTexture());
            vertices[3] = getVertex(x + 1, y + 1, z + 0, 0, 1, block.getTopTexture());
            positions.add(vertices[0]);
            positions.add(vertices[1]);
            positions.add(vertices[2]);
            positions.add(vertices[3]);

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

        int bottomBlock = chunk.getBlock(x, y - 1, z);
        if (bottomBlock == 0 || (block.getId() != bottomBlock && BlockRegistry.getBLock(bottomBlock).isTransparent())) {
            int[] vertices = new int[4];
            vertices[0] = getVertex(x + 0, y + 0, z + 0, 1, 1, block.getBottomTexture());
            vertices[1] = getVertex(x + 0, y + 0, z + 1, 1, 0, block.getBottomTexture());
            vertices[2] = getVertex(x + 1, y + 0, z + 1, 0, 0, block.getBottomTexture());
            vertices[3] = getVertex(x + 1, y + 0, z + 0, 0, 1, block.getBottomTexture());
            positions.add(vertices[0]);
            positions.add(vertices[1]);
            positions.add(vertices[2]);
            positions.add(vertices[3]);

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

}
