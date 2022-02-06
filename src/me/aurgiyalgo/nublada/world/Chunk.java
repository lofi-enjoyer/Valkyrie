package me.aurgiyalgo.nublada.world;

import me.aurgiyalgo.nublada.graphics.mesh.DynamicMesher;
import me.aurgiyalgo.nublada.graphics.mesh.Mesh;
import me.aurgiyalgo.nublada.graphics.mesh.GreedyMesher;
import me.aurgiyalgo.nublada.graphics.mesh.MeshBundle;
import me.aurgiyalgo.nublada.utils.PerlinNoise;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static me.aurgiyalgo.nublada.world.World.CHUNK_WIDTH;
import static me.aurgiyalgo.nublada.world.World.CHUNK_HEIGHT;

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

    private static final ScheduledExecutorService meshService =
            new ScheduledThreadPoolExecutor(3, r -> {
       Thread thread = new Thread(r, "Meshing Thread");
       thread.setDaemon(true);

       return thread;
    });

    private short[] voxels;
    private final Vector2i position;
    private MeshBundle mesh;

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
    public void populateChunk(PerlinNoise noise) {
        if (this.voxels != null) return;

        this.voxels = new short[CHUNK_WIDTH * CHUNK_HEIGHT * CHUNK_WIDTH];

        int chunkX = position.x * CHUNK_WIDTH;
        int chunkZ = position.y * CHUNK_WIDTH;
        Vector2i worldCenter = new Vector2i();

        byte voxel;
        for (int x = 0; x < CHUNK_WIDTH; x++) {
            for (int z = 0; z < CHUNK_WIDTH; z++) {

                double noiseValue = (noise.noise(x + chunkX, z + chunkZ) + 1) / 2f;
                double maxHeight = noiseValue * noiseValue * 250;
                double distance = worldCenter.distance(x + chunkX, z + chunkZ) - 256;
                distance = Math.max(distance, 0);
                double gradient = (1 - (distance / 256f)) * 0.5f;
                maxHeight = gradient > 0 ? maxHeight * gradient + 4 : 4;

                for (int y = 0; y < maxHeight; y++) {
                    voxel = 2;
                    if ((int) (maxHeight) == y || gradient < 0) {
                        voxel = 1;
                    }
                    voxels[x | y << 5 | z << 13] = voxel;
                }

                for (int y = 0; y < 20; y++) {
                    if (voxels[x | y << 5 | z << 13] == 0)
                        voxels[x | y << 5 | z << 13] = 7;
                }
            }
        }

        Random random = new Random();
        int treeAmount = random.nextInt(16) + 8;
        for (int i = 0; i < treeAmount; i++) {
            int treeX = random.nextInt(CHUNK_WIDTH - 4) + 2;
            int treeZ = random.nextInt(CHUNK_WIDTH - 4) + 2;

            int height = random.nextInt(8) + 3;

            int treeY = 0;
            for (int y = 1; y < CHUNK_HEIGHT - 15; y++) {
                if (getBlock(treeX, y, treeZ) == 0 && getBlock(treeX, y - 1, treeZ) == 1) {
                    treeY = y;
                    break;
                }
            }

            if (treeY == 0) continue;

            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = 0; z < 5; z++) {
                        if (getBlock(x + treeX - 2, y + treeY + height, z + treeZ - 2) == 0)
                            setBlock(6, x + treeX - 2, y + treeY + height, z + treeZ - 2, false);
                    }
                }
            }

            setBlock(6, treeX, treeY + height + 3, treeZ, false);

            for (int y = 0; y < height + 2; y++) {
                setBlock(3, treeX, y + treeY, treeZ, false);
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

        cacheNeighbors();
        mesherFuture = meshService.submit(() -> new MeshBundle(this));
    }

    private void cacheNeighbors() {
        for (int i = 0; i < 4; i++) {
            neighbors[i] = world.getChunk(position.x + NEIGHBOR_VECTORS[i].x, position.y + NEIGHBOR_VECTORS[i].y);
        }
    }

    // TODO: 24/01/2022 Cache neighbor chunks
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
}
