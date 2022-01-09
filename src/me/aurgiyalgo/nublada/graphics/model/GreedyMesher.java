package me.aurgiyalgo.nublada.graphics.model;

import me.aurgiyalgo.nublada.Nublada;
import me.aurgiyalgo.nublada.world.Chunk;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static me.aurgiyalgo.nublada.world.World.CHUNK_WIDTH;
import static me.aurgiyalgo.nublada.world.World.CHUNK_HEIGHT;

public class GreedyMesher {

    private static final int[] dims = {CHUNK_WIDTH, CHUNK_HEIGHT, CHUNK_WIDTH};

    List<Vector3f> verticesList = new ArrayList<>();
    List<Integer> elementsList = new ArrayList<>();
    List<Vector3f> uvList = new ArrayList<>();

    public Model mesh(Chunk chunk) {

        //Sweep over 3-axes
        for (int d = 0; d < 3; d++)
        {
            int i = 0;
            int j = 0;
            int k = 0;
            int l = 0;
            int w = 0;
            int h = 0;

            int u = (d + 1) % 3;
            int v = (d + 2) % 3;

            int[] x = { 0, 0, 0 };
            int[] q = { 0, 0, 0 };
            int[] mask = new int[(dims[u] + 1) * (dims[v] + 1)];

            q[d] = 1;

            for (x[d] = -1; x[d] < dims[d];)
            {
                // Compute the mask
                int n = 0;
                for (x[v] = 0; x[v] < dims[v]; ++x[v])
                {
                    for (x[u] = 0; x[u] < dims[u]; ++x[u], ++n)
                    {
                        int vox1 = chunk.getBlock(x[0], x[1], x[2]);
                        int vox2 = chunk.getBlock(x[0] + q[0], x[1] + q[1], x[2] + q[2]);

                        int a = (0 <= x[d] ? vox1 : 0);
                        int b = (x[d] < dims[d] - 1 ? vox2 : 0);

                        if ((a != 0) == (b != 0))
                        {
                            mask[n] = 0;
                        }
                        else if ((a !=0))
                        {
                            mask[n] = a;
                        }
                        else
                        {
                            mask[n] = -b;
                        }


                        //    bool test = (0 <= x[d] ? (int)chunk.GetField(x[0], x[1], x[2]) : 0) !=
                        //       (x[d] < dims[d] - 1 ? (int)chunk.GetField(x[0] + q[0], x[1] + q[1], x[2] + q[2]) : 0);

                        //  mask[n++] = test ? 1 : 0;
                    }
                }

                // Increment x[d]
                ++x[d];

                // Generate mesh for mask using lexicographic ordering
                n = 0;
                for (j = 0; j < dims[v]; ++j)
                {
                    for (i = 0; i < dims[u];)
                    {
                        int c = mask[n];

                        if (c != 0)
                        {
                            // compute width
                            for (w = 1; mask[n + w] == c && (i + w) < dims[u]; ++w) { }

                            // compute height
                            boolean done = false;
                            for (h = 1; (j + h) < dims[v]; ++h)
                            {
                                for (k = 0; k < w; ++k)
                                {
                                    if (mask[n + k + h * dims[u]] != c)
                                    {
                                        done = true;
                                        break;
                                    }
                                }
                                if (done)
                                {
                                    break;
                                }
                            }

                            // add quad
                            x[u] = i;
                            x[v] = j;

                            int[] du = { 0, 0, 0 };
                            int[] dv = { 0, 0, 0 };

                            if (c > 0)
                            {
                                dv[v] = h;
                                du[u] = w;
                            }
                            else
                            {
                                du[v] = h;
                                dv[u] = w;
                            }

                            Vector3f v1 = new Vector3f(x[0], x[1], x[2]);
                            Vector3f v2 = new Vector3f(x[0] + du[0], x[1] + du[1], x[2] + du[2]);
                            Vector3f v3 = new Vector3f(x[0] + du[0] + dv[0], x[1] + du[1] + dv[1], x[2] + du[2] + dv[2]);
                            Vector3f v4 = new Vector3f(x[0] + dv[0], x[1] + dv[1], x[2] + dv[2]);

                            AddQuad(v1, v2, v3, v4, w, h, chunk.getBlock(x[0], x[1], x[2]));

                            for (l = 0; l < h; ++l)
                            {
                                for (k = 0; k < w; ++k)
                                {
                                    mask[n + k + l * dims[u]] = 0;
                                }
                            }
                            // increment counters
                            i += w;
                            n += w;
                        }
                        else
                        {
                            ++i;
                            ++n;
                        }
                    }
                }
            }
        }

//        AddQuad(new Vector3ff(2, 0, 0),
//                new Vector3ff(2, 1, 0),
//                new Vector3ff(2, 1, 1),
//                new Vector3ff(2, 0, 1),
//                verticesList, elementsList);


        float[] verticesArray = new float[verticesList.size() * 3];
        for (int i = 0; i < verticesList.size(); i++) {
            verticesArray[i * 3] = verticesList.get(i).x;
            verticesArray[i * 3 + 1] = verticesList.get(i).y;
            verticesArray[i * 3 + 2] = verticesList.get(i).z;
        }

        int[] indicesArray = new int[elementsList.size()];
        for (int i = 0; i < elementsList.size(); i++) {
            indicesArray[i] = elementsList.get(i);
        }

        float[] uvsArray = new float[uvList.size() * 3];
        for (int i = 0; i < uvList.size(); i++) {
            uvsArray[i * 3] = uvList.get(i).x;
            uvsArray[i * 3 + 1] = uvList.get(i).y;
            uvsArray[i * 3 + 2] = uvList.get(i).z;
        }

        return Nublada.modelLoader.loadToVAO(verticesArray, indicesArray, uvsArray);
    }

    private void AddQuad(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4, int width, int height, int voxel)
    {
//        int i = vertices.size();
//        vertices.add(v1);
//        vertices.add(v2);
//        vertices.add(v3);
//        vertices.add(v4);
//
//        elements.add(i + 0);
//        elements.add(i + 1);
//        elements.add(i + 2);
//        elements.add(i + 2);
//        elements.add(i + 3);
//        elements.add(i + 0);

        // Add the 4 vertices, and color for each vertex.
        verticesList.add(v1);
        verticesList.add(v2);
        verticesList.add(v3);
        verticesList.add(v4);

        int size = verticesList.size() - 4;

        elementsList.add(size + 0);
        elementsList.add(size + 1);
        elementsList.add(size + 2);

        elementsList.add(size + 0);
        elementsList.add(size + 2);
        elementsList.add(size + 3);

        uvList.add(new Vector3f(0, 0, voxel - 1));
        uvList.add(new Vector3f(height, 0, voxel - 1));
        uvList.add(new Vector3f(height, width, voxel - 1));
        uvList.add(new Vector3f(0, width, voxel - 1));

    }

}
