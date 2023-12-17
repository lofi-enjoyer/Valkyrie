package me.lofienjoyer.valkyrie.engine.graphics.shaders;

public class FboShader extends ShaderProgram {

    private static final String VERTEX_FILE = "res/shaders/fboVertex.glsl";
    private static final String FRAGMENT_FILE = "res/shaders/fboFragment.glsl";

    public FboShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "aPos");
        super.bindAttribute(1, "aTexCoords");
    }

}
