package me.aurgiyalgo.nublada.graphics.shaders;

import me.aurgiyalgo.nublada.graphics.camera.Camera;
import me.aurgiyalgo.nublada.utils.Maths;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SkyboxShader extends ShaderProgram {

    private static final String VERTEX_FILE = "res/shaders/skybox_vertex.glsl";
    private static final String FRAGMENT_FILE = "res/shaders/skybox_fragment.glsl";

    private int locationProjectionMatrix;
    private int locationViewMatrix;
    private int locationFogColor;

    public SkyboxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix(locationProjectionMatrix, matrix);
    }

    public void loadViewMatrix(Camera camera){
        Matrix4f matrix = Maths.createViewMatrix(camera);
        matrix.m30(0);
        matrix.m31(0);
        matrix.m32(0);
        super.loadMatrix(locationViewMatrix, matrix);
    }

    public void loadFogColor(Vector3f color) {
        super.loadVector(locationFogColor, color);
    }

    @Override
    protected void getAllUniformLocations() {
        locationProjectionMatrix = super.getUniformLocation("projectionMatrix");
        locationViewMatrix = super.getUniformLocation("viewMatrix");
        locationFogColor = super.getUniformLocation("fogColor");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
}
