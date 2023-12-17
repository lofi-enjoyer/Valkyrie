package me.lofienjoyer.valkyrie.engine.graphics.shaders;

import me.lofienjoyer.valkyrie.Valkyrie;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL40.*;

public class Shader {

    private final String name;
    private final int programID;
    private final int vertexShaderID;
    private final int fragmentShaderID;

    private final FloatBuffer buffer;

    public Shader(String name, String vertexSource, String fragmentSource) {
        this.name = name;
        this.buffer = BufferUtils.createFloatBuffer(16);

        this.programID = glCreateProgram();

        this.vertexShaderID = loadShader(vertexSource, GL_VERTEX_SHADER);
        this.fragmentShaderID = loadShader(fragmentSource, GL_FRAGMENT_SHADER);

        glAttachShader(programID, vertexShaderID);
        glAttachShader(programID, fragmentShaderID);

        glLinkProgram(programID);
        glValidateProgram(programID);
    }

    protected int getUniformLocation(String uniformName) {
        return glGetUniformLocation(programID, uniformName);
    }

    protected void loadFloat(int location, float value) {
        glUniform1f(location, value);
    }

    protected void loadVector(int location, Vector3f vector) {
        glUniform3f(location, vector.x, vector.y, vector.z);
    }

    protected void loadBoolean(int location, boolean value) {
        glUniform1f(location, value ? 1 : 0);
    }

    protected void loadMatrix(int location, Matrix4f matrix) {
        glUniformMatrix4fv(location, false, matrix.get(buffer));
    }

    protected void loadInt(int location, int value) {
        glUniform1i(location, value);
    }

    public void loadFloat(String uniformName, float value) {
        loadFloat(getUniformLocation(uniformName), value);
    }

    public void loadVector(String uniformName, Vector3f vector) {
        glUniform3f(getUniformLocation(uniformName), vector.x, vector.y, vector.z);
    }

    public void loadBoolean(String uniformName, boolean value) {
        glUniform1f(getUniformLocation(uniformName), value ? 1 : 0);
    }

    public void loadMatrix(String uniformName, Matrix4f matrix) {
        glUniformMatrix4fv(getUniformLocation(uniformName), false, matrix.get(buffer));
    }

    public void loadInt(String uniformName, int value) {
        glUniform1i(getUniformLocation(uniformName), value);
    }

    public void bind() {
        glUseProgram(programID);
    }

    public void cleanUp(){
        glUseProgram(0);
        glDetachShader(programID, vertexShaderID);
        glDetachShader(programID, fragmentShaderID);
        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);
        glDeleteProgram(programID);
    }

    private int loadShader(String source, int type) {
        int shaderID = glCreateShader(type);
        glShaderSource(shaderID, source);
        glCompileShader(shaderID);

        if(glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE){
            Valkyrie.LOG.severe("Shader " + name + "(" + type + ") could not be compiled: " + glGetShaderInfoLog(shaderID, 512));
            System.exit(-1);
        }

        return shaderID;
    }

}
