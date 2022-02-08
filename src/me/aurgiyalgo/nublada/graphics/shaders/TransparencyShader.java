package me.aurgiyalgo.nublada.graphics.shaders;

import me.aurgiyalgo.nublada.graphics.camera.Camera;
import me.aurgiyalgo.nublada.utils.Maths;
import org.joml.Matrix4f;

public class TransparencyShader extends ShaderProgram{

    private static final String VERTEX_FILE = "res/shaders/transparent_vertex.glsl";
    private static final String FRAGMENT_FILE = "res/shaders/transparent_fragment.glsl";

    private int locationTransformationMatrix;
    private int locationProjectionMatrix;
    private int locationViewMatrix;
    private int locationTime;
    private int locationCameraPosition;
    private int locationViewDistance;

    public TransparencyShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        locationTransformationMatrix = super.getUniformLocation("transformationMatrix");
        locationProjectionMatrix = super.getUniformLocation("projectionMatrix");
        locationViewMatrix = super.getUniformLocation("viewMatrix");
        locationTime = super.getUniformLocation("time");
        locationCameraPosition = super.getUniformLocation("cameraPosition");
        locationViewDistance = super.getUniformLocation("viewDistance");
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

    public void loadTime(float time) {
        super.loadFloat(locationTime, time);
    }

    public void loadViewDistance(float viewDistance) {
        super.loadFloat(locationViewDistance, viewDistance);
    }

}
