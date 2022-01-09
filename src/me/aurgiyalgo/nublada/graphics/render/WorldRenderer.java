package me.aurgiyalgo.nublada.graphics.render;

import me.aurgiyalgo.nublada.graphics.camera.Camera;
import me.aurgiyalgo.nublada.graphics.shaders.StaticShader;
import me.aurgiyalgo.nublada.utils.Maths;
import me.aurgiyalgo.nublada.world.World;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL30;

import static me.aurgiyalgo.nublada.world.World.CHUNK_WIDTH;

public class WorldRenderer {

    private final Matrix4f projectionMatrix;
    private final FrustumCullingTester tester;

    public WorldRenderer() {
        this.projectionMatrix = new Matrix4f();
        this.projectionMatrix.perspective(70, 16f/9f, 0.1f, 5000);

        this.tester = new FrustumCullingTester();
    }

    public void render(World world, StaticShader shader, Camera camera) {
        GL30.glEnable(GL30.GL_DEPTH_TEST);

        shader.loadProjectionMatrix(projectionMatrix);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, world.getTextureId());

        long timer = System.nanoTime();
        world.chunks.forEach((position, chunk) -> {
            if (chunk.getModel() == null) return;
            if (!tester.isChunkInside(chunk, camera.getPosition().y)) return;
            GL30.glBindVertexArray(chunk.getModel().getVao());
            GL30.glEnableVertexAttribArray(0);
            GL30.glEnableVertexAttribArray(1);
//            shader.loadTransformationMatrix(Maths.createTransformationMatrix(position, World.CHUNK_SIZE));
            shader.loadTranslation(position);
//            GL30.glDrawArrays(GL30.GL_TRIANGLES, chunk.getModel().getVertexCount(), GL30.GL_UNSIGNED_INT);
            GL30.glDrawElements(GL30.GL_TRIANGLES, chunk.getModel().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
        });
//        System.out.println("World render: " + ((System.nanoTime() - timer) / 1000000000f) + "s");

        GL30.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
    }

    public void updateFrustum(Camera camera) {
        tester.updateFrustum(projectionMatrix, Maths.createViewMatrix(camera));
    }

    private boolean isInFrustum(Vector2f position, Camera camera) {

//        float l1x = position.x - camera.getPosition().x;
//        float l1y = 0;
//        float l1z = position.y - camera.getPosition().z;
//        float l1mag = (float) Math.sqrt(l1x*l1x + l1y*l1y + l1z*l1z);
//        float l2x = (float) Math.sin(camera.getRotationX() - 90);
//        float l2y = 0;
//        float l2z = (float) Math.cos(camera.getRotationX() - 90);
//        float dot = l1x * l2x + l1y * l2y + l1z * l2z;
//        float angle = (float) Math.acos( dot / l1mag);
//        return Math.abs(angle) > 180/2f;

        float x = position.x * CHUNK_WIDTH - camera.getPosition().x + 16;
        float z = position.y * CHUNK_WIDTH - camera.getPosition().z + 16;
        float a = (float) (Math.atan(z / x) - (Math.PI));
        float b = (float) Math.toRadians(camera.getRotationX() - 90);
        double cosAB = Math.cos(a) * Math.cos(b) + Math.sin(a) * Math.sin(b);
        float angle = (float) Math.acos(cosAB);
        System.out.println(Math.toDegrees(a) + " - " + (camera.getRotationX() - 90));

        return Math.toDegrees(angle) < 70/2f;
    }

}
