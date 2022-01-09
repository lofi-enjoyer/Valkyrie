package me.aurgiyalgo.nublada.world;

import me.aurgiyalgo.nublada.graphics.mesh.Mesh;
import me.aurgiyalgo.nublada.graphics.mesh.GreedyMesher;
import me.aurgiyalgo.nublada.utils.PerlinNoise;
import org.joml.Vector2f;

import static me.aurgiyalgo.nublada.world.World.CHUNK_WIDTH;
import static me.aurgiyalgo.nublada.world.World.CHUNK_HEIGHT;

public class Chunk {

    private int[][][] voxels;
    private final Vector2f position;
    private Mesh mesh;

    private GreedyMesher mesher;

    private final World world;

    public boolean updated = false;

    public Chunk(Vector2f position, World world) {
        this.position = position;
        this.world = world;
    }

    public void populateChunk(PerlinNoise noise) {
        if (this.voxels != null) return;
        this.voxels = new int[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_WIDTH];
        int voxel;
        long timer = System.nanoTime();
        for (int x = 0; x < CHUNK_WIDTH; x++) {
            for (int z = 0; z < CHUNK_WIDTH; z++) {
                double maxHeight = (noise.noise(x + position.x * CHUNK_WIDTH, z + position.y * CHUNK_WIDTH) + 1) / 2f * 120 + 4;
                for (int y = 0; y < CHUNK_HEIGHT; y++) {
                    if (maxHeight >= y) {
                        if ((int) (maxHeight) == y)
                            voxel = 1;
                        else voxel = 2;
                    } else {
                        voxel = 0;
                    }
                    voxels[x][y][z] = voxel;

                }
            }
        }
        System.out.println("Chunk gen: " + ((System.nanoTime() - timer) / 1000000f) + "ms");
    }

    public void computeMesh() {
        this.mesher = new GreedyMesher(this);
        this.mesher.compute();

        voxels = null;
    }

    public void loadModel() {
        mesh = mesher.loadMeshToGpu();
        updated = true;
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
        if (!updated) return;
        world.addChunkToUpdate(this);
        updated = false;
    }

    public Vector2f getPosition() {
        return position;
    }

    public Mesh getModel() {
        return mesh;
    }
}
