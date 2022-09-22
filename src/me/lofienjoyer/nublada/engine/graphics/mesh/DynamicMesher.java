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

    private List<Float> positions;
    private List<Integer> indices;
    private List<Float> uvs;
    private List<Float> light;

    private float[] positionsArray;
    private int[] indicesArray;
    private float[] uvsArray;
    private float[] lightArray;

    public DynamicMesher(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public Mesher compute() {
        this.positions = new ArrayList<>(10000);
        this.indices = new ArrayList<>(6000);
        this.uvs = new ArrayList<>(10000);
        this.light = new ArrayList<>(10000);

        computeMesh();

        positionsArray = new float[positions.size()];
        for (int i = 0; i < positions.size(); i++) {
            positionsArray[i] = positions.get(i);
        }
        positions = null;

        indicesArray = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }
        indices = null;

        uvsArray = new float[uvs.size()];
        for (int i = 0; i < uvs.size(); i++) {
            uvsArray[i] = uvs.get(i);
        }
        uvs = null;

        lightArray = new float[light.size()];
        for (int i = 0; i < light.size(); i++) {
            lightArray[i] = light.get(i);
        }
        light = null;

        return this;
    }

    @Override
    public Mesh loadToGpu() {
        Mesh mesh = new Mesh(positionsArray, indicesArray, uvsArray, lightArray);
        positionsArray = null;
        indicesArray = null;
        uvsArray = null;
        lightArray = null;
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
            positions.addAll(List.of(x + 1f, y + 0f, z + 0f));
            positions.addAll(List.of(x + 1f, y + 1f, z + 0f));
            positions.addAll(List.of(x + 1f, y + 1f, z + 1f));
            positions.addAll(List.of(x + 1f, y + 0f, z + 1f));

            indices.addAll(List.of(
                    0 + passes,
                    1 + passes,
                    2 + passes,
                    2 + passes,
                    3 + passes,
                    0 + passes
            ));
            passes += 4;

            uvs.addAll(List.of(1f, 1f, (float) block.getWestTexture()));
            uvs.addAll(List.of(1f, 0f, (float) block.getWestTexture()));
            uvs.addAll(List.of(0f, 0f, (float) block.getWestTexture()));
            uvs.addAll(List.of(0f, 1f, (float) block.getWestTexture()));

            for (int i = 0; i < 4; i++) {
                light.add(0f);
            }
        }

        int eastBlock = chunk.getBlock(x - 1, y, z);
        if (eastBlock == 0 || (block.getId() != eastBlock && BlockRegistry.getBLock(eastBlock).isTransparent())) {
            positions.addAll(List.of(x + 0f, y + 0f, z + 0f));
            positions.addAll(List.of(x + 0f, y + 1f, z + 0f));
            positions.addAll(List.of(x + 0f, y + 1f, z + 1f));
            positions.addAll(List.of(x + 0f, y + 0f, z + 1f));

            indices.addAll(List.of(
                    2 + passes,
                    1 + passes,
                    0 + passes,
                    0 + passes,
                    3 + passes,
                    2 + passes
            ));
            passes += 4;

            uvs.addAll(List.of(1f, 1f, (float) block.getEastTexture()));
            uvs.addAll(List.of(1f, 0f, (float) block.getEastTexture()));
            uvs.addAll(List.of(0f, 0f, (float) block.getEastTexture()));
            uvs.addAll(List.of(0f, 1f, (float) block.getEastTexture()));

            for (int i = 0; i < 4; i++) {
                light.add(1f);
            }
        }

        int northBlock = chunk.getBlock(x, y, z - 1);
        if (northBlock == 0 || (block.getId() != northBlock && BlockRegistry.getBLock(northBlock).isTransparent())) {
            positions.addAll(List.of(x + 0f, y + 0f, z + 0f));
            positions.addAll(List.of(x + 0f, y + 1f, z + 0f));
            positions.addAll(List.of(x + 1f, y + 1f, z + 0f));
            positions.addAll(List.of(x + 1f, y + 0f, z + 0f));

            indices.addAll(List.of(
                    0 + passes,
                    1 + passes,
                    2 + passes,
                    2 + passes,
                    3 + passes,
                    0 + passes
            ));
            passes += 4;

            uvs.addAll(List.of(1f, 1f, (float) block.getNorthTexture()));
            uvs.addAll(List.of(1f, 0f, (float) block.getNorthTexture()));
            uvs.addAll(List.of(0f, 0f, (float) block.getNorthTexture()));
            uvs.addAll(List.of(0f, 1f, (float) block.getNorthTexture()));

            for (int i = 0; i < 4; i++) {
                light.add(5f);
            }
        }

        int southBlock = chunk.getBlock(x, y, z + 1);
        if (southBlock == 0 || (block.getId() != southBlock && BlockRegistry.getBLock(southBlock).isTransparent())) {
            positions.addAll(List.of(x + 0f, y + 0f, z + 1f));
            positions.addAll(List.of(x + 0f, y + 1f, z + 1f));
            positions.addAll(List.of(x + 1f, y + 1f, z + 1f));
            positions.addAll(List.of(x + 1f, y + 0f, z + 1f));

            indices.addAll(List.of(
                    2 + passes,
                    1 + passes,
                    0 + passes,
                    0 + passes,
                    3 + passes,
                    2 + passes
            ));
            passes += 4;

            uvs.addAll(List.of(1f, 1f, (float) block.getSouthTexture()));
            uvs.addAll(List.of(1f, 0f, (float) block.getSouthTexture()));
            uvs.addAll(List.of(0f, 0f, (float) block.getSouthTexture()));
            uvs.addAll(List.of(0f, 1f, (float) block.getSouthTexture()));

            for (int i = 0; i < 4; i++) {
                light.add(4f);
            }
        }

        int upBlock = chunk.getBlock(x, y + 1, z);
        if (upBlock == 0 || (block.getId() != upBlock && BlockRegistry.getBLock(upBlock).isTransparent())) {
            positions.addAll(List.of(x + 0f, y + 1f, z + 0f));
            positions.addAll(List.of(x + 0f, y + 1f, z + 1f));
            positions.addAll(List.of(x + 1f, y + 1f, z + 1f));
            positions.addAll(List.of(x + 1f, y + 1f, z + 0f));

            indices.addAll(List.of(
                    0 + passes,
                    1 + passes,
                    2 + passes,
                    2 + passes,
                    3 + passes,
                    0 + passes
            ));
            passes += 4;

            uvs.addAll(List.of(1f, 1f, (float) block.getTopTexture()));
            uvs.addAll(List.of(1f, 0f, (float) block.getTopTexture()));
            uvs.addAll(List.of(0f, 0f, (float) block.getTopTexture()));
            uvs.addAll(List.of(0f, 1f, (float) block.getTopTexture()));

            for (int i = 0; i < 4; i++) {
                light.add(2f);
            }
        }

        int bottomBlock = chunk.getBlock(x, y - 1, z);
        if (bottomBlock == 0 || (block.getId() != bottomBlock && BlockRegistry.getBLock(bottomBlock).isTransparent())) {
            positions.addAll(List.of(x + 0f, y + 0f, z + 0f));
            positions.addAll(List.of(x + 0f, y + 0f, z + 1f));
            positions.addAll(List.of(x + 1f, y + 0f, z + 1f));
            positions.addAll(List.of(x + 1f, y + 0f, z + 0f));

            indices.addAll(List.of(
                    2 + passes,
                    1 + passes,
                    0 + passes,
                    0 + passes,
                    3 + passes,
                    2 + passes
            ));
            passes += 4;

            uvs.addAll(List.of(1f, 1f, (float) block.getBottomTexture()));
            uvs.addAll(List.of(1f, 0f, (float) block.getBottomTexture()));
            uvs.addAll(List.of(0f, 0f, (float) block.getBottomTexture()));
            uvs.addAll(List.of(0f, 1f, (float) block.getBottomTexture()));

            for (int i = 0; i < 4; i++) {
                light.add(3f);
            }
        }
    }

}
