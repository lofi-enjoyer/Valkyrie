package me.aurgiyalgo.nublada.game;

import me.aurgiyalgo.nublada.engine.scene.IScene;
import me.aurgiyalgo.nublada.graphics.camera.Camera;
import me.aurgiyalgo.nublada.graphics.display.Window;
import me.aurgiyalgo.nublada.graphics.render.WorldRenderer;
import me.aurgiyalgo.nublada.world.World;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class WorldScene implements IScene {

    private Camera camera;
    private World world;
    private WorldRenderer renderer;

    @Override
    public void init() {
        this.camera = new Camera();
        this.world = new World();
        this.renderer = new WorldRenderer();
        renderer.setupProjectionMatrix(640, 360);
    }

    int mouse = 0;

    @Override
    public void render(float delta) {
        camera.update(Window.id, delta);
        renderer.updateFrustum(camera);

        renderer.render(world, camera);

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
                world.setBlock(5, position);
        }
    }

    @Override
    public void onResize(int width, int height) {
        renderer.setupProjectionMatrix(width, height);
    }

    @Override
    public void dispose() {

    }

}
