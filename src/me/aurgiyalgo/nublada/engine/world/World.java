package me.aurgiyalgo.nublada.engine.world;

import me.aurgiyalgo.nublada.Nublada;
import me.aurgiyalgo.nublada.engine.utils.Maths;
import me.aurgiyalgo.nublada.engine.utils.PerlinNoise;
import me.aurgiyalgo.nublada.engine.world.populator.Populator;
import me.aurgiyalgo.nublada.engine.world.populator.TerrainPopulator;
import me.aurgiyalgo.nublada.engine.world.populator.TreePopulator;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Handles the render and data for a world
 */
public class World {

    public static final int CHUNK_WIDTH = 32;
    public static final int CHUNK_HEIGHT = 256;

    private final List<Populator> populators;

    private static final ScheduledExecutorService generationService = new ScheduledThreadPoolExecutor(1, r -> {
        Thread thread = new Thread(r, "Generation Thread");
        thread.setDaemon(true);
        return thread;
    });

    private final Map<Vector2i, Chunk> chunks;

    private final PerlinNoise noise;

    private final List<Future<Chunk>> chunkGenerationFutures;

    public World() {
        this.chunks = new HashMap<>();
        this.chunkGenerationFutures = new ArrayList<>();

        this.noise = new PerlinNoise();

        this.populators = new ArrayList<>();
        populators.add(new TerrainPopulator(noise));
        populators.add(new TreePopulator(noise));

        Nublada.LOG.info("World generation seed set to " + noise.getSeed());
    }

    /**
     * Adds a chunk to the world and schedules it for generation
     * @param x Chunk X coordinate
     * @param z Chunk Z coordinate
     */
    public void addChunk(int x, int z) {
        Vector2i position = new Vector2i(x, z);
        if (chunks.containsKey(position))
            throw new RuntimeException("Chunk already loaded");

        Chunk chunk = new Chunk(position, this);

        chunks.put(position, chunk);

        Future<Chunk> future = generationService.submit(() -> {
            chunk.populateChunk(populators);
            return chunk;
        });

        chunkGenerationFutures.add(future);
    }

    /**
     * Queues the chunk to mesh and flags its neighbors as not-updated
     * @param chunk Chunk to mesh
     */
    private void initializeChunk(Chunk chunk) {
        chunk.generateMesh();

        for(int i = -1; i <= 1;i++){
            for(int j = -1; j <= 1;j++){
                if(i == 0 && j == 0){
                    continue;
                }

                Chunk other = getChunk(i + chunk.getPosition().x, j + chunk.getPosition().y);
                if(other != null){
                    other.updated = false;
                }

            }
        }
    }

    /**
     * Checks the chunks in the generation queue and adds those which finished
     */
    public void checkGeneratingChunks() {
        Iterator<Future<Chunk>> iterator = chunkGenerationFutures.iterator();

        while (iterator.hasNext()) {
            Future<Chunk> future = iterator.next();

            if (future.isDone()) {
                iterator.remove();

                try {
                    initializeChunk(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    Nublada.LOG.severe(e.getMessage());
                }
            }
        }
    }

    public Vector3f rayCast(Vector3f position, Vector3f direction, float distance, boolean isPlace) {
        float xPos = (float) Math.floor(position.x);
        float yPos = (float) Math.floor(position.y);
        float zPos = (float) Math.floor(position.z);
        int stepX = Maths.signum(direction.x);
        int stepY = Maths.signum(direction.y);
        int stepZ = Maths.signum(direction.z);
        Vector3f tMax = new Vector3f(Maths.intbound(position.x, direction.x), Maths.intbound(position.y, direction.y), Maths.intbound(position.z, direction.z));
        Vector3f tDelta = new Vector3f((float)stepX / direction.x, (float)stepY / direction.y, (float)stepZ / direction.z);
        float faceX = 0;
        float faceY = 0;
        float faceZ = 0;

        do {
            if (getBlock((int)xPos, (int)yPos, (int)zPos) != 0) {
                if (!isPlace) {
                    return new Vector3f(xPos, yPos, zPos);
                } else {
                    return new Vector3f((int)(xPos + faceX), (int)(yPos + faceY), (int)(zPos + faceZ));
                }
            }
            if (tMax.x < tMax.y) {
                if (tMax.x < tMax.z) {
                    if (tMax.x > distance) break;

                    xPos += stepX;
                    tMax.x += tDelta.x;

                    faceX = -stepX;
                    faceY = 0;
                    faceZ = 0;
                } else {
                    if (tMax.z > distance) break;
                    zPos += stepZ;
                    tMax.z += tDelta.z;
                    faceX = 0;
                    faceY = 0;
                    faceZ = -stepZ;
                }
            } else {
                if (tMax.y < tMax.z) {
                    if (tMax.y > distance) break;
                    yPos += stepY;
                    tMax.y += tDelta.y;
                    faceX = 0;
                    faceY = -stepY;
                    faceZ = 0;
                } else {
                    if (tMax.z > distance) break;
                    zPos += stepZ;
                    tMax.z += tDelta.z;
                    faceX = 0;
                    faceY = 0;
                    faceZ = -stepZ;
                }
            }
        } while (true);

        return null;
    }

    public Chunk getChunk(int x, int z) {
        return chunks.get(new Vector2i(x, z));
    }

    public int getBlock(int x, int y, int z) {
        Vector2i position = getChunkPositionAt(x, z);
        if (!chunks.containsKey(position)) return 0;
        return chunks.get(position)
                .getBlock(Math.abs(position.x * CHUNK_WIDTH - x), y, Math.abs(position.y * CHUNK_WIDTH - z));
    }

    public int getBlock(Vector3f position) {
        return getBlock((int)position.x, (int)position.y, (int)position.z);
    }

    public void setBlock(int voxel, int x, int y, int z) {
        Vector2i position = getChunkPositionAt(x, z);
        if (!chunks.containsKey(position)) {
            Nublada.LOG.warning("Tried to set a block on a non-loaded chunk (" + x + ", " + y + ", " + z + ")");
            return;
        }

        chunks.get(position).setBlock(voxel, Math.abs(position.x * CHUNK_WIDTH - x), y, Math.abs(position.y * CHUNK_WIDTH - z));
    }

    public void setBlock(int voxel, Vector3f position) {
        setBlock(voxel, (int)position.x, (int)position.y, (int)position.z);
    }

    private Vector2i getChunkPositionAt(int x, int z) {
        return new Vector2i((int)Math.floor(x / (float)CHUNK_WIDTH), (int)Math.floor(z / (float)CHUNK_WIDTH));
    }

    public Map<Vector2i, Chunk> getChunks() {
        return chunks;
    }
}
