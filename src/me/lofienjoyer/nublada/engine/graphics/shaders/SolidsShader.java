package me.lofienjoyer.nublada.engine.graphics.shaders;

import me.lofienjoyer.nublada.engine.graphics.camera.Camera;
import me.lofienjoyer.nublada.engine.utils.Maths;
import org.joml.Matrix4f;

public class SolidsShader extends ShaderProgram {

    private static final String VERTEX_FILE = "res/shaders/vertex.glsl";
    private static final String FRAGMENT_FILE = "res/shaders/fragment.glsl";

    private int locationTransformationMatrix;
    private int locationProjectionMatrix;
    private int locationViewMatrix;
    private int locationCameraPosition;
    private int locationViewDistance;
    private int locationInWater;

    public SolidsShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        locationTransformationMatrix = super.getUniformLocation("transformationMatrix");
        locationProjectionMatrix = super.getUniformLocation("projectionMatrix");
        locationViewMatrix = super.getUniformLocation("viewMatrix");
        locationCameraPosition = super.getUniformLocation("cameraPosition");
        locationViewDistance = super.getUniformLocation("viewDistance");
        locationInWater = super.getUniformLocation("inWater");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "color");
        super.bindAttribute(2, "light");
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(locationTransformationMatrix, matrix);
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrix(locationProjectionMatrix, matrix);
    }

    public void loadViewMatrix(Camera camera) {
        super.loadMatrix(locationViewMatrix, Maths.createViewMatrix(camera));
        super.loadVector(locationCameraPosition, camera.getPosition());
    }

    public void loadViewDistance(float viewDistance) {
        super.loadFloat(locationViewDistance, viewDistance);
    }

    public void setInWater(boolean inWater) {
        super.loadBoolean(locationInWater, inWater);
    }

}
