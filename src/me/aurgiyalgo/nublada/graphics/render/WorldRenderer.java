package me.aurgiyalgo.nublada.graphics.render;

import me.aurgiyalgo.nublada.graphics.camera.Camera;
import me.aurgiyalgo.nublada.graphics.shaders.SelectorShader;
import me.aurgiyalgo.nublada.graphics.shaders.SolidsShader;
import me.aurgiyalgo.nublada.graphics.shaders.TransparencyShader;
import me.aurgiyalgo.nublada.utils.Maths;
import me.aurgiyalgo.nublada.world.BlockRegistry;
import me.aurgiyalgo.nublada.world.Chunk;
import me.aurgiyalgo.nublada.world.World;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static me.aurgiyalgo.nublada.world.World.CHUNK_WIDTH;

public class WorldRenderer {

    private static final int VIEW_DISTANCE = 4;

    private Matrix4f projectionMatrix;
    private final FrustumCullingTester tester;

    private final SolidsShader solidsShader;
    private final TransparencyShader transparencyShader;
    private final SelectorShader selectorShader;

    private final Vector2i playerPosition;

    private final RaycastRenderer raycastRenderer;

    public WorldRenderer() {
        this.projectionMatrix = new Matrix4f();
        this.solidsShader = new SolidsShader();
        this.transparencyShader = new TransparencyShader();
        this.selectorShader = new SelectorShader();

        this.tester = new FrustumCullingTester();
        this.playerPosition = new Vector2i();

        this.raycastRenderer = new RaycastRenderer();
    }

    public void render(World world, Camera camera) {

        world.checkGeneratingChunks();

        long timer = System.nanoTime();

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

        List<Chunk> chunksToRender = new ArrayList<>();
        List<Chunk> chunksToUnload = new ArrayList<>();

        world.getChunks().forEach((position, chunk) -> {
            chunk.prepare();

            long distance = position.distanceSquared(playerX, playerZ);
            if (distance > VIEW_DISTANCE * VIEW_DISTANCE + 2) {
                chunksToUnload.add(chunk);
                return;
            }

            if (chunk.getModel() == null)
                return;

            if (!tester.isChunkInside(chunk, camera.getPosition().y))
                return;

            chunksToRender.add(chunk);
        });

        chunksToUnload.forEach(chunk -> {
            chunk.onDestroy();
            world.getChunks().remove(chunk.getPosition());
        });

        playerPosition.x = playerX;
        playerPosition.y = playerZ;

        // TODO: 03/02/2022 Sort only when player moves between chunks
        chunksToRender.sort(new SortByDistance());

        GL30.glEnable(GL30.GL_DEPTH_TEST);
        GL30.glEnable(GL30.GL_CULL_FACE);
        GL30.glCullFace(GL30.GL_BACK);

        GL30.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, BlockRegistry.TEXTURE_ARRAY_ID);

        solidsShader.start();
        solidsShader.loadProjectionMatrix(projectionMatrix);
        solidsShader.loadViewMatrix(camera);
        solidsShader.loadViewDistance(VIEW_DISTANCE * 32);

        chunksToRender.forEach(chunk -> {
            solidsShader.loadTransformationMatrix(Maths.createTransformationMatrix(chunk.getPosition()));

            GL30.glBindVertexArray(chunk.getModel().getSolidMesh().getVao());
            GL30.glEnableVertexAttribArray(0);
            GL30.glEnableVertexAttribArray(1);
            GL30.glEnableVertexAttribArray(2);

            GL30.glDrawElements(GL30.GL_TRIANGLES, chunk.getModel().getSolidMesh().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
        });

        solidsShader.stop();

        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

        transparencyShader.start();
        transparencyShader.loadProjectionMatrix(projectionMatrix);
        transparencyShader.loadViewMatrix(camera);
        transparencyShader.loadTime((float) GLFW.glfwGetTime());
        transparencyShader.loadViewDistance(VIEW_DISTANCE * 32);

        chunksToRender.forEach(chunk -> {
            transparencyShader.loadTransformationMatrix(Maths.createTransformationMatrix(chunk.getPosition()));

            GL30.glBindVertexArray(chunk.getModel().getTransparentMesh().getVao());
            GL30.glEnableVertexAttribArray(0);
            GL30.glEnableVertexAttribArray(1);
            GL30.glEnableVertexAttribArray(2);

            GL30.glDrawElements(GL30.GL_TRIANGLES, chunk.getModel().getTransparentMesh().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
        });

        transparencyShader.stop();

        GL30.glDisable(GL30.GL_BLEND);

        GL30.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL30.glBindVertexArray(0);

        selectorShader.start();
        selectorShader.loadProjectionMatrix(projectionMatrix);
        selectorShader.loadViewMatrix(camera);

        Vector3f hitPosition = world.rayCast(camera.getPosition(), camera.getDirection(), 10, false);
        if (hitPosition != null) {
            selectorShader.loadTransformationMatrix(Maths.createTransformationMatrix(hitPosition, 0));
            raycastRenderer.render();
        }

        selectorShader.stop();

        System.out.println("World render: " + ((System.nanoTime() - timer) / 1000000f) + "ms (" + chunksToRender.size() + " chunks)");

        // TODO: 05/02/2022 Make a proper crosshair
        GL30.glPointSize(5);
        GL30.glBegin(GL30.GL_POINTS);
        GL30.glVertex2f(0, 0);
        GL30.glEnd();
        GL30.glPointSize(1);
    }

    public void updateFrustum(Camera camera) {
        tester.updateFrustum(projectionMatrix, Maths.createViewMatrix(camera));
    }

    public void setupProjectionMatrix(int width, int height) {
        this.projectionMatrix = new Matrix4f();
        this.projectionMatrix.perspective(70, width / (float)height, 0.01f, 5000f);
    }

    class SortByDistance implements Comparator<Chunk> {

        @Override
        public int compare(Chunk chunk1, Chunk chunk2) {
            return (int) (chunk2.getPosition().distanceSquared(playerPosition.x, playerPosition.y) - chunk1.getPosition().distanceSquared(playerPosition.x, playerPosition.y));
        }

    }

}
