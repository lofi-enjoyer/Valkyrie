package me.lofienjoyer.nublada.engine.graphics.render;

import me.lofienjoyer.nublada.Nublada;
import me.lofienjoyer.nublada.engine.events.mesh.MeshGenerationEvent;
import me.lofienjoyer.nublada.engine.events.world.ChunkLoadEvent;
import me.lofienjoyer.nublada.engine.events.world.ChunkUpdateEvent;
import me.lofienjoyer.nublada.engine.graphics.camera.Camera;
import me.lofienjoyer.nublada.engine.graphics.mesh.MeshBundle;
import me.lofienjoyer.nublada.engine.graphics.shaders.SolidsShader;
import me.lofienjoyer.nublada.engine.graphics.shaders.TransparencyShader;
import me.lofienjoyer.nublada.engine.utils.Maths;
import me.lofienjoyer.nublada.engine.world.BlockRegistry;
import me.lofienjoyer.nublada.engine.world.Chunk;
import me.lofienjoyer.nublada.engine.world.World;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static org.lwjgl.opengl.GL45.*;

public class WorldRenderer {

    public static int VIEW_DISTANCE = 8;

    // TODO: 24/03/2022 Make the core count customizable
    private static final ScheduledExecutorService meshService =
            new ScheduledThreadPoolExecutor(4, r -> {
                Thread thread = new Thread(r, "Meshing Thread");
                thread.setDaemon(true);

                return thread;
            });

    private Matrix4f projectionMatrix;
    private final FrustumCullingTester tester;

    private final SolidsShader solidsShader;
    private final TransparencyShader transparencyShader;

    private final Map<Vector2i, MeshBundle> chunkMeshes;
    private final Map<Vector2i, Future<MeshBundle>> meshFutures;
    private final Map<Vector2i, MeshBundle> meshesToUpload;
    private final Set<Chunk> chunksToUpdate;

    public WorldRenderer() {
        this.projectionMatrix = new Matrix4f();
        this.solidsShader = new SolidsShader();
        this.transparencyShader = new TransparencyShader();

        this.tester = new FrustumCullingTester();

        this.chunkMeshes = new HashMap<>();
        this.meshFutures = new HashMap<>();
        this.meshesToUpload = new HashMap<>();
        this.chunksToUpdate = new HashSet<>();

        Nublada.EVENT_HANDLER.registerListener(ChunkLoadEvent.class, this::handleChunkLoading);
        Nublada.EVENT_HANDLER.registerListener(ChunkUpdateEvent.class, this::handleChunkUpdating);
        Nublada.EVENT_HANDLER.registerListener(MeshGenerationEvent.class, this::handleMeshGeneration);

        transparencyShader.start();
        transparencyShader.loadLeavesId(BlockRegistry.getBLock(6).getTopTexture());
        transparencyShader.loadWaterId(BlockRegistry.getBLock(7).getTopTexture());

        Nublada.LOG.info("World renderer has been setup");
    }

    public void render(World world, Camera camera) {

        var meshesToUploadIterator = meshesToUpload.entrySet().iterator();
        if (meshesToUploadIterator.hasNext()) {
            var currentMeshEntry = meshesToUploadIterator.next();
            currentMeshEntry.getValue().loadMeshToGpu();
            meshesToUploadIterator.remove();
        }

        chunksToUpdate.forEach(chunk -> {
            generateMesh(chunk, chunkMeshes.get(chunk.getPosition()));
        });
        chunksToUpdate.clear();

        /*
            Checks the chunk the player is in, and if it changed
            from the last frame it unloads the chunks that are not in view,
            sets to load the new chunks that entered the view distance and if
            new chunks are added for rendering sorts the list of chunks to
            render them from back to front
        */

//        updateChunksToRenderList(world, playerX, playerZ);

        // Get the block the camera is in (for in-water effects)
        int headBlock = world.getBlock(camera.getPosition());

        renderSolidMeshes(camera, headBlock);

        renderTransparentMeshes(camera, headBlock);

        // TODO: 05/02/2022 Make a proper crosshair
        // Renders a point in the middle of the screen
        glUseProgram(0);
        glPointSize(5);
        glBegin(GL_POINTS);
        glVertex2f(0, 0);
        glEnd();
        glPointSize(1);
    }

