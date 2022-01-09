package me.aurgiyalgo.nublada.world;

import me.aurgiyalgo.nublada.graphics.model.ModelLoader;
import me.aurgiyalgo.nublada.utils.Maths;
import me.aurgiyalgo.nublada.utils.PerlinNoise;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.*;

public class World {

    public static final int CHUNK_WIDTH = 32;
    public static final int CHUNK_HEIGHT = 64;

    public static final int WORLD_SIDE = 32;

    public Map<Vector2f, Chunk> chunks;

    private List<Chunk> toGenChunks;
    private final List<Chunk> toLoadChunks;

    private int textureId;
    private PerlinNoise noise;
    private Random random;

    public World(ModelLoader loader) {
        this.chunks = new HashMap<>();
        this.toGenChunks = new LinkedList<>();
        this.toLoadChunks = new LinkedList<>();

        this.noise = new PerlinNoise(40595);
        this.random = new Random();

        this.textureId = loader.loadTextureArray("res/textures/stone.png", "res/textures/obsidian.png", "res/textures/grass_top.png");

        for (int x = 0; x < WORLD_SIDE; x++) {
            for (int z = 0; z < WORLD_SIDE; z++) {
                genChunk(new Vector2f(x, z));
            }
        }
    }

    private void genChunk(Vector2f position) {
        Chunk chunk = new Chunk(position);

        chunks.put(position, chunk);
        toGenChunks.add(chunk);
    }

    public synchronized void generateNextChunk() {
        if (toGenChunks.isEmpty()) return;
        Chunk nextChunk  = toGenChunks.get(0);
        nextChunk.populateChunk(noise, random);
        toGenChunks.remove(0);
        toLoadChunks.add(nextChunk);
    }

    public synchronized void updateNextChunk() {
        if (toLoadChunks.isEmpty()) return;
        Chunk nextChunk = toLoadChunks.get(0);
        nextChunk.loadModel();
        toLoadChunks.remove(0);
    }

    public void raycast(Vector3f position, Vector3f direction, float distance, boolean isPlace) {
//        List<Vector3f> voxelPositions = getIntersectedVoxels(position, direction, distance);
//        if (voxelPositions.isEmpty()) {
//            return;
//        }
//        Vector3f previous = voxelPositions.get(0);
//        System.out.println(voxelPositions.size());
//
//        for (Vector3f voxelPosition : voxelPositions) {
//            int voxelId = getBlock(voxelPosition);
//            if (voxelId != 0) {
//                if (isPlace) {
//                    setBlock(1, previous);
//                    System.out.println(previous);
//                } else {
//                    setBlock(0, voxelPosition);
//                    System.out.println(voxelPosition);
//                }
//            }
//            previous = voxelPosition;
//        }
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
                int voxel = 2;
                if (!isPlace) {
                    voxel = 0;
                    setBlock(voxel, (int)xPos, (int)yPos, (int)zPos);
                }
                else setBlock(voxel, (int)(xPos + faceX), (int)(yPos + faceY), (int)(zPos + faceZ));
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

    private List<Vector3f> getIntersectedVoxels(Vector3f startPoint, Vector3f direction, float range) {
        Vector3f endPoint = new Vector3f(startPoint).add(new Vector3f(direction).mul(range));
        Vector3f startVoxel = new Vector3f(startPoint);

        int FLT_MAX = 10;

        // +1, -1, or 0
        int stepX = (direction.x > 0) ? 1 : ((direction.x < 0) ? -1 : 0);
        int stepY = (direction.y > 0) ? 1 : ((direction.y < 0) ? -1 : 0);
        int stepZ = (direction.z > 0) ? 1 : ((direction.z < 0) ? -1 : 0);

        float tDeltaX =
                (stepX != 0) ? Math.min(stepX / (endPoint.x - startPoint.x), FLT_MAX) : FLT_MAX;
        float tDeltaY =
                (stepY != 0) ? Math.min(stepY / (endPoint.y - startPoint.y), FLT_MAX) : FLT_MAX;
        float tDeltaZ =
                (stepZ != 0) ? Math.min(stepZ / (endPoint.z - startPoint.z), FLT_MAX) : FLT_MAX;

        float tMaxX = (stepX > 0.0f) ? tDeltaX * (1.0f - startPoint.x + startVoxel.x)
                : tDeltaX * (startPoint.x - startVoxel.x);
        float tMaxY = (stepY > 0.0f) ? tDeltaY * (1.0f - startPoint.y + startVoxel.y)
                : tDeltaY * (startPoint.y - startVoxel.y);
        float tMaxZ = (stepZ > 0.0f) ? tDeltaZ * (1.0f - startPoint.z + startVoxel.z)
                : tDeltaZ * (startPoint.z - startVoxel.z);

        Vector3f currentVoxel = new Vector3f(startVoxel);
        List<Vector3f> intersected = new ArrayList<>();
        intersected.add(startVoxel);

        // sanity check to prevent leak
        while (intersected.size() < range * 3) {
            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    currentVoxel.x += stepX;
                    tMaxX += tDeltaX;
                }
                else {
                    currentVoxel.z += stepZ;
                    tMaxZ += tDeltaZ;
                }
            }
            else {
                if (tMaxY < tMaxZ) {
                    currentVoxel.y += stepY;
                    tMaxY += tDeltaY;
                }
                else {
                    currentVoxel.z += stepZ;
                    tMaxZ += tDeltaZ;
                }
            }
            if (tMaxX > 1 && tMaxY > 1 && tMaxZ > 1)
                break;
            intersected.add(currentVoxel);
        }
        return intersected;
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
            Chunk chunk = new Chunk(position);
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

    public int getTextureId() {
        return textureId;
    }
}
