package me.lofienjoyer.nublada.engine.graphics.mesh;

import me.lofienjoyer.nublada.engine.world.Block;
import me.lofienjoyer.nublada.engine.world.BlockRegistry;
import me.lofienjoyer.nublada.engine.world.Chunk;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static me.lofienjoyer.nublada.engine.world.World.CHUNK_WIDTH;
import static me.lofienjoyer.nublada.engine.world.World.CHUNK_HEIGHT;

/**
 * Based on roboleary's algorithm, but improved to support non-cubic chunks
 * @see <a href="https://github.com/roboleary/GreedyMesh">GreedyMesh</a>
 */
public class GreedyMesher implements Mesher {

    private final int[] dims;

    private final Chunk chunk;

    private int passes = 0;

    private static final int SOUTH      = 0;
    private static final int NORTH      = 1;
    private static final int EAST       = 2;
    private static final int WEST       = 3;
    private static final int TOP        = 4;
    private static final int BOTTOM     = 5;

    private List<Float> positions;
    private List<Integer> indices;
    private List<Float> uvs;
    private List<Float> light;

    private float[] positionsArray;
    private int[] indicesArray;
    private float[] uvsArray;
    private float[] lightArray;

    public GreedyMesher(Chunk chunk) {
        this.chunk = chunk;

        this.dims = new int[] {CHUNK_WIDTH, CHUNK_HEIGHT, CHUNK_WIDTH};
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
        int i, j, k, l, w, h, u, v, n;

        final int[] x = new int []{0,0,0};
        final int[] q = new int []{0,0,0};
        final int[] du = new int[]{0,0,0};
        final int[] dv = new int[]{0,0,0};

        int[] mask;

        int voxelFace, voxelFace1;

        for (boolean backFace = true, b = false; b != backFace; backFace = false, b = !b) {

            for(int d = 0; d < 3; d++) {

                u = (d + 1) % 3;
                v = (d + 2) % 3;

                x[0] = 0;
                x[1] = 0;
                x[2] = 0;

                q[0] = 0;
                q[1] = 0;
                q[2] = 0;
                q[d] = 1;

                mask = new int [(dims[u] + 1) * (dims[v] + 1)];

                for(x[d] = -1; x[d] < dims[d];) {

                    n = 0;

                    for(x[v] = 0; x[v] < dims[v]; x[v]++) {

                        for(x[u] = 0; x[u] < dims[u]; x[u]++) {

                            voxelFace  = chunk.getBlock(x[0], x[1], x[2]);
                            voxelFace1 = chunk.getBlock((x[0] + q[0]), (x[1] + q[1]), (x[2] + q[2]));

                            if (voxelFace != 0 && BlockRegistry.getBLock(voxelFace).isTransparent()) {
                                voxelFace = 0;
                            }

                            if (voxelFace1 != 0 && BlockRegistry.getBLock(voxelFace1).isTransparent()) {
                                voxelFace1 = 0;
                            }

                            mask[n++] = ((voxelFace == 0 || voxelFace1 == 0))
                                    ? backFace ? voxelFace1 : voxelFace
                                    : 0;
                        }
                    }

                    x[d]++;

                    n = 0;

                    for(j = 0; j < dims[v]; j++) {

                        for(i = 0; i < dims[u];) {

                            if(mask[n] != 0) {

                                for(w = 1; i + w < dims[u] && mask[n + w] != 0 && mask[n + w] == mask[n]; w++) {}

                                boolean done = false;

                                for(h = 1; j + h < dims[v]; h++) {

                                    for(k = 0; k < w; k++) {

                                        if(mask[n + k + h * dims[u]] == 0 || mask[n + k + h * dims[u]] != mask[n]) { done = true; break; }
                                    }

                                    if(done) { break; }
                                }

                                if (mask[n] != 0) {
                                    x[u] = i;
                                    x[v] = j;

                                    du[0] = 0;
                                    du[1] = 0;
                                    du[2] = 0;
                                    du[u] = w;

                                    dv[0] = 0;
                                    dv[1] = 0;
                                    dv[2] = 0;
                                    dv[v] = h;

                                    quad(x[0],                 x[1],                   x[2],
                                            x[0] + du[0],         x[1] + du[1],           x[2] + du[2],
                                            x[0] + du[0] + dv[0], x[1] + du[1] + dv[1],   x[2] + du[2] + dv[2],
                                            x[0] + dv[0],         x[1] + dv[1],           x[2] + dv[2],
                                            w,
                                            h,
                                            BlockRegistry.getBLock(mask[n]),
                                            backFace,
                                            d);
                                }

                                for(l = 0; l < h; ++l) {

                                    for(k = 0; k < w; ++k) { mask[n + k + l * dims[u]] = 0; }
                                }

                                i += w;
                                n += w;

                            } else {

                                i++;
                                n++;
                            }
                        }
                    }
                }
            }
        }
    }

