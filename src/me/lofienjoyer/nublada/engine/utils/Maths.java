package me.lofienjoyer.nublada.engine.utils;

import me.lofienjoyer.nublada.engine.graphics.camera.Camera;
import me.lofienjoyer.nublada.engine.world.World;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class Maths {

    public static Matrix4f createTransformationMatrix(Vector2i position) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(position.x * World.CHUNK_WIDTH, 0, position.y * World.CHUNK_WIDTH);
        return matrix;
    }

    public static Matrix4f createTransformationMatrix(Vector2i position, int scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(position.x * World.CHUNK_WIDTH, 0, position.y * World.CHUNK_WIDTH);
        matrix.scale(scale);
        return matrix;
    }

    public static Matrix4f createTransformationMatrix(Vector2f position, Vector3f rotation) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0));
        matrix.rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        matrix.translate(position.x, position.y, 0);
        return matrix;
    }

    public static Matrix4f createTransformationMatrix(Vector3f position, int offset) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(position.x, position.y, position.z);
        return matrix;
    }

    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.rotate((float) Math.toRadians(camera.getRotationY()), new Vector3f(1, 0, 0));
        matrix.rotate((float) Math.toRadians(camera.getRotationX()), new Vector3f(0, 1, 0));
        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        matrix.translate(negativeCameraPos);
        return matrix;
    }

    public static float intbound(float a, float b) {
        if (b < 0) {
            return intbound(-a, -b);
        } else {
            a = mod(a, 1);
            return (1-a)/b;
        }
    }

    public static float mod(float value, float modulus) {
        return (value % modulus + modulus) % modulus;
    }

    public static int signum(float x) {
        return x > 0 ? 1 : x < 0 ? -1 : 0;
    }

}
