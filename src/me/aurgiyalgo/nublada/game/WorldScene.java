package me.aurgiyalgo.nublada.game;

import me.aurgiyalgo.nublada.Nublada;
import me.aurgiyalgo.nublada.engine.scene.IScene;
import me.aurgiyalgo.nublada.engine.graphics.camera.Camera;
import me.aurgiyalgo.nublada.engine.graphics.display.Window;
import me.aurgiyalgo.nublada.engine.graphics.render.SkyboxRenderer;
import me.aurgiyalgo.nublada.engine.graphics.render.WorldRenderer;
import me.aurgiyalgo.nublada.engine.graphics.render.gui.SelectedBlockRenderer;
import me.aurgiyalgo.nublada.engine.world.BlockRegistry;
import me.aurgiyalgo.nublada.engine.world.World;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class WorldScene implements IScene {

    private Camera camera;
    private World world;
    private WorldRenderer worldRenderer;
    private SkyboxRenderer skyboxRenderer;
    private SelectedBlockRenderer selectedBlockRenderer;

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

    @Override
    public void render(float delta) {
        camera.update(Window.id, delta);
        worldRenderer.updateFrustum(camera);

        skyboxRenderer.render(camera);

        worldRenderer.render(world, camera);

        selectedBlockRenderer.render(selectedBlock + 1);

        if (GLFW.glfwGetMouseButton(Window.id, 0) == 0 &&
                GLFW.glfwGetMouseButton(Window.id, 1) == 0) mouse = 0;

        if (mouse != 1 && GLFW.glfwGetMouseButton(Window.id, 0) != 0) {
            mouse = 1;
            Vector3f position = world.rayCast(camera.getPosition(), camera.getDirection(), 10, false);
            if (position != null)
                world.setBlock(0, position);
        }

        if (mouse != 1 && GLFW.glfwGetMouseButton(Window.id, 1) != 0) {
            mouse = 1;
            Vector3f position = world.rayCast(camera.getPosition(), camera.getDirection(), 10, true);
            if (position != null)
                world.setBlock(selectedBlock + 1, position);
        }
    }

    @Override
    public void onResize(int width, int height) {
        worldRenderer.setupProjectionMatrix(width, height);
        skyboxRenderer.setupProjectionMatrix(width, height);
        selectedBlockRenderer.setupProjectionMatrix(width, height);
    }

    @Override
    public void dispose() {

    }

}
