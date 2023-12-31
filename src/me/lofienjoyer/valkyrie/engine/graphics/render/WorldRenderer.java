package me.lofienjoyer.valkyrie.engine.graphics.render;

import me.lofienjoyer.valkyrie.Valkyrie;
import me.lofienjoyer.valkyrie.engine.config.Config;
import me.lofienjoyer.valkyrie.engine.events.mesh.MeshGenerationEvent;
import me.lofienjoyer.valkyrie.engine.events.world.ChunkLoadEvent;
import me.lofienjoyer.valkyrie.engine.events.world.ChunkUnloadEvent;
import me.lofienjoyer.valkyrie.engine.events.world.ChunkUpdateEvent;
import me.lofienjoyer.valkyrie.engine.graphics.camera.Camera;
import me.lofienjoyer.valkyrie.engine.graphics.mesh.MeshBundle;
import me.lofienjoyer.valkyrie.engine.graphics.mesh.QuadMesh;
import me.lofienjoyer.valkyrie.engine.graphics.shaders.Shader;
import me.lofienjoyer.valkyrie.engine.graphics.shaders.SolidsShader;
import me.lofienjoyer.valkyrie.engine.graphics.shaders.TransparencyShader;
import me.lofienjoyer.valkyrie.engine.resources.ResourceLoader;
import me.lofienjoyer.valkyrie.engine.utils.Maths;
import me.lofienjoyer.valkyrie.engine.world.BlockRegistry;
import me.lofienjoyer.valkyrie.engine.world.Chunk;
import me.lofienjoyer.valkyrie.engine.world.ChunkState;
import me.lofienjoyer.valkyrie.engine.world.World;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.concurrent.Future;

import static org.lwjgl.opengl.GL45.*;

public class WorldRenderer {

    public static int VIEW_DISTANCE = 8;

    private Matrix4f projectionMatrix;
    private final FrustumCullingTester tester;

    private final SolidsShader solidsShader;
    private final TransparencyShader transparencyShader;

    private final Map<Vector2i, MeshBundle> chunkMeshes;
    private final Map<Vector3i, Future<MeshBundle>> meshFutures;
    private final Map<Vector3i, MeshBundle> meshesToUpload;
    private final Map<Vector3i, Chunk> chunksToUpdate;

    public WorldRenderer() {
        this.projectionMatrix = new Matrix4f();
        this.solidsShader = new SolidsShader();
        this.transparencyShader = new TransparencyShader();

        this.tester = new FrustumCullingTester();

        this.chunkMeshes = new HashMap<>();
        this.meshFutures = new HashMap<>();
        this.meshesToUpload = new HashMap<>();
        this.chunksToUpdate = new HashMap<>();

        Valkyrie.EVENT_HANDLER.registerListener(ChunkLoadEvent.class, this::handleChunkLoading);
        Valkyrie.EVENT_HANDLER.registerListener(ChunkUpdateEvent.class, this::handleChunkUpdating);
        Valkyrie.EVENT_HANDLER.registerListener(ChunkUnloadEvent.class, this::handleChunkUnloading);
        Valkyrie.EVENT_HANDLER.registerListener(MeshGenerationEvent.class, this::handleMeshGeneration);

        var config = Config.getInstance();
        VIEW_DISTANCE = config.get("view_distance", Integer.class);

        transparencyShader.start();
        transparencyShader.loadLeavesId(BlockRegistry.getBlock(6).getTopTexture());
        transparencyShader.loadWaterId(BlockRegistry.getBlock(7).getTopTexture());

        Valkyrie.LOG.info("World renderer has been setup");
    }

    public void render(World world, Camera camera) {

        var meshesToUploadIterator = meshesToUpload.entrySet().iterator();
        while (meshesToUploadIterator.hasNext()) {
            var currentMeshEntry = meshesToUploadIterator.next();
            meshesToUploadIterator.remove();

            if (currentMeshEntry.getValue().loadMeshToGpu(currentMeshEntry.getKey().y))
                break;
        }

        chunksToUpdate.forEach((position, chunk) -> {
            generateMesh(chunk, chunkMeshes.get(chunk.getPosition()), position.y);
        });
        chunksToUpdate.clear();

        // Get the block the camera is in (for in-water effects)
        int headBlock = world.getBlock(camera.getPosition());

        glBindTexture(GL_TEXTURE_2D, BlockRegistry.TILESET_ID);

        renderSolidMeshes(camera, headBlock);

        renderTransparentMeshes(camera, headBlock);
    }

