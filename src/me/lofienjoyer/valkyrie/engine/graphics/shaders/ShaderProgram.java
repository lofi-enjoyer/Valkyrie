package me.lofienjoyer.valkyrie.engine.graphics.shaders;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import me.lofienjoyer.valkyrie.Valkyrie;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public abstract class ShaderProgram {

    private final int programID;
    private final int vertexShaderID;
    private final int fragmentShaderID;

    private final FloatBuffer buffer;

    public ShaderProgram(String vertexFile, String fragmentFile) {
        buffer = BufferUtils.createFloatBuffer(16);

        vertexShaderID = loadShader(vertexFile,GL20.GL_VERTEX_SHADER);
        fragmentShaderID = loadShader(fragmentFile,GL20.GL_FRAGMENT_SHADER);
        programID = GL20.glCreateProgram();
        GL20.glAttachShader(programID, vertexShaderID);
        GL20.glAttachShader(programID, fragmentShaderID);
        bindAttributes();
        GL20.glLinkProgram(programID);
        GL20.glValidateProgram(programID);
        getAllUniformLocations();
    }

    protected abstract void getAllUniformLocations();

    protected int getUniformLocation(String uniformName) {
        return GL30.glGetUniformLocation(programID, uniformName);
    }

    protected void loadFloat(int location, float value) {
        GL30.glUniform1f(location, value);
    }

    protected void loadVector(int location, Vector3f vector) {
        GL30.glUniform3f(location, vector.x, vector.y, vector.z);
    }

    protected void loadBoolean(int location, boolean value) {
        GL30.glUniform1f(location, value ? 1 : 0);
    }

    protected void loadMatrix(int location, Matrix4f matrix) {
        GL30.glUniformMatrix4fv(location, false, matrix.get(buffer));
    }

    protected void loadInt(int location, int value) {
        GL30.glUniform1i(location, value);
    }

    public void start() {
        GL20.glUseProgram(programID);
    }

    public void cleanUp(){
        GL30.glUseProgram(0);
        GL30.glDetachShader(programID, vertexShaderID);
        GL30.glDetachShader(programID, fragmentShaderID);
        GL30.glDeleteShader(vertexShaderID);
        GL30.glDeleteShader(fragmentShaderID);
        GL30.glDeleteProgram(programID);
    }

    protected abstract void bindAttributes();

    protected void bindAttribute(int attribute, String variableName){
        GL30.glBindAttribLocation(programID, attribute, variableName);
    }

    private static int loadShader(String file, int type) {
        String source;
        try {
            source = Files.readString(Paths.get(file));
        } catch (IOException e) {
            Valkyrie.LOG.severe(e.getMessage());
            return 0;
        }

        int shaderID = GL30.glCreateShader(type);
        GL30.glShaderSource(shaderID, source);
        GL30.glCompileShader(shaderID);

        if(GL30.glGetShaderi(shaderID, GL30.GL_COMPILE_STATUS )== GL11.GL_FALSE){
            Valkyrie.LOG.severe("Shader " + file + " could not be compiled: " + GL30.glGetShaderInfoLog(shaderID, 500));
            System.exit(-1);
        }

        return shaderID;
    }

}
