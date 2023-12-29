package me.lofienjoyer.valkyrie.game;

import me.lofienjoyer.valkyrie.Valkyrie;
import me.lofienjoyer.valkyrie.engine.graphics.camera.Camera;
import me.lofienjoyer.valkyrie.engine.graphics.display.Window;
import me.lofienjoyer.valkyrie.engine.graphics.font.ValkyrieFont;
import me.lofienjoyer.valkyrie.engine.graphics.render.FontRenderer;
import me.lofienjoyer.valkyrie.engine.graphics.render.RaycastRenderer;
import me.lofienjoyer.valkyrie.engine.graphics.render.SkyboxRenderer;
import me.lofienjoyer.valkyrie.engine.graphics.render.WorldRenderer;
import me.lofienjoyer.valkyrie.engine.graphics.render.gui.SelectedBlockRenderer;
import me.lofienjoyer.valkyrie.engine.input.Input;
import me.lofienjoyer.valkyrie.engine.scene.IScene;
import me.lofienjoyer.valkyrie.engine.world.BlockRegistry;
import me.lofienjoyer.valkyrie.engine.world.Player;
import me.lofienjoyer.valkyrie.engine.world.World;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL11.*;

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
    private FontRenderer fontRenderer;
    private String gpuInfo;

    @Override
    public void init() {
        this.camera = new Camera();
        this.world = new World();
        this.worldRenderer = new WorldRenderer();
        this.skyboxRenderer = new SkyboxRenderer();
        this.selectedBlockRenderer = new SelectedBlockRenderer();
        this.raycastRenderer = new RaycastRenderer();
        var font = new ValkyrieFont("res/fonts/Silkscreen-Regular.ttf", 16);
        this.fontRenderer = new FontRenderer(font);

        this.player = new Player(world);

        this.input = Input.getInstance();
        this.window = Window.getInstance();

        GLFW.glfwSetScrollCallback(Valkyrie.WINDOW_ID, (id, xOffset, yOffset) -> {
            selectedBlock += yOffset;
            if (selectedBlock > BlockRegistry.getBlockCount() - 1) {
                selectedBlock = 0;
            } else if (selectedBlock < 0) {
                selectedBlock = BlockRegistry.getBlockCount() - 1;
            }
        });

        this.gpuInfo = glGetString(GL_VENDOR) + " - " + glGetString(GL_RENDERER);
    }

    int selectedBlock = 0;

    @Override
    public void render(float delta) {
        camera.update(Window.id, delta);
        player.setRotation(new Vector3f(camera.getRotationX(), camera.getRotationY(), 0));
        player.update(delta);
        camera.setPosition(new Vector3f(player.getPosition().x, player.getPosition().y + 1.5f, player.getPosition().z));
        worldRenderer.updateFrustum(camera);

        skyboxRenderer.render(camera);

        worldRenderer.render(world, camera);

        if (hitPosition != null) {
            raycastRenderer.render(camera, hitPosition);
        }

        selectedBlockRenderer.render(selectedBlock);

        if (Valkyrie.DEBUG_MODE) {
            fontRenderer.render(String.format(
                    "Valkyrie | FPS: %04.1f (delta: %06.4fs)" +
                            "\nMemory usage: %06.2f/%06.2f MB" +
                            "\n" + gpuInfo +
                            "\n\nWASD: Movement" +
                            "\nSpace: Jump" +
                            "\nH: Toggle VSync" +
                            "\nP: Garbage collector",
                    1f / delta,
                    delta,
                    (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024f),
                    Runtime.getRuntime().totalMemory() / (1024 * 1024f)
            ), 50, 50);
        }
    }

    @Override
    public void fixedUpdate() {
        world.update(1 / 20f, camera);

        hitPosition = world.rayCast(camera.getPosition(), camera.getDirection(), 10, false);

        if (hitPosition != null) {
            if (Input.isButtonJustPressed(0)) {
                world.setBlock(0, hitPosition);
            } else if (Input.isButtonJustPressed(1) && BlockRegistry.getBlock(selectedBlock) != null) {
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
        fontRenderer.setupProjectionMatrix(width, height);
    }

    @Override
    public void onClose() {

    }

    @Override
    public void dispose() {

    }

}