    private static final int[] indexes1 = new int[] { 2,0,1, 1,3,2 };
    private static final int[] indexes2 = new int[]{ 2,3,1, 1,0,2 };

    private void quad(
            float bottomLeftX, float bottomLeftY, float bottomLeftZ,
            float topLeftX, float topLeftY, float topLeftZ,
            float topRightX, float topRightY, float topRightZ,
            float bottomRightX, float bottomRightY, float bottomRightZ,
            final int width,
            final int height,
            final Block voxel,
            final boolean backFace, int direction)
    {

        int [] indexes = backFace ? indexes1 : indexes2;

        positions.add(bottomLeftX);
        positions.add(bottomLeftY);
        positions.add(bottomLeftZ);

        positions.add(bottomRightX);
        positions.add(bottomRightY);
        positions.add(bottomRightZ);

        positions.add(topLeftX);
        positions.add(topLeftY);
        positions.add(topLeftZ);

        positions.add(topRightX);
        positions.add(topRightY);
        positions.add(topRightZ);

        for (int index : indexes) {
            indices.add(index + passes * 4);
        }

        // Texture re-orientation based on the direction
        if (direction == 2) {
            if (!backFace) {
                // 2
                uvs.add(0f);
                uvs.add((float) height);
                uvs.add((float) voxel.getNorthTexture());

                // 0
                uvs.add(0f);
                uvs.add(0f);
                uvs.add((float) voxel.getNorthTexture());

                // 3
                uvs.add((float) width);
                uvs.add((float) height);
                uvs.add((float) voxel.getNorthTexture());

                // 1
                uvs.add((float) width);
                uvs.add(0f);
                uvs.add((float) voxel.getNorthTexture());
            } else {
                // 3
                uvs.add((float) width);
                uvs.add((float) height);
                uvs.add((float) voxel.getSouthTexture());

                // 1
                uvs.add((float) width);
                uvs.add(0f);
                uvs.add((float) voxel.getSouthTexture());

                // 2
                uvs.add(0f);
                uvs.add((float) height);
                uvs.add((float) voxel.getSouthTexture());

                // 0
                uvs.add(0f);
                uvs.add(0f);
                uvs.add((float) voxel.getSouthTexture());
            }
        } else if (direction == 0) {
            if (backFace) {
                // 2
                uvs.add(0f);
                uvs.add((float) width);
                uvs.add((float) voxel.getWestTexture());

                // 3
                uvs.add((float) height);
                uvs.add((float) width);
                uvs.add((float) voxel.getWestTexture());
                // 0
                uvs.add(0f);
                uvs.add(0f);
                uvs.add((float) voxel.getWestTexture());

                // 1
                uvs.add((float) height);
                uvs.add(0f);
                uvs.add((float) voxel.getWestTexture());
            } else {
                // 3
                uvs.add((float) height);
                uvs.add((float) width);
                uvs.add((float) voxel.getEastTexture());

                // 2
                uvs.add(0f);
                uvs.add((float) width);
                uvs.add((float) voxel.getEastTexture());

                // 1
                uvs.add((float) height);
                uvs.add(0f);
                uvs.add((float) voxel.getEastTexture());

                // 0
                uvs.add(0f);
                uvs.add(0f);
                uvs.add((float) voxel.getEastTexture());
            }
        } else {
            if (!backFace) {
                // 0
                uvs.add(0f);
                uvs.add(0f);
                uvs.add((float) voxel.getTopTexture());

                // 1
                uvs.add((float) height);
                uvs.add(0f);
                uvs.add((float) voxel.getTopTexture());

                // 2
                uvs.add(0f);
                uvs.add((float) width);
                uvs.add((float) voxel.getTopTexture());

                // 3
                uvs.add((float) height);
                uvs.add((float) width);
                uvs.add((float) voxel.getTopTexture());
            } else {

                // 1
                uvs.add((float) height);
                uvs.add(0f);
                uvs.add((float) voxel.getBottomTexture());
                // 0
                uvs.add(0f);
                uvs.add(0f);
                uvs.add((float) voxel.getBottomTexture());

                // 3
                uvs.add((float) height);
                uvs.add((float) width);
                uvs.add((float) voxel.getBottomTexture());

                // 2
                uvs.add(0f);
                uvs.add((float) width);
                uvs.add((float) voxel.getBottomTexture());
            }
        }

        if (direction == 1) {
            for (int i = 0; i < 4; i++) {
                light.add(backFace ? 3f : 2f);
            }
        } else if (direction == 0) {
            for (int i = 0; i < 4; i++) {
                light.add(backFace ? 1f : 0f);
            }
        } else {
            for (int i = 0; i < 4; i++) {
                light.add(backFace ? 5f : 4f);
            }
        }

        passes++;
    }

}
