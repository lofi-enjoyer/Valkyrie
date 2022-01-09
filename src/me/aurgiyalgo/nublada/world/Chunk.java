package me.aurgiyalgo.nublada.world;

import me.aurgiyalgo.nublada.graphics.model.Model;
import me.aurgiyalgo.nublada.utils.PerlinNoise;
import org.joml.Vector2f;

import java.util.Random;

import static me.aurgiyalgo.nublada.world.World.CHUNK_WIDTH;
import static me.aurgiyalgo.nublada.world.World.CHUNK_HEIGHT;

public class Chunk {

    private int[][][] voxels;
    private final Vector2f position;
    private Model model;
    private boolean isGenerated;
    private boolean isReady;

    private WorldMesher mesher;

    public Chunk(Vector2f position) {
        this.position = position;
        this.voxels = new int[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_WIDTH];
    }

    public void populateChunk(PerlinNoise noise, Random random) {
//        this.voxels = new int[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_WIDTH];
        int voxel;
        for (int x = 0; x < CHUNK_WIDTH; x++) {
            for (int y = 0; y < CHUNK_HEIGHT; y++) {
                for (int z = 0; z < CHUNK_WIDTH; z++) {
                    if ((noise.noise(x + position.x * CHUNK_WIDTH, z + position.y * CHUNK_WIDTH) + 1) / 4f * 32 + 24 >= y) {
                        if ((int) ((noise.noise(x + position.x * CHUNK_WIDTH, z + position.y * CHUNK_WIDTH) + 1) / 4f * 32 + 24) == y)
                            voxel = 3;
                        else voxel = 1;
                    } else {
                        voxel = 0;
                    }
                    voxels[x][y][z] = voxel;
                }
            }
        }
        this.mesher = new WorldMesher(this);
        this.mesher.compute();

        voxels = null;
        isGenerated = true;
    }

    public void loadModel() {
        model = mesher.loadModel();
        isReady = true;
    }

    public int getBlock(int x, int y, int z) {
        if (voxels == null) return 0;
        if (x < 0 || y < 0 || z < 0) return 0;
        if (x > CHUNK_WIDTH - 1 || y > CHUNK_HEIGHT - 1 || z > CHUNK_WIDTH - 1) return 0;
        return voxels[x][y][z];
    }

    public void setBlock(int voxel, int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0) return;
        if (x > CHUNK_WIDTH - 1 || y > CHUNK_HEIGHT - 1 || z > CHUNK_WIDTH - 1) return;
        voxels[x][y][z] = voxel;
        loadModel();
    }

    public boolean IsBlockFaceVisible(int[] blockPosition, int axis, boolean backFace) {
        blockPosition[axis] += backFace ? -1 : 1;
        return getBlock(blockPosition[0], blockPosition[1], blockPosition[2]) != 0;
    }

    public boolean CompareStep(int[] a, int[] b, int direction, boolean backFace) {
        int blockA = getBlock(a[0], a[1], a[2]);
        int blockB = getBlock(b[0], b[1], b[2]);

        return blockA == blockB && blockB != 0 && IsBlockFaceVisible(b, direction, backFace);
    }

    public Vector2f getPosition() {
        return position;
    }

    public Model getModel() {
        return model;
    }

    public boolean isGenerated() {
        return isGenerated;
    }

    public boolean isReady() {
        return isReady;
    }
}
