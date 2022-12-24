package me.lofienjoyer.nublada.engine.graphics.render;

import me.lofienjoyer.nublada.Nublada;
import me.lofienjoyer.nublada.engine.graphics.camera.Camera;
import me.lofienjoyer.nublada.engine.graphics.shaders.SelectorShader;
import me.lofienjoyer.nublada.engine.graphics.shaders.SolidsShader;
import me.lofienjoyer.nublada.engine.graphics.shaders.TransparencyShader;
import me.lofienjoyer.nublada.engine.utils.Maths;
import me.lofienjoyer.nublada.engine.world.BlockRegistry;
import me.lofienjoyer.nublada.engine.world.Chunk;
import me.lofienjoyer.nublada.engine.world.World;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class WorldRenderer {

    public static int VIEW_DISTANCE = 8;

    private Matrix4f projectionMatrix;
    private final FrustumCullingTester tester;

    private final SolidsShader solidsShader;
    private final TransparencyShader transparencyShader;
    private final SelectorShader selectorShader;

    private final List<Chunk> chunksToRender = new ArrayList<>();
    private final Vector2i playerPosition;

    private final RaycastRenderer raycastRenderer;
    private final AtomicBoolean needsSorting;

    public WorldRenderer() {
        this.projectionMatrix = new Matrix4f();
        this.solidsShader = new SolidsShader();
        this.transparencyShader = new TransparencyShader();
        this.selectorShader = new SelectorShader();

        this.tester = new FrustumCullingTester();
        this.playerPosition = new Vector2i();

        this.raycastRenderer = new RaycastRenderer();

        this.needsSorting = new AtomicBoolean(true);

        transparencyShader.start();
        transparencyShader.loadLeavesId(BlockRegistry.getBLock(6).getTopTexture());
        transparencyShader.loadWaterId(BlockRegistry.getBLock(7).getTopTexture());

        Nublada.LOG.info("World renderer has been setup");
    }

    public void render(World world, Camera camera) {

        world.checkGeneratingChunks();

        int playerX = (int) Math.floor(camera.getPosition().x / (float) World.CHUNK_WIDTH);
        int playerZ = (int) Math.floor(camera.getPosition().z / (float) World.CHUNK_WIDTH);

        if (!playerPosition.equals(new Vector2i(playerX, playerZ)))
            needsSorting.set(true);

        playerPosition.x = playerX;
        playerPosition.y = playerZ;

        // Checks all the chunks within the view distance,
        // and loads those which are not loaded
        for(int x = -VIEW_DISTANCE; x <= VIEW_DISTANCE; x++){
            for(int z = -VIEW_DISTANCE; z <= VIEW_DISTANCE; z++){
                int chunkX = playerX + x;
                int chunkZ = playerZ + z;

                int distance = x * x + z * z;

                if(distance < VIEW_DISTANCE * VIEW_DISTANCE){
                    if (world.getChunk(chunkX, chunkZ) == null) {
                        world.addChunk(chunkX, chunkZ);
                        needsSorting.set(true);
                    }
                }

            }
        }

        List<Chunk> chunksToUnload = new ArrayList<>();

        if (needsSorting.get())
            chunksToRender.clear();

        // Checks all loaded chunks, and if any is outside the view area unloads it
        world.getChunks().forEach((position, chunk) -> {
            chunk.prepare();

            long distance = position.distanceSquared(playerX, playerZ);
            if (distance > VIEW_DISTANCE * VIEW_DISTANCE + 2) {
                chunksToUnload.add(chunk);
                return;
            }

            if (!needsSorting.get())
                return;

            chunksToRender.add(chunk);
        });

        chunksToUnload.forEach(chunk -> {
            chunk.onDestroy();
            world.getChunks().remove(chunk.getPosition());
        });

        int headBlock = world.getBlock(camera.getPosition());

        if (needsSorting.get()) {
            chunksToRender.sort(new SortByDistance());
            needsSorting.set(false);
        }

        GL30.glEnable(GL30.GL_DEPTH_TEST);
        GL30.glEnable(GL30.GL_CULL_FACE);
        GL30.glCullFace(GL30.GL_BACK);

        GL30.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, BlockRegistry.TEXTURE_ARRAY_ID);

        // Renders the solid mesh for all chunks
        solidsShader.start();
        solidsShader.loadViewMatrix(camera);
        solidsShader.loadViewDistance(VIEW_DISTANCE * 32);
        solidsShader.setInWater(headBlock == 7);

        chunksToRender.forEach(chunk -> {
            if (chunk.getModel() == null || !tester.isChunkInside(chunk, camera.getPosition().y))
                return;

            solidsShader.loadTransformationMatrix(Maths.createTransformationMatrix(chunk.getPosition()));

            GL30.glBindVertexArray(chunk.getModel().getSolidMeshes().getVaoId());
            GL30.glEnableVertexAttribArray(0);
            GL30.glEnableVertexAttribArray(1);
            GL30.glEnableVertexAttribArray(2);

            GL30.glDrawElements(GL30.GL_TRIANGLES, chunk.getModel().getSolidMeshes().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
        });

        // Renders the transparent mesh for all chunks
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

        transparencyShader.start();
        transparencyShader.loadViewMatrix(camera);
        transparencyShader.loadTime((float) GLFW.glfwGetTime());
        transparencyShader.loadViewDistance(VIEW_DISTANCE * 32);
        transparencyShader.setInWater(headBlock == 7);

        chunksToRender.forEach(chunk -> {
            if (chunk.getModel() == null || !tester.isChunkInside(chunk, camera.getPosition().y))
                return;
            transparencyShader.loadTransformationMatrix(Maths.createTransformationMatrix(chunk.getPosition()));

            GL30.glBindVertexArray(chunk.getModel().getTransparentMeshes().getVaoId());
            GL30.glEnableVertexAttribArray(0);
            GL30.glEnableVertexAttribArray(1);
            GL30.glEnableVertexAttribArray(2);

            GL30.glDrawElements(GL30.GL_TRIANGLES, chunk.getModel().getTransparentMeshes().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
        });
        GL30.glDisable(GL30.GL_BLEND);

        // Highlights the voxel the player is looking at
        selectorShader.start();
        selectorShader.loadViewMatrix(camera);
        selectorShader.loadTime((float) GLFW.glfwGetTime());

        // TODO: 24/12/2022 Make this async
        Vector3f hitPosition = world.rayCast(camera.getPosition(), camera.getDirection(), 10, false);
        if (hitPosition != null) {
            selectorShader.loadTransformationMatrix(Maths.createTransformationMatrix(hitPosition, 0));
            raycastRenderer.render();
        }

        // TODO: 05/02/2022 Make a proper crosshair
        // Renders a point in the middle of the screen
        GL30.glUseProgram(0);
        GL30.glPointSize(5);
        GL30.glBegin(GL30.GL_POINTS);
        GL30.glVertex2f(0, 0);
        GL30.glEnd();
        GL30.glPointSize(1);
    }

    public void updateFrustum(Camera camera) {
        tester.updateFrustum(projectionMatrix, Maths.createViewMatrix(camera));
    }

    /**
     * Updates the projection matrix based on the screen resolution
     * @param width Screen width
     * @param height Screen height
     */
    public void setupProjectionMatrix(int width, int height) {
        this.projectionMatrix = new Matrix4f();
        this.projectionMatrix.perspective(70, width / (float)height, 0.01f, 5000f);

        solidsShader.start();
        solidsShader.loadProjectionMatrix(projectionMatrix);
        transparencyShader.start();
        transparencyShader.loadProjectionMatrix(projectionMatrix);
        selectorShader.start();
        selectorShader.loadProjectionMatrix(projectionMatrix);
    }

    class SortByDistance implements Comparator<Chunk> {

        @Override
        public int compare(Chunk chunk1, Chunk chunk2) {
            return (int) Math.floor(chunk2.getPosition().distanceSquared(playerPosition.x, playerPosition.y) - chunk1.getPosition().distanceSquared(playerPosition.x, playerPosition.y));
        }

    }

}
