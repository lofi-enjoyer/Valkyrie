package me.lofienjoyer.valkyrie.engine.graphics.mesh;

public class BlockMeshBuilder {

    private static final float diagonal = (float) (Math.sqrt(0.5) / 2);
    private static final float diagonalM = 0.5f + diagonal;
    private static final float diagonalP = 0.5f - diagonal;

    public static BlockMesh buildFullBlockMesh(int southTexture, int eastTexture, int topTexture) {
        float[] positions = {
                0, 0, 0, compressData(0, 1, southTexture, 4, 0, 0),
                0, 1, 0, compressData(0, 0, southTexture, 4, 0, 0),
                0, 1, 1, compressData(1, 0, southTexture, 4, 0, 0),
                0, 0, 1, compressData(1, 1, southTexture, 4, 0, 0),

                1, 0, 1, compressData(0, 1, eastTexture, 0, 0, 0),
                1, 1, 1, compressData(0, 0, eastTexture, 0, 0, 0),
                0, 1, 1, compressData(1, 0, eastTexture, 0, 0, 0),
                0, 0, 1, compressData(1, 1, eastTexture, 0, 0, 0),

                0, 1, 0, compressData(1, 1, topTexture, 2, 0, 0),
                1, 1, 0, compressData(1, 0, topTexture, 2, 0, 0),
                1, 1, 1, compressData(0, 0, topTexture, 2, 0, 0),
                0, 1, 1, compressData(0, 1, topTexture, 2, 0, 0)
        };

        int[] indices = {
                2, 1, 0,
                0, 3, 2,

                4, 5, 6,
                6, 7, 4,

                10, 9, 8,
                8, 11, 10
        };

        return new BlockMesh(positions, indices);
    }

    public static BlockMesh buildXMesh(int texture) {
        float[] positions = {
                diagonalP, 0, diagonalM, compressData(0, 1, texture, 2, 1, 0),
                diagonalP, 1, diagonalM, compressData(0, 0, texture, 2, 1, 1),
                diagonalM, 1, diagonalP, compressData(1, 0, texture, 2, 1, 1),
                diagonalM, 0, diagonalP, compressData(1, 1, texture, 2, 1, 0),

                diagonalM, 0, diagonalM, compressData(0, 1, texture, 2, 1, 0),
                diagonalM, 1, diagonalM, compressData(0, 0, texture, 2, 1, 1),
                diagonalP, 1, diagonalP, compressData(1, 0, texture, 2, 1, 1),
                diagonalP, 0, diagonalP, compressData(1, 1, texture, 2, 1, 0),
        };

        int[] indices = {
                2, 1, 0,
                0, 3, 2,

                4, 5, 6,
                6, 7, 4
        };

        return new BlockMesh(positions, indices);
    }

    public static BlockMesh buildLeavesMesh(int texture) {
        float[] positions = {
                0, 0, 0, compressData(0, 1, texture, 4, 1, 1),
                1.125f, 1.125f, 0, compressData(0, 0, texture, 4, 1, 1),
                1.125f, 1.125f, 1.125f, compressData(1, 0, texture, 4, 1, 1),
                0, 0, 1.125f, compressData(1, 1, texture, 4, 1, 1),

                1.125f, 0, 1.125f, compressData(0, 1, texture, 0, 1, 1),
                1.125f, 1.125f, 0, compressData(0, 0, texture, 0, 1, 1),
                0, 1.125f, 0, compressData(1, 0, texture, 0, 1, 1),
                0, 0, 1.125f, compressData(1, 1, texture, 0, 1, 1),

                0.625f, 1.125f, 0, compressData(1, 1, texture, 4, 1, 1),
                0.625f, 0, 0, compressData(1, 0, texture, 4, 1, 1),
                0.625f, 0, 1.125f, compressData(0, 0, texture, 4, 1, 1),
                0.625f, 1.125f, 1.125f, compressData(0, 1, texture, 4, 1, 1),

                1.125f, 0, 0, compressData(0, 1, texture, 1, 1, 1),
                0, 1.125f, 0, compressData(0, 0, texture, 1, 1, 1),
                0, 1.125f, 1.125f, compressData(1, 0, texture, 1, 1, 1),
                1.125f, 0, 1.125f, compressData(1, 1, texture, 1, 1, 1),

                1.125f, 0, 0, compressData(0, 1, texture, 5, 1, 1),
                1.125f, 1.125f, 1.125f, compressData(0, 0, texture, 5, 1, 1),
                0, 1.125f, 1.125f, compressData(1, 0, texture, 5, 1, 1),
                0, 0, 0, compressData(1, 1, texture, 5, 1, 1),

                0, 0, 0.625f, compressData(1, 1, texture, 5, 1, 1),
                0, 1.125f, 0.625f, compressData(1, 0, texture, 5, 1, 1),
                1.125f, 1.125f, 0.625f, compressData(0, 0, texture, 5, 1, 1),
                1.125f, 0, 0.625f, compressData(0, 1, texture, 5, 1, 1)
        };

        int[] indices = {
                2, 1, 0,
                0, 3, 2,

                4, 5, 6,
                6, 7, 4,

                10, 9, 8,
                8, 11, 10,

                14, 13, 12,
                12, 15, 14,

                16, 17, 18,
                18, 19, 16,

                22, 21, 20,
                20, 23, 22
        };

        return new BlockMesh(positions, indices);
    }

    private static float compressData(int xUv, int yUv, int texture, int normal, int cull, int wave) {
        return (float)(xUv | yUv << 1 | normal << 2 | cull << 5 | wave << 6 | texture << 7);
    }

}
