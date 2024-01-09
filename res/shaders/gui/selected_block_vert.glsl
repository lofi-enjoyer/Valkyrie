#version 400 core

in vec3 position;
in vec3 color;

out vec3 passTexture;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;

void main() {

    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);

    gl_Position = projectionMatrix * worldPosition;

    passTexture = color;
}