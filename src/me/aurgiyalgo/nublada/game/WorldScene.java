package me.aurgiyalgo.nublada.game;

import me.aurgiyalgo.nublada.Nublada;
import me.aurgiyalgo.nublada.engine.scene.IScene;
import me.aurgiyalgo.nublada.engine.graphics.camera.Camera;
import me.aurgiyalgo.nublada.engine.graphics.display.Window;
import me.aurgiyalgo.nublada.engine.graphics.render.SkyboxRenderer;
import me.aurgiyalgo.nublada.engine.graphics.render.WorldRenderer;
import me.aurgiyalgo.nublada.engine.graphics.render.gui.SelectedBlockRenderer;
import me.aurgiyalgo.nublada.engine.world.BlockRegistry;
import me.aurgiyalgo.nublada.engine.world.Player;
import me.aurgiyalgo.nublada.engine.world.World;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class WorldScene implements IScene {

    private Camera camera;
    private World world;
    private WorldRenderer worldRenderer;
    private SkyboxRenderer skyboxRenderer;
    private SelectedBlockRenderer selectedBlockRenderer;
    private Player player;

    private ScriptEngine scriptEngine;
    private Invocable script;

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
        this.scriptEngine = new ScriptEngineManager().getEngineByName("Nashorn");
        try {
            scriptEngine.eval(new FileReader("res/scripts/" + getClass().getSimpleName().toLowerCase() + ".js"));
            script = (Invocable) scriptEngine;
        } catch (ScriptException | FileNotFoundException e) {
            Nublada.LOG.warning("Script loading failed!");
            e.printStackTrace();
            return;
        }
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

        selectedBlockRenderer.render(selectedBlock + 1);

        if (GLFW.glfwGetMouseButton(Window.id, 0) == 0 &&
                GLFW.glfwGetMouseButton(Window.id, 1) == 0) mouse = 0;

        if (mouse != 1 && GLFW.glfwGetMouseButton(Window.id, 0) != 0) {
            mouse = 1;
//            Vector3f position = world.rayCast(camera.getPosition(), camera.getDirection(), 10, false);
//            if (position != null)
//                world.setBlock(0, position);
            try {
                script.invokeFunction("onBreak", world, camera);
            } catch (ScriptException | NoSuchMethodException e) {}
        }

        if (mouse != 1 && GLFW.glfwGetMouseButton(Window.id, 1) != 0) {
            mouse = 1;
//            Vector3f position = world.rayCast(camera.getPosition(), camera.getDirection(), 10, true);
//            if (position != null)
//                world.setBlock(selectedBlock + 1, position);
            try {
                script.invokeFunction("onPlace", world, camera, selectedBlock + 1);
            } catch (ScriptException | NoSuchMethodException e) {}
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
