package me.aurgiyalgo.nublada.engine.world;

import me.aurgiyalgo.nublada.engine.graphics.mesh.MeshBundle;
import me.aurgiyalgo.nublada.engine.utils.PerlinNoise;
import me.aurgiyalgo.nublada.engine.world.populator.Populator;
import org.joml.Vector2i;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static me.aurgiyalgo.nublada.engine.world.World.CHUNK_WIDTH;
import static me.aurgiyalgo.nublada.engine.world.World.CHUNK_HEIGHT;

/**
 * Handles the data and meshing tasks of a single chunk
 */
public class Chunk {

    private static final int NORTH = 0;
    private static final int SOUTH = 2;
    private static final int EAST = 1;
    private static final int WEST = 3;

    private static final Vector2i[] NEIGHBOR_VECTORS = {
            new Vector2i( 0, -1),
            new Vector2i(-1,  0),
            new Vector2i( 0,  1),
            new Vector2i( 1,  0),
    };

    // TODO: 24/03/2022 Make the core count customizable
    private static final ScheduledExecutorService meshService =
            new ScheduledThreadPoolExecutor(2, r -> {
        Thread thread = new Thread(r, "Meshing Thread");
        thread.setDaemon(true);

        return thread;
    });

    private short[] voxels;
    private final Vector2i position;
    private MeshBundle mesh;

    private int currentLodLevel;

    private final World world;

    private final Chunk[] neighbors;

    public boolean updated = false;

    private Future<MeshBundle> mesherFuture;

    public Chunk(Vector2i position, World world) {
        this.position = position;
        this.world = world;

        this.neighbors = new Chunk[4];
    }

    // TODO: 24/01/2022 Implement generators for World Generation
    // Temporary generation code
    public void populateChunk(List<Populator> populators) {
        voxels = new short[CHUNK_WIDTH * CHUNK_HEIGHT * CHUNK_WIDTH];

        for (Populator populator : populators) {
            populator.populate(this);
        }
    }

    /**
     * Queries the chunk to mesh if it is not updated, and loads
     * the mesh data to the GPU when meshing is finished
     */
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

    /**
     * Queues a task to mesh the chunk
     */
    public void generateMesh() {
        if (mesherFuture != null) {
            mesherFuture.cancel(true);
            mesherFuture = null;
        }

        cacheNeighbors();
        mesherFuture = meshService.submit(() -> new MeshBundle(this));
    }

    private void cacheNeighbors() {
        for (int i = 0; i < 4; i++) {
            neighbors[i] = world.getChunk(position.x + NEIGHBOR_VECTORS[i].x, position.y + NEIGHBOR_VECTORS[i].y);
        }
    }

    public int getBlock(int x, int y, int z) {
        if (voxels == null) return 0;
        if (y < 0 || y > CHUNK_HEIGHT - 1) return 0;
        if (x < 0) {
            return neighbors[EAST] != null ? neighbors[EAST].getBlock(x + CHUNK_WIDTH, y, z) : 0;
        }
        if (x > CHUNK_WIDTH - 1) {
            return neighbors[WEST] != null ? neighbors[WEST].getBlock(x - CHUNK_WIDTH, y, z) : 0;
        }
        if (z < 0) {
            return neighbors[NORTH] != null ? neighbors[NORTH].getBlock(x, y, z + CHUNK_WIDTH) : 0;
        }
        if (z > CHUNK_WIDTH - 1) {
            return neighbors[SOUTH] != null ? neighbors[SOUTH].getBlock(x, y, z - CHUNK_WIDTH) : 0;
        }
        return voxels[x | y << 5 | z << 13];
    }

    public void setBlock(int voxel, int x, int y, int z) {
        setBlock(voxel, x, y, z, true);
    }

    public void setBlock(int voxel, int x, int y, int z, boolean updateChunk) {
        if (x < 0 || y < 0 || z < 0) return;
        if (x > CHUNK_WIDTH - 1 || y > CHUNK_HEIGHT - 1 || z > CHUNK_WIDTH - 1) return;
            voxels[x | y << 5 | z << 13] = (short) voxel;

        if (!updateChunk) return;

        updated = false;
        if (x == 0) world.getChunk(position.x - 1, position.y).updated = false;
        if (x == CHUNK_WIDTH - 1) world.getChunk(position.x + 1, position.y).updated = false;
        if (z == 0) world.getChunk(position.x, position.y - 1).updated = false;
        if (z == CHUNK_WIDTH - 1) world.getChunk(position.x, position.y + 1).updated = false;
    }

    public void onDestroy() {
        for (int i = 0; i < 4; i++) {
            Chunk neighbor = neighbors[i];
            if (neighbor == null) continue;
            neighbor.neighbors[(i + 2) % 4] = null;
            neighbor.updated = false;
        }
    }

    public Vector2i getPosition() {
        return position;
    }

    public MeshBundle getModel() {
        return mesh;
    }

    public int getCurrentLodLevel() {
        return currentLodLevel;
    }

    public void setCurrentLodLevel(int lodLevel) {
        this.currentLodLevel = lodLevel;
    }
}
