package me.lofienjoyer.nublada.game;

import me.lofienjoyer.nublada.Nublada;
import me.lofienjoyer.nublada.engine.graphics.camera.Camera;
import me.lofienjoyer.nublada.engine.graphics.display.Window;
import me.lofienjoyer.nublada.engine.graphics.render.RaycastRenderer;
import me.lofienjoyer.nublada.engine.graphics.render.SkyboxRenderer;
import me.lofienjoyer.nublada.engine.graphics.render.WorldRenderer;
import me.lofienjoyer.nublada.engine.graphics.render.gui.SelectedBlockRenderer;
import me.lofienjoyer.nublada.engine.input.Input;
import me.lofienjoyer.nublada.engine.scene.IScene;
import me.lofienjoyer.nublada.engine.world.BlockRegistry;
import me.lofienjoyer.nublada.engine.world.Player;
import me.lofienjoyer.nublada.engine.world.World;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

public class WorldScene implements IScene {

    private Camera camera;
    private World world;
    private WorldRenderer worldRenderer;
    private SkyboxRenderer skyboxRenderer;
    private SelectedBlockRenderer selectedBlockRenderer;
    private RaycastRenderer raycastRenderer;
    private Player player;
    private Vector3f hitPosition;
    private Input input;
    private Window window;

    @Override
    public void init() {
        this.camera = new Camera();
        this.world = new World();
        this.worldRenderer = new WorldRenderer();
        this.skyboxRenderer = new SkyboxRenderer();
        this.selectedBlockRenderer = new SelectedBlockRenderer();
        this.raycastRenderer = new RaycastRenderer();

        this.player = new Player(world);

        this.input = Input.getInstance();
        this.window = Window.getInstance();

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
        camera.update(Window.id, delta);
        player.setRotation(new Vector3f(camera.getRotationX(), camera.getRotationY(), 0));
        player.update(1 / 20f);
        camera.setPosition(new Vector3f(player.getPosition().x, player.getPosition().y + 1.75f, player.getPosition().z));
        worldRenderer.updateFrustum(camera);

        skyboxRenderer.render(camera);

        worldRenderer.render(world, camera);

        if (hitPosition != null) {
            raycastRenderer.render(camera, hitPosition);
        }

        selectedBlockRenderer.render(selectedBlock);
    }

    @Override
    public void fixedUpdate() {
        world.update(1 / 20f, camera);

        hitPosition = world.rayCast(camera.getPosition(), camera.getDirection(), 10, false);

        if (hitPosition != null) {
            if (Input.isButtonJustPressed(0)) {
                world.setBlock(0, hitPosition);
            } else if (Input.isButtonJustPressed(1) && BlockRegistry.getBLock(selectedBlock) != null) {
                var blockToPlacePosition = world.rayCast(camera.getPosition(), camera.getDirection(), 10, true);
                world.setBlock(selectedBlock, blockToPlacePosition);
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
