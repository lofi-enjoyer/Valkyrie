package me.lofienjoyer.valkyrie.game;

import me.lofienjoyer.valkyrie.Valkyrie;
import me.lofienjoyer.valkyrie.engine.graphics.camera.Camera;
import me.lofienjoyer.valkyrie.engine.graphics.display.Window;
import me.lofienjoyer.valkyrie.engine.graphics.font.ValkyrieFont;
import me.lofienjoyer.valkyrie.engine.graphics.framebuffer.ColorNormalFramebuffer;
import me.lofienjoyer.valkyrie.engine.graphics.mesh.QuadMesh;
import me.lofienjoyer.valkyrie.engine.graphics.render.*;
import me.lofienjoyer.valkyrie.engine.graphics.render.gui.CrosshairRenderer;
import me.lofienjoyer.valkyrie.engine.graphics.render.gui.SelectedBlockRenderer;
import me.lofienjoyer.valkyrie.engine.graphics.shaders.Shader;
import me.lofienjoyer.valkyrie.engine.input.Input;
import me.lofienjoyer.valkyrie.engine.input.KeyMapping;
import me.lofienjoyer.valkyrie.engine.resources.ResourceLoader;
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

import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class WorldScene implements IScene {

    private Camera camera;
    private World world;
    private WorldRenderer worldRenderer;
    private Player player;
    private String gpuInfo;
    private Timer worldTimer;
    private ValkyrieFont font;
    private ColorNormalFramebuffer worldFbo;
    private QuadMesh quadMesh;
    private Shader shader;

    int selectedBlock = 0;
    private final Map<Vector3f, Integer> blocksToSet = new HashMap<>();

    @Override
    public void init() {
        this.camera = new Camera();
        this.world = new World();
        this.worldRenderer = new WorldRenderer(world);
        SkyboxRenderer.init();
        SelectedBlockRenderer.init();
        RaycastRenderer.init();
        CrosshairRenderer.init();
        FontRenderer.init();
        this.worldFbo = new ColorNormalFramebuffer(640, 360);
        this.quadMesh = new QuadMesh();
        this.shader = ResourceLoader.loadShader("FBO Shader", "res/shaders/postprocessing/world_vert.glsl", "res/shaders/postprocessing/world_frag.glsl");

        this.font = new ValkyrieFont("res/fonts/Silkscreen-Regular.ttf", 16);

        this.player = new Player(world);

        // TODO: 9/1/24 Move scroll callback to the Input class
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

        SkyboxRenderer.setFogColor(0.45f, 0.71f, 1.00f);
        SkyboxRenderer.render(camera);

        // Activates the world fbo and clears its buffers
        worldFbo.bind();
        Renderer.setClearColor(0, 0, 0, 0);
        Renderer.clearColorBuffer();
        Renderer.clearDepthBuffer();

        worldRenderer.render(camera);

        // Draw the world fbo to the main fbo
        var mainFbo = Valkyrie.getMainFramebuffer();
        mainFbo.bind();
        shader.bind();
        glViewport(0, 0, mainFbo.getWidth(), mainFbo.getHeight());
        glBindVertexArray(quadMesh.getVaoId());
        Renderer.disableDepthTest();
        Renderer.enableBlend();
        Renderer.bindTexture2D(worldFbo.getColorTextureId());
        glDrawArrays(GL_TRIANGLES, 0, 6);
        Renderer.disableBlend();

        // Casts a ray and, if found a block, renders a cube highlighting its position
        Vector3f hitPosition = world.rayCast(camera.getPosition(), camera.getDirection(), 10, false);
        if (hitPosition != null) {
            RaycastRenderer.render(camera, hitPosition);

            synchronized (blocksToSet) {
                if (Input.isButtonJustPressed(0)) {
                    blocksToSet.put(new Vector3f(hitPosition), 0);
                } else if (Input.isButtonJustPressed(1) && BlockRegistry.getBlock(selectedBlock) != null) {
                    var blockToPlacePosition = world.rayCast(camera.getPosition(), camera.getDirection(), 10, true);
                    blocksToSet.put(new Vector3f(blockToPlacePosition), selectedBlock);
                }
            }
        }

        SelectedBlockRenderer.render(selectedBlock);
        CrosshairRenderer.render();

        // Prints debug information to the screen if the debug mode is enabled
        if (Valkyrie.DEBUG_MODE) {
            FontRenderer.render(String.format(
                    "FPS: %04.1f (delta: %06.4fs)" +
                            "\nMemory usage: %06.2f/%06.2f MB" +
                            "\n" + gpuInfo +
                            "\nX: %.2f | Y: %.2f | Z: %.2f" +
                            "\n\n" + KeyMapping.getToggleVsyncKey() + ":Toggle VSync" +
                            "\n" + KeyMapping.getToggleWireframeKey() + ": Toggle wireframe" +
                            "\n" + KeyMapping.getCallGcKey() + ": Garbage collector",
                    1f / delta,
                    delta,
                    (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024f),
                    Runtime.getRuntime().totalMemory() / (1024 * 1024f),
                    player.getPosition().x,
                    player.getPosition().y,
                    player.getPosition().z
            ), 50, 50, font);
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
        SkyboxRenderer.setupProjectionMatrix(width, height);
        SelectedBlockRenderer.setupProjectionMatrix(width, height);
        RaycastRenderer.setupProjectionMatrix(width, height);
        FontRenderer.setupProjectionMatrix(width, height);
        CrosshairRenderer.setupProjectionMatrix(width, height);
        worldFbo.resize(width, height);
    }

    @Override
    public void dispose() {
        SkyboxRenderer.dispose();
        SelectedBlockRenderer.dispose();
        RaycastRenderer.dispose();
        CrosshairRenderer.dispose();
        worldTimer.cancel();
    }

}
