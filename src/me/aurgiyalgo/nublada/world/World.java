package me.aurgiyalgo.nublada.world;

import me.aurgiyalgo.nublada.graphics.loader.Loader;
import me.aurgiyalgo.nublada.utils.Maths;
import me.aurgiyalgo.nublada.utils.PerlinNoise;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.*;

public class World {

    public static final int CHUNK_WIDTH = 32;
    public static final int CHUNK_HEIGHT = 256;

    // FIXME: 09/01/2022 temporary world dimensions, they will be loaded dynamically around the player
    public static final int WORLD_SIDE = 8;

    private final Map<Vector2f, Chunk> chunks;

    private final List<Chunk> toGenChunks;
    private final List<Chunk> toLoadChunks;

    private final int textureId;
    private final PerlinNoise noise;

    public World(Loader loader) {
        this.chunks = new HashMap<>();
        this.toGenChunks = new LinkedList<>();
        this.toLoadChunks = new LinkedList<>();

        this.noise = new PerlinNoise(40595);

        // FIXME: 09/01/2022 load textures dynamically
        this.textureId = loader.loadTextureArray(
                "res/textures/stone.png",
                "res/textures/grass_side.png",
                "res/textures/grass_top.png",
                "res/textures/log_oak.png",
                "res/textures/log_top.png");

        for (int x = -WORLD_SIDE/2; x < WORLD_SIDE/2; x++) {
            for (int z = -WORLD_SIDE/2; z < WORLD_SIDE/2; z++) {
                genChunk(new Vector2f(x, z));
            }
        }
    }

    private void genChunk(Vector2f position) {
        Chunk chunk = new Chunk(position, this);

        chunks.put(position, chunk);
        toGenChunks.add(chunk);
    }

    public void generateNextChunk() {
        if (toGenChunks.isEmpty()) return;
        Chunk nextChunk = toGenChunks.get(0);
        nextChunk.populateChunk(noise);
        nextChunk.computeMesh();
        toGenChunks.remove(nextChunk);
        toLoadChunks.add(nextChunk);
    }

    public void updateNextChunk() {
        if (toLoadChunks.isEmpty()) return;
        Chunk nextChunk = toLoadChunks.get(0);
        nextChunk.loadModel();
        toLoadChunks.remove(nextChunk);
    }

    public synchronized void addChunkToUpdate(Chunk chunk) {
        toGenChunks.add(0, chunk);
    }

    public void raycast(Vector3f position, Vector3f direction, float distance, boolean isPlace) {
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
                    setBlock(0, (int)xPos, (int)yPos, (int)zPos);
                }
                else setBlock(3, (int)(xPos + faceX), (int)(yPos + faceY), (int)(zPos + faceZ));
                break;
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
    }

    public int getBlock(int x, int y, int z) {
        Vector2f position = getChunkPositionAt(x, z);
        if (!chunks.containsKey(position)) return 0;
        return chunks.get(position)
                .getBlock((int)Math.abs(position.x * CHUNK_WIDTH - x), y, (int)Math.abs(position.y * CHUNK_WIDTH - z));
    }

    public int getBlock(Vector3f position) {
        return getBlock((int)position.x, (int)position.y, (int)position.z);
    }

    public void setBlock(int voxel, int x, int y, int z) {
        Vector2f position = getChunkPositionAt(x, z);
        if (!chunks.containsKey(position)) {
            Chunk chunk = new Chunk(position, this);
            chunks.put(position, chunk);
            chunk.setBlock(voxel, (int)Math.abs(position.x * CHUNK_WIDTH - x), y, (int)Math.abs(position.y * CHUNK_WIDTH - z));
            return;
        }
        chunks.get(position).setBlock(voxel, (int)Math.abs(position.x * CHUNK_WIDTH - x), y, (int)Math.abs(position.y * CHUNK_WIDTH - z));
    }

    public void setBlock(int voxel, Vector3f position) {
        setBlock(voxel, (int)position.x, (int)position.y, (int)position.z);
    }

    private Vector2f getChunkPositionAt(int x, int z) {
        return new Vector2f((int)Math.floor(x / (float)CHUNK_WIDTH), (int)Math.floor(z / (float)CHUNK_WIDTH));
    }

    public Map<Vector2f, Chunk> getChunks() {
        return chunks;
    }

    public int getTextureId() {
        return textureId;
    }
}
