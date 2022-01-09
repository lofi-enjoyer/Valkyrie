#version 400 core

in vec3 position;
in vec3 color;

out vec3 passColor;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 translation;

void main() {
    gl_Position = projectionMatrix * viewMatrix * vec4(position + translation, 1.0);
    passColor = color;
}