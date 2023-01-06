package me.lofienjoyer.nublada.game;

import me.lofienjoyer.nublada.Nublada;
import me.lofienjoyer.nublada.engine.graphics.render.RaycastRenderer;
import me.lofienjoyer.nublada.engine.scene.IScene;
import me.lofienjoyer.nublada.engine.graphics.camera.Camera;
import me.lofienjoyer.nublada.engine.graphics.display.Window;
import me.lofienjoyer.nublada.engine.graphics.render.SkyboxRenderer;
import me.lofienjoyer.nublada.engine.graphics.render.WorldRenderer;
import me.lofienjoyer.nublada.engine.graphics.render.gui.SelectedBlockRenderer;
import me.lofienjoyer.nublada.engine.scripting.Script;
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

    private Script script;

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

        Nublada.LOG.info("Starting script loading...");
        this.script = new Script("res/scripts");
        Nublada.LOG.info("Script loaded succesfully!");
    }

    int mouse = 0;
    int selectedBlock = 0;

    @Override
    public void render(float delta) {
        player.update(delta);
        camera.update(Window.id, delta);
        worldRenderer.updateFrustum(camera);

        skyboxRenderer.render(camera);

        worldRenderer.render(world, camera);

        // TODO: 24/12/2022 Make this async
        Vector3f hitPosition = world.rayCast(camera.getPosition(), camera.getDirection(), 10, false);
        if (hitPosition != null) {
            raycastRenderer.render(camera, hitPosition);
        }

        selectedBlockRenderer.render(selectedBlock);

        if (GLFW.glfwGetMouseButton(Window.id, 0) == 0 &&
                GLFW.glfwGetMouseButton(Window.id, 1) == 0) mouse = 0;

        if (mouse != 1 && GLFW.glfwGetMouseButton(Window.id, 0) != 0) {
            mouse = 1;
            script.callFunction("onBreak", world, camera);
        }

        if (mouse != 1 && GLFW.glfwGetMouseButton(Window.id, 1) != 0) {
            mouse = 1;
            script.callFunction("onPlace", world, camera, selectedBlock);
        }
    }

    @Override
    public void onResize(int width, int height) {
//        worldRenderer.setupProjectionMatrix(width, height);
//        skyboxRenderer.setupProjectionMatrix(width, height);
//        selectedBlockRenderer.setupProjectionMatrix(width, height);
    }

    @Override
    public void onClose() {
        world.saveWorld();
    }

    @Override
    public void dispose() {

    }

}
