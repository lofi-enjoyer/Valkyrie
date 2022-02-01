package me.aurgiyalgo.nublada.graphics.render;

import me.aurgiyalgo.nublada.graphics.camera.Camera;
import me.aurgiyalgo.nublada.graphics.shaders.WorldShader;
import me.aurgiyalgo.nublada.utils.Maths;
import me.aurgiyalgo.nublada.world.BlockRegistry;
import me.aurgiyalgo.nublada.world.Chunk;
import me.aurgiyalgo.nublada.world.World;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static me.aurgiyalgo.nublada.world.World.CHUNK_WIDTH;

public class WorldRenderer {

    private static final int VIEW_DISTANCE = 4;

    private Matrix4f projectionMatrix;
    private final FrustumCullingTester tester;
    private final WorldShader shader;

    public WorldRenderer() {
        this.projectionMatrix = new Matrix4f();
        this.shader = new WorldShader();

        this.tester = new FrustumCullingTester();
    }

    public void render(World world, Camera camera) {

        world.checkGeneratingChunks();

        GL30.glEnable(GL30.GL_DEPTH_TEST);
        GL30.glEnable(GL30.GL_CULL_FACE);
        GL30.glCullFace(GL30.GL_BACK);

        GL30.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, BlockRegistry.TEXTURE_ARRAY_ID);

        long timer = System.nanoTime();
        AtomicInteger counter = new AtomicInteger();

        int playerX = (int) Math.floor(camera.getPosition().x / (float) CHUNK_WIDTH);
        int playerZ = (int) Math.floor(camera.getPosition().z / (float) CHUNK_WIDTH);

        for(int x = -VIEW_DISTANCE; x <= VIEW_DISTANCE; x++){
            for(int z = -VIEW_DISTANCE; z <= VIEW_DISTANCE; z++){
                int chunkX = playerX + x;
                int chunkZ = playerZ + z;

                int distance = x * x + z * z;

                if(distance < VIEW_DISTANCE * VIEW_DISTANCE){
                    if (world.getChunk(chunkX, chunkZ) == null) {
                        world.addChunk(chunkX, chunkZ);
                    }
                }

            }
        }

        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.loadViewMatrix(camera);

        List<Chunk> chunksToUnload = new ArrayList<>();

        world.getChunks().forEach((position, chunk) -> {

            if (chunk.getPosition().distanceSquared(playerX, playerZ) > VIEW_DISTANCE * VIEW_DISTANCE + 2) {
                // TODO: 28/01/2022 Unload properly (gpu data)
                chunksToUnload.add(chunk);
                return;
            }
            chunk.prepare();
            if (chunk.getModel() == null) return;
            if (!tester.isChunkInside(chunk, camera.getPosition().y)) return;
            counter.incrementAndGet();
            GL30.glBindVertexArray(chunk.getModel().getVao());
            GL30.glEnableVertexAttribArray(0);
            GL30.glEnableVertexAttribArray(1);
            GL30.glEnableVertexAttribArray(2);
//            shader.loadTranslation(position);
            shader.loadTransformationMatrix(Maths.createTransformationMatrix(position));
            GL30.glDrawElements(GL30.GL_TRIANGLES, chunk.getModel().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
        });
        System.out.println("World render: " + ((System.nanoTime() - timer) / 1000000f) + "ms (" + counter.get() + " chunks)");

        shader.stop();
        GL30.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL30.glBindVertexArray(0);

        chunksToUnload.forEach(chunk -> {
            chunk.onDestroy();
            world.getChunks().remove(chunk.getPosition());
        });

    }

    public void updateFrustum(Camera camera) {
        tester.updateFrustum(projectionMatrix, Maths.createViewMatrix(camera));
    }

    public void setupProjectionMatrix(int width, int height) {
        this.projectionMatrix = new Matrix4f();
        this.projectionMatrix.perspective(70, width / (float)height, 0.01f, 5000f);
    }

}