    // Renders the solid mesh for all chunks
    private void renderSolidMeshes(Camera camera, int headBlock) {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        glBindTexture(GL_TEXTURE_2D_ARRAY, BlockRegistry.TEXTURE_ARRAY_ID);

        // Renders the solid mesh for all chunks
        solidsShader.start();
        solidsShader.loadViewMatrix(camera);
        solidsShader.loadViewDistance(VIEW_DISTANCE * 32 - 32);
        solidsShader.setInWater(headBlock == 7);

        chunkMeshes.forEach((position, mesh) -> {
            if (!mesh.isLoaded() || !tester.isChunkInside(position, camera.getPosition().y))
                return;

            solidsShader.loadTransformationMatrix(Maths.createTransformationMatrix(position));

            glBindVertexArray(mesh.getSolidMeshes().getVaoId());
            glEnableVertexAttribArray(0);

            glDrawElements(GL_TRIANGLES, mesh.getSolidMeshes().getVertexCount(), GL_UNSIGNED_INT, 0);
        });
    }

    // Renders the transparent mesh for all chunks
    private void renderTransparentMeshes(Camera camera, int headBlock) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        transparencyShader.start();
        transparencyShader.loadViewMatrix(camera);
        transparencyShader.loadTime((float) GLFW.glfwGetTime());
        transparencyShader.loadViewDistance(VIEW_DISTANCE * 32 - 32);
        transparencyShader.setInWater(headBlock == 7);

        chunkMeshes.forEach((position, mesh) -> {
            if (!mesh.isLoaded() || !tester.isChunkInside(position, camera.getPosition().y))
                return;
            transparencyShader.loadTransformationMatrix(Maths.createTransformationMatrix(position));

            glBindVertexArray(mesh.getTransparentMeshes().getVaoId());
            glEnableVertexAttribArray(0);

            glDrawElements(GL_TRIANGLES, mesh.getTransparentMeshes().getVertexCount(), GL_UNSIGNED_INT, 0);
        });
        glDisable(GL_BLEND);
    }

    private void handleChunkLoading(ChunkLoadEvent event) {
        var meshBundle = new MeshBundle(event.getChunk());
        chunkMeshes.put(event.getChunk().getPosition(), meshBundle);
        generateMesh(event.getChunk(), meshBundle);
    }

    private void handleChunkUpdating(ChunkUpdateEvent event) {
        chunksToUpdate.add(event.getChunk());
    }

    private void handleMeshGeneration(MeshGenerationEvent event) {
        meshesToUpload.put(event.getPosition(), event.getMeshBundle());
    }

    /**
     * Queues a task to mesh the chunk
     */
    public void generateMesh(Chunk chunk, MeshBundle meshBundle) {
        if (!chunk.isLoaded())
            return;

        var meshFuture = meshFutures.get(chunk.getPosition());

        if (meshFuture != null) {
            meshFuture.cancel(true);
            meshFutures.remove(chunk.getPosition());
        }

        chunk.cacheNeighbors();
        meshFuture = meshService.submit(() -> {
            meshBundle.compute();
            Nublada.EVENT_HANDLER.process(new MeshGenerationEvent(chunk.getPosition(), meshBundle));
            return meshBundle;
        });

        meshFutures.put(chunk.getPosition(), meshFuture);
    }

    // Checks all loaded chunks, and unloads any that is outside the view distance
    private void updateChunksToRenderList(World world, int playerX, int playerZ) {
        var iterator = chunkMeshes.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();

            long distance = entry.getKey().distanceSquared(playerX, playerZ);
            if (distance > VIEW_DISTANCE * VIEW_DISTANCE) {
                iterator.remove();
            }
        }
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
    }

}
