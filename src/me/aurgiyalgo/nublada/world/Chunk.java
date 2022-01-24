package me.aurgiyalgo.nublada.world;

import me.aurgiyalgo.nublada.graphics.mesh.Mesh;
import me.aurgiyalgo.nublada.graphics.mesh.GreedyMesher;
import me.aurgiyalgo.nublada.utils.PerlinNoise;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static me.aurgiyalgo.nublada.world.World.CHUNK_WIDTH;
import static me.aurgiyalgo.nublada.world.World.CHUNK_HEIGHT;

public class Chunk {

    private static final ScheduledExecutorService meshService =
            new ScheduledThreadPoolExecutor(3, r -> {
       Thread thread = new Thread(r, "Meshing Thread");
       thread.setDaemon(true);

       return thread;
    });

    private int[][][] voxels;
    private final Vector2i position;
    private Mesh mesh;

    private final World world;

    public boolean updated = false;

    private Future<GreedyMesher> mesherFuture;

    public Chunk(Vector2i position, World world) {
        this.position = position;
        this.world = world;
    }

    // TODO: 24/01/2022 Implement generators for World Generation
    public void populateChunk(PerlinNoise noise) {
        if (this.voxels != null) return;

        this.voxels = new int[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_WIDTH];

        int voxel;
        for (int x = 0; x < CHUNK_WIDTH; x++) {
            for (int z = 0; z < CHUNK_WIDTH; z++) {

                double maxHeight = (noise.noise(x + position.x * CHUNK_WIDTH, z + position.y * CHUNK_WIDTH) + 1) / 2f * 250 + 4;

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
    }

    public void prepare() {
        if (!updated) {
            updated = true;
            generateMesh();
        }

        if (mesherFuture != null && mesherFuture.isDone()) {
            try {
                mesh = mesherFuture.get().loadMeshToGpu();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } finally {
                mesherFuture = null;
            }
        }
    }

    public void generateMesh() {
        if (mesherFuture != null) {
            mesherFuture.cancel(true);
            mesherFuture = null;
        }

        mesherFuture = meshService.submit(() -> new GreedyMesher(this));
    }

    // TODO: 24/01/2022 Cache neighbor chunks
    public int getBlock(int x, int y, int z) {
        if (voxels == null) return 0;
        if (y < 0 || y > CHUNK_HEIGHT - 1) return 0;
        if (x < 0) {
            Chunk neighbor = world.getChunk(position.x - 1, position.y);
            if (neighbor == null) return 0;
            return neighbor.getBlock(x + CHUNK_WIDTH, y, z);
        }
        if (x > CHUNK_WIDTH - 1) {
            Chunk neighbor = world.getChunk(position.x + 1, position.y);
            if (neighbor == null) return 0;
            return neighbor.getBlock(x - CHUNK_WIDTH, y, z);
        }
        if (z < 0) {
            Chunk neighbor = world.getChunk(position.x, position.y - 1);
            if (neighbor == null) return 0;
            return neighbor.getBlock(x, y, z + CHUNK_WIDTH);
        }
        if (z > CHUNK_WIDTH - 1) {
            Chunk neighbor = world.getChunk(position.x, position.y + 1);
            if (neighbor == null) return 0;
            return neighbor.getBlock(x, y, z - CHUNK_WIDTH);
        }
        return voxels[x][y][z];
    }

    public void setBlock(int voxel, int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0) return;
        if (x > CHUNK_WIDTH - 1 || y > CHUNK_HEIGHT - 1 || z > CHUNK_WIDTH - 1) return;
        if (voxels == null) {
            this.voxels = new int[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_WIDTH];
            voxels[x][y][z] = voxel;
            updated = false;
            return;
        }
        voxels[x][y][z] = voxel;
        updated = false;

        if (x == 0) world.getChunk(position.x - 1, position.y).updated = false;
        if (x == CHUNK_WIDTH - 1) world.getChunk(position.x + 1, position.y).updated = false;
        if (z == 0) world.getChunk(position.x, position.y - 1).updated = false;
        if (z == CHUNK_WIDTH - 1) world.getChunk(position.x, position.y + 1).updated = false;
    }

    public Vector2i getPosition() {
        return position;
    }

    public Mesh getModel() {
        return mesh;
    }
}
