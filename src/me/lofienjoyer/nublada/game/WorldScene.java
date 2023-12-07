package me.lofienjoyer.nublada.game;

import me.lofienjoyer.nublada.Nublada;
import me.lofienjoyer.nublada.engine.graphics.camera.Camera;
import me.lofienjoyer.nublada.engine.graphics.display.Window;
import me.lofienjoyer.nublada.engine.graphics.render.RaycastRenderer;
import me.lofienjoyer.nublada.engine.graphics.render.SkyboxRenderer;
import me.lofienjoyer.nublada.engine.graphics.render.WorldRenderer;
import me.lofienjoyer.nublada.engine.graphics.render.gui.SelectedBlockRenderer;
import me.lofienjoyer.nublada.engine.scene.IScene;
import me.lofienjoyer.nublada.engine.world.BlockRegistry;
import me.lofienjoyer.nublada.engine.world.Player;
import me.lofienjoyer.nublada.engine.world.World;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class WorldScene implements IScene {

    private Camera camera;
    private World world;
    private WorldRenderer worldRenderer;
    private SkyboxRenderer skyboxRenderer;
    private SelectedBlockRenderer selectedBlockRenderer;
    private RaycastRenderer raycastRenderer;
    private Player player;

    @Override
    public void init() {
        this.camera = new Camera();
        this.world = new World();
        this.worldRenderer = new WorldRenderer();
        worldRenderer.setupProjectionMatrix(640, 360);
        this.skyboxRenderer = new SkyboxRenderer();
        skyboxRenderer.setupProjectionMatrix(640, 360);
        this.selectedBlockRenderer = new SelectedBlockRenderer();
        selectedBlockRenderer.setupProjectionMatrix(640, 360);
        this.raycastRenderer = new RaycastRenderer();
        raycastRenderer.setupProjectionMatrix(640, 360);

        this.player = new Player();
        player.camera = camera;
        player.world = world;

        GLFW.glfwSetScrollCallback(Nublada.WINDOW_ID, (id, xOffset, yOffset) -> {
            selectedBlock += yOffset;
            if (selectedBlock > BlockRegistry.getBlockCount() - 1) {
                selectedBlock = 0;
            } else if (selectedBlock < 0) {
                selectedBlock = BlockRegistry.getBlockCount() - 1;
            }
        });
    }

    int mouse = 0;
    int selectedBlock = 0;

    private static final int RADIUS = 2;

    @Override
    public void render(float delta) {
        player.update(delta);
        camera.update(Window.id, delta);
        world.update(delta);
        worldRenderer.updateFrustum(camera);

        skyboxRenderer.render(camera);

        worldRenderer.render(world, camera);

        Vector3f hitPosition = world.rayCast(camera.getPosition(), camera.getDirection(), 64, false);
        if (hitPosition != null) {
            raycastRenderer.render(camera, hitPosition);
        }

        selectedBlockRenderer.render(selectedBlock);

        if (GLFW.glfwGetMouseButton(Window.id, 0) != 0) {
            mouse = 1;
        } else if (GLFW.glfwGetMouseButton(Window.id, 1) != 0) {
            mouse = 2;
        } else {
            mouse = 0;
        }

        if (hitPosition != null && mouse != 0 && BlockRegistry.getBLock(selectedBlock) != null) {
            for (int x = -RADIUS; x <= RADIUS; x++) {
                for (int y = -RADIUS; y <= RADIUS; y++) {
                    for (int z = -RADIUS; z <= RADIUS; z++) {
                        world.setBlock(mouse == 1 ? 0 : selectedBlock, new Vector3f(hitPosition.x + x, hitPosition.y + y, hitPosition.z + z));
                    }
                }
            }
        }
    }

    @Override
    public void onResize(int width, int height) {
        worldRenderer.setupProjectionMatrix(width, height);
        skyboxRenderer.setupProjectionMatrix(width, height);
        selectedBlockRenderer.setupProjectionMatrix(width, height);
        raycastRenderer.setupProjectionMatrix(width, height);
    }

    @Override
    public void onClose() {

    }

    @Override
    public void dispose() {

    }

}
