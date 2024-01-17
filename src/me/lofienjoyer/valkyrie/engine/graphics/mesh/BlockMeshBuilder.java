package me.lofienjoyer.valkyrie.engine.graphics.mesh;

public class BlockMeshBuilder {

    private static final float diagonal = (float) (Math.sqrt(0.5) / 2);
    private static final float diagonalM = 0.5f + diagonal;
    private static final float diagonalP = 0.5f - diagonal;

    public static BlockMesh buildFullBlockMesh(int southTexture, int eastTexture, int topTexture) {
        float[] positions = {
                0, 0, 0,
                0, 1, 0,
                0, 1, 1,
                0, 0, 1,

                1, 0, 1,
                1, 1, 1,
                0, 1, 1,
                0, 0, 1,

                0, 1, 0,
                1, 1, 0,
                1, 1, 1,
                0, 1, 1
        };

        float[] uvs = {
                0, 1, southTexture,
                0, 0, southTexture,
                1, 0, southTexture,
                1, 1, southTexture,

                0, 1, eastTexture,
                0, 0, eastTexture,
                1, 0, eastTexture,
                1, 1, eastTexture,

                1, 1, topTexture,
                1, 0, topTexture,
                0, 0, topTexture,
                0, 1, topTexture
        };

        int[] indices = {
                2, 1, 0,
                0, 3, 2,

                4, 5, 6,
                6, 7, 4,

                10, 9, 8,
                8, 11, 10
        };

        return new BlockMesh(positions, indices, uvs);
    }

    public static BlockMesh buildXMesh(int texture) {
        float[] positions = {
                diagonalP, 0, diagonalM,
                diagonalP, 1, diagonalM,
                diagonalM, 1, diagonalP,
                diagonalM, 0, diagonalP,

                diagonalM, 0, diagonalM,
                diagonalM, 1, diagonalM,
                diagonalP, 1, diagonalP,
                diagonalP, 0, diagonalP
        };

        float[] uvs = {
                0, 1, texture,
                0, 0, texture,
                1, 0, texture,
                1, 1, texture,

                0, 1, texture,
                0, 0, texture,
                1, 0, texture,
                1, 1, texture
        };

        int[] indices = {
                2, 1, 0,
                0, 3, 2,

                4, 5, 6,
                6, 7, 4
        };

        return new BlockMesh(positions, indices, uvs);
    }

}
