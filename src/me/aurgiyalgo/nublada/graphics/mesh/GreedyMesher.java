package me.aurgiyalgo.nublada.graphics.mesh;

import me.aurgiyalgo.nublada.Nublada;
import me.aurgiyalgo.nublada.world.Chunk;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static me.aurgiyalgo.nublada.world.World.CHUNK_WIDTH;
import static me.aurgiyalgo.nublada.world.World.CHUNK_HEIGHT;

/**
 * Based on roboleary's algorithm, but improved to support non-cubic chunks
 * @see <a href="https://github.com/roboleary/GreedyMesh">GreedyMesh</a>
 */
public class GreedyMesher {

    private static final int[] dims = { CHUNK_WIDTH, CHUNK_HEIGHT, CHUNK_WIDTH };

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
    }

    public void compute() {
        this.positions = new ArrayList<>();
        this.indices = new ArrayList<>();
        this.uvs = new ArrayList<>();
        this.light = new ArrayList<>();

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
    }

    public Mesh loadMeshToGpu() {
        Mesh mesh = Nublada.loader.loadToVAO(positionsArray, indicesArray, uvsArray, lightArray);
        positionsArray = null;
        indicesArray = null;
        uvsArray = null;
        lightArray = null;
        return mesh;
    }

    void computeMesh() {
        /*
         * These are just working variables for the algorithm - almost all taken
         * directly from Mikola Lysenko's javascript implementation.
         */
        int i, j, k, l, w, h, u, v, n, side = 0;

        final int[] x = new int []{0,0,0};
        final int[] q = new int []{0,0,0};
        final int[] du = new int[]{0,0,0};
        final int[] dv = new int[]{0,0,0};

        /*
         * We create a mask - this will contain the groups of matching voxel faces
         * as we proceed through the chunk in 6 directions - once for each face.
         */
        int[] mask;

        /*
         * These are just working variables to hold two faces during comparison.
         */
        int voxelFace, voxelFace1;

        /**
         * We start with the lesser-spotted boolean for-loop (also known as the old flippy floppy).
         *
         * The variable backFace will be TRUE on the first iteration and FALSE on the second - this allows
         * us to track which direction the indices should run during creation of the quad.
         *
         * This loop runs twice, and the inner loop 3 times - totally 6 iterations - one for each
         * voxel face.
         */
        for (boolean backFace = true, b = false; b != backFace; backFace = backFace && b, b = !b) {

            /*
             * We sweep over the 3 dimensions - most of what follows is well described by Mikola Lysenko
             * in his post - and is ported from his Javascript implementation.  Where this implementation
             * diverges, I've added commentary.
             */
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

                /*
                 * Here we're keeping track of the side that we're meshing.
                 */
                if (d == 0)      { side = backFace ? WEST   : EAST;  }
                else if (d == 1) { side = backFace ? BOTTOM : TOP;   }
                else { side = backFace ? SOUTH  : NORTH; }

                /*
                 * We move through the dimension from front to back
                 */
                for(x[d] = -1; x[d] < dims[d];) {

                    /*
                     * -------------------------------------------------------------------
                     *   We compute the mask
                     * -------------------------------------------------------------------
                     */
                    n = 0;

                    for(x[v] = 0; x[v] < dims[v]; x[v]++) {

                        for(x[u] = 0; x[u] < dims[u]; x[u]++) {

                            /*
                             * Here we retrieve two voxel faces for comparison.
                             */
                            voxelFace  = (x[d] >= 0 )             ? chunk.getBlock(x[0], x[1], x[2])                      : 0;
                            voxelFace1 = (x[d] < dims[d] - 1) ? chunk.getBlock(x[0] + q[0], x[1] + q[1], x[2] + q[2]) : 0;

                            /*
                             * Note that we're using the equals function in the voxel face class here, which lets the faces
                             * be compared based on any number of attributes.
                             *
                             * Also, we choose the face to add to the mask depending on whether we're moving through on a backface or not.
                             */
                            mask[n++] = ((voxelFace != 0 && voxelFace1 != 0))
                                    ? 0
                                    : backFace ? voxelFace1 : voxelFace;
                        }
                    }

                    x[d]++;

                    /*
                     * Now we generate the mesh for the mask
                     */
                    n = 0;

                    for(j = 0; j < dims[v]; j++) {

                        for(i = 0; i < dims[u];) {

                            if(mask[n] != 0) {

                                /*
                                 * We compute the width
                                 */
                                for(w = 1; i + w < dims[u] && mask[n + w] != 0 && mask[n + w] == mask[n]; w++) {}

                                /*
                                 * Then we compute height
                                 */
                                boolean done = false;

                                for(h = 1; j + h < dims[v]; h++) {

                                    for(k = 0; k < w; k++) {

                                        if(mask[n + k + h * dims[u]] == 0 || mask[n + k + h * dims[u]] != mask[n]) { done = true; break; }
                                    }

                                    if(done) { break; }
                                }

                                /*
                                 * Here we check the "transparent" attribute in the VoxelFace class to ensure that we don't mesh
                                 * any culled faces.
                                 */
                                if (mask[n] != 0) {
                                    /*
                                     * Add quad
                                     */
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

                                    /*
                                     * And here we call the quad function in order to render a merged quad in the scene.
                                     *
                                     * We pass mask[n] to the function, which is an instance of the VoxelFace class containing
                                     * all the attributes of the face - which allows for variables to be passed to shaders - for
                                     * example lighting values used to create ambient occlusion.
                                     */
                                    quad(new Vector3f(x[0],                 x[1],                   x[2]),
                                            new Vector3f(x[0] + du[0],         x[1] + du[1],           x[2] + du[2]),
                                            new Vector3f(x[0] + du[0] + dv[0], x[1] + du[1] + dv[1],   x[2] + du[2] + dv[2]),
                                            new Vector3f(x[0] + dv[0],         x[1] + dv[1],           x[2] + dv[2]),
                                            w,
                                            h,
                                            mask[n],
                                            backFace,
                                            d);
                                }

                                /*
                                 * We zero out the mask
                                 */
                                for(l = 0; l < h; ++l) {

                                    for(k = 0; k < w; ++k) { mask[n + k + l * dims[u]] = 0; }
                                }

                                /*
                                 * And then finally increment the counters and continue
                                 */
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

    private void quad(final Vector3f bottomLeft,
              final Vector3f topLeft,
              final Vector3f topRight,
              final Vector3f bottomRight,
              final int width,
              final int height,
              final int voxel,
              final boolean backFace, int direction) {

        final Vector3f [] vertices = new Vector3f[4];

        vertices[2] = topLeft;
        vertices[3] = topRight;
        vertices[0] = bottomLeft;
        vertices[1] = bottomRight;

        int [] indexes = backFace ? new int[] { 2,0,1, 1,3,2 } : new int[]{ 2,3,1, 1,0,2 };

        if (direction == 0) {
            indexes = backFace ? new int[] { 2,0,1, 1,3,2 } : new int[]{ 2,3,1, 1,0,2 };
        }

        for (int i = 0; i < 4; i++) {
            positions.add(vertices[i].x);
            positions.add(vertices[i].y);
            positions.add(vertices[i].z);
        }

        for (int index : indexes) {
            indices.add(index + passes * 4);
        }

        // Texture re-orientation based on the direction
        if (direction == 2) {
            if (!backFace) {
                // 2
                uvs.add(0f);
                uvs.add((float) height);
                uvs.add((float) voxel - 1);

                // 0
                uvs.add(0f);
                uvs.add(0f);
                uvs.add((float) voxel - 1);

                // 3
                uvs.add((float) width);
                uvs.add((float) height);
                uvs.add((float) voxel - 1);

                // 1
                uvs.add((float) width);
                uvs.add(0f);
                uvs.add((float) voxel - 1);
            } else {
                // 3
                uvs.add((float) width);
                uvs.add((float) height);
                uvs.add((float) voxel - 1);

                // 1
                uvs.add((float) width);
                uvs.add(0f);
                uvs.add((float) voxel - 1);

                // 2
                uvs.add(0f);
                uvs.add((float) height);
                uvs.add((float) voxel - 1);

                // 0
                uvs.add(0f);
                uvs.add(0f);
                uvs.add((float) voxel - 1);
            }
        } else if (direction == 0) {
            if (backFace) {
                // 2
                uvs.add(0f);
                uvs.add((float) width);
                uvs.add((float) voxel - 1);

                // 3
                uvs.add((float) height);
                uvs.add((float) width);
                uvs.add((float) voxel - 1);
                // 0
                uvs.add(0f);
                uvs.add(0f);
                uvs.add((float) voxel - 1);

                // 1
                uvs.add((float) height);
                uvs.add(0f);
                uvs.add((float) voxel - 1);
            } else {
                // 3
                uvs.add((float) height);
                uvs.add((float) width);
                uvs.add((float) voxel - 1);

                // 2
                uvs.add(0f);
                uvs.add((float) width);
                uvs.add((float) voxel - 1);

                // 1
                uvs.add((float) height);
                uvs.add(0f);
                uvs.add((float) voxel - 1);

                // 0
                uvs.add(0f);
                uvs.add(0f);
                uvs.add((float) voxel - 1);
            }
        } else {
            // 0
            uvs.add(0f);
            uvs.add(0f);
            uvs.add((float) voxel - 1);

            // 1
            uvs.add((float) height);
            uvs.add(0f);
            uvs.add((float) voxel - 1);

            // 2
            uvs.add(0f);
            uvs.add((float) width);
            uvs.add((float) voxel - 1);

            // 3
            uvs.add((float) height);
            uvs.add((float) width);
            uvs.add((float) voxel - 1);
        }

        if (direction == 1) {
            for (int i = 0; i < 4; i++) {
                light.add(backFace ? 0.4f : 1f);
            }
        } else {
            for (int i = 0; i < 4; i++) {
                light.add(0.7f);
            }
        }

        passes++;
    }

}
