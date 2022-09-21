package me.lofienjoyer.nublada.engine.graphics.shaders;

import me.lofienjoyer.nublada.engine.graphics.camera.Camera;
import me.lofienjoyer.nublada.engine.utils.Maths;
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
    private int locationLeavesId;
    private int locationWaterId;
    private int locationInWater;

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
        locationLeavesId = super.getUniformLocation("leavesId");
        locationWaterId = super.getUniformLocation("waterId");
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

    public void loadTime(float time) {
        super.loadFloat(locationTime, time);
    }

    public void loadViewDistance(float viewDistance) {
        super.loadFloat(locationViewDistance, viewDistance);
    }

    public void loadLeavesId(int id) {
        super.loadInt(locationLeavesId, id);
    }

    public void loadWaterId(int id) {
        super.loadInt(locationWaterId, id);
    }

    public void setInWater(boolean inWater) {
        super.loadBoolean(locationInWater, inWater);
    }

}
