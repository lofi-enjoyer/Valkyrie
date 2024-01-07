package me.lofienjoyer.valkyrie.game;

import me.lofienjoyer.valkyrie.Valkyrie;
import me.lofienjoyer.valkyrie.engine.graphics.camera.Camera;
import me.lofienjoyer.valkyrie.engine.graphics.display.Window;
import me.lofienjoyer.valkyrie.engine.graphics.font.ValkyrieFont;
import me.lofienjoyer.valkyrie.engine.graphics.render.FontRenderer;
import me.lofienjoyer.valkyrie.engine.graphics.render.RaycastRenderer;
import me.lofienjoyer.valkyrie.engine.graphics.render.SkyboxRenderer;
import me.lofienjoyer.valkyrie.engine.graphics.render.WorldRenderer;
import me.lofienjoyer.valkyrie.engine.graphics.render.gui.CrosshairRenderer;
import me.lofienjoyer.valkyrie.engine.graphics.render.gui.SelectedBlockRenderer;
import me.lofienjoyer.valkyrie.engine.input.Input;
import me.lofienjoyer.valkyrie.engine.scene.IScene;
import me.lofienjoyer.valkyrie.engine.world.BlockRegistry;
import me.lofienjoyer.valkyrie.engine.world.Player;
import me.lofienjoyer.valkyrie.engine.world.World;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static org.lwjgl.opengl.GL11.*;

public class WorldScene implements IScene {

    private Camera camera;
    private World world;
    private WorldRenderer worldRenderer;
    private SkyboxRenderer skyboxRenderer;
    private SelectedBlockRenderer selectedBlockRenderer;
    private RaycastRenderer raycastRenderer;
    private CrosshairRenderer crosshairRenderer;
    private Player player;
    private Vector3f hitPosition;
    private FontRenderer fontRenderer;
    private String gpuInfo;
    private Timer worldTimer;

    int selectedBlock = 0;
    private final Map<Vector3f, Integer> blocksToSet = new HashMap<>();

    @Override
    public void init() {
        this.camera = new Camera();
        this.world = new World();
        this.worldRenderer = new WorldRenderer(world);
        this.skyboxRenderer = new SkyboxRenderer();
        this.selectedBlockRenderer = new SelectedBlockRenderer();
        this.raycastRenderer = new RaycastRenderer();
        this.crosshairRenderer = new CrosshairRenderer();
        var font = new ValkyrieFont("res/fonts/Silkscreen-Regular.ttf", 16);
        this.fontRenderer = new FontRenderer(font);

        this.player = new Player(world);

        GLFW.glfwSetScrollCallback(Valkyrie.WINDOW_ID, (id, xOffset, yOffset) -> {
            selectedBlock += yOffset;
            if (selectedBlock > BlockRegistry.getBlockCount() - 1) {
                selectedBlock = 0;
            } else if (selectedBlock < 0) {
                selectedBlock = BlockRegistry.getBlockCount() - 1;
            }
        });

        this.gpuInfo = glGetString(GL_VENDOR) + " - " + glGetString(GL_RENDERER);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                fixedUpdate();
            }
        };

        this.worldTimer = new Timer("World timer");
        worldTimer.scheduleAtFixedRate(task, 0, 50);
    }

    @Override
    public void render(float delta) {
        camera.update(Window.id, delta);
        player.setRotation(new Vector3f(camera.getRotationX(), camera.getRotationY(), 0));
        player.update(delta);
        camera.setPosition(new Vector3f(player.getPosition().x, player.getPosition().y + 1.5f, player.getPosition().z));
        worldRenderer.update();

        skyboxRenderer.render(camera);

        worldRenderer.render(camera);

        hitPosition = world.rayCast(camera.getPosition(), camera.getDirection(), 10, false);
        if (hitPosition != null) {
            raycastRenderer.render(camera, hitPosition);

            synchronized (blocksToSet) {
                if (Input.isButtonJustPressed(0)) {
                    blocksToSet.put(new Vector3f(hitPosition), 0);
                } else if (Input.isButtonJustPressed(1) && BlockRegistry.getBlock(selectedBlock) != null) {
                    var blockToPlacePosition = world.rayCast(camera.getPosition(), camera.getDirection(), 10, true);
                    blocksToSet.put(new Vector3f(blockToPlacePosition), selectedBlock);
                }
            }
        }

        selectedBlockRenderer.render(selectedBlock);
        crosshairRenderer.render();

        if (Valkyrie.DEBUG_MODE) {
            fontRenderer.render(String.format(
                    "Valkyrie 0.1.2 | FPS: %04.1f (delta: %06.4fs)" +
                            "\nMemory usage: %06.2f/%06.2f MB" +
                            "\n" + gpuInfo +
                            "\nX: %.2f | Y: %.2f | Z: %.2f" +
                            "\n\nH: Toggle VSync" +
                            "\nR: Toggle wireframe" +
                            "\nP: Garbage collector",
                    1f / delta,
                    delta,
                    (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024f),
                    Runtime.getRuntime().totalMemory() / (1024 * 1024f),
                    player.getPosition().x,
                    player.getPosition().y,
                    player.getPosition().z
            ), 50, 50);
        }
    }

    public void fixedUpdate() {
        world.update(1 / 20f, camera);

        synchronized (blocksToSet) {
            blocksToSet.forEach((position, voxel) -> {
                world.setBlock(voxel, position);
            });
            blocksToSet.clear();
        }
    }

    @Override
    public void onResize(int width, int height) {
        worldRenderer.setupProjectionMatrix(width, height);
        skyboxRenderer.setupProjectionMatrix(width, height);
        selectedBlockRenderer.setupProjectionMatrix(width, height);
        raycastRenderer.setupProjectionMatrix(width, height);
        fontRenderer.setupProjectionMatrix(width, height);
        crosshairRenderer.setupProjectionMatrix(width, height);
    }

    @Override
    public void onClose() {
        worldTimer.cancel();
    }

    @Override
    public void dispose() {

    }

}