    // Renders the solid mesh for all chunks
    private void renderSolidMeshes(Camera camera, int headBlock) {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        // Renders the solid mesh for all chunks
        solidsShader.start();
        solidsShader.loadViewMatrix(camera);
        solidsShader.loadViewDistance(VIEW_DISTANCE * 32 - 32);
        solidsShader.setInWater(headBlock == 7);

        chunkMeshes.forEach((position, mesh) -> {
            if (!mesh.isLoaded() || !tester.isChunkInside(position, camera.getPosition().y))
                return;

            for (int y = 0; y < World.CHUNK_HEIGHT / World.CHUNK_SECTION_HEIGHT; y++) {
                solidsShader.loadTransformationMatrix(Maths.createTransformationMatrix(new Vector3i(position.x, y, position.y)));

                glBindVertexArray(mesh.getSolidMeshes(y).getVaoId());
                glEnableVertexAttribArray(0);

                glDrawElements(GL_TRIANGLES, mesh.getSolidMeshes(y).getVertexCount(), GL_UNSIGNED_INT, 0);
            }
        });
    }

    // Renders the transparent mesh for all chunks
    private void renderTransparentMeshes(Camera camera, int headBlock) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_CULL_FACE);

        transparencyShader.start();
        transparencyShader.loadViewMatrix(camera);
        transparencyShader.loadTime((float) GLFW.glfwGetTime());
        transparencyShader.loadViewDistance(VIEW_DISTANCE * 32 - 32);
        transparencyShader.setInWater(headBlock == 7);

        chunkMeshes.forEach((position, mesh) -> {
            if (!mesh.isLoaded() || !tester.isChunkInside(position, camera.getPosition().y))
                return;

            for (int y = 0; y < World.CHUNK_HEIGHT / World.CHUNK_SECTION_HEIGHT; y++) {
                transparencyShader.loadTransformationMatrix(Maths.createTransformationMatrix(new Vector3i(position.x, y, position.y)));

                glBindVertexArray(mesh.getTransparentMeshes(y).getVaoId());
                glEnableVertexAttribArray(0);

                glDrawElements(GL_TRIANGLES, mesh.getTransparentMeshes(y).getVertexCount(), GL_UNSIGNED_INT, 0);
            }
        });
        glDisable(GL_BLEND);
    }

    private void handleChunkLoading(ChunkLoadEvent event) {
        var meshBundle = new MeshBundle(event.getChunk());
        chunkMeshes.put(event.getChunk().getPosition(), meshBundle);
        for (int i = 0; i < 8; i++) {
            chunksToUpdate.put(new Vector3i(event.getChunk().getPosition().x, i, event.getChunk().getPosition().y), event.getChunk());
        }
    }

    private void handleChunkUpdating(ChunkUpdateEvent event) {
        if (event.getChunk() == null)
            return;

        chunksToUpdate.put(new Vector3i(event.getChunk().getPosition().x, event.getUpdatePosition().y / World.CHUNK_SECTION_HEIGHT, event.getChunk().getPosition().y), event.getChunk());
        meshesToUpload.remove(new Vector3i(event.getChunk().getPosition().x, event.getUpdatePosition().y / World.CHUNK_SECTION_HEIGHT, event.getChunk().getPosition().y));
    }

    private void handleMeshGeneration(MeshGenerationEvent event) {
        meshesToUpload.put(event.getPosition(), event.getMeshBundle());
    }

    private void handleChunkUnloading(ChunkUnloadEvent event) {
        for (int i = 0; i < 8; i++) {
            var meshPosition = new Vector3i(event.getChunk().getPosition().x, i, event.getChunk().getPosition().y);
            var meshFuture = meshFutures.get(meshPosition);
            if (meshFuture != null) {
                meshFuture.cancel(true);
                meshFutures.remove(meshPosition);
            }
        }

        chunkMeshes.remove(event.getChunk().getPosition());
    }

    public void generateMesh(Chunk chunk, MeshBundle meshBundle, int section) {
        if (chunk.getState() == ChunkState.UNLOADED)
            return;

        var position = chunk.getPosition();
        var meshPosition = new Vector3i(position.x, section, position.y);
        var meshFuture = meshFutures.get(meshPosition);

        if (meshFuture != null) {
            meshFuture.cancel(true);
            meshFutures.remove(meshPosition);
        }

        chunk.cacheNeighbors();
        meshFuture = Valkyrie.getMeshingService().submit(() -> {
            meshBundle.compute(section);
            Valkyrie.EVENT_HANDLER.process(new MeshGenerationEvent(meshPosition, meshBundle));
            return meshBundle;
        });

        meshFutures.put(meshPosition, meshFuture);
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
        this.projectionMatrix.perspective(Valkyrie.FOV, width / (float)height, 0.01f, 5000f);

        solidsShader.start();
        solidsShader.loadProjectionMatrix(projectionMatrix);
        transparencyShader.start();
        transparencyShader.loadProjectionMatrix(projectionMatrix);
    }

}
