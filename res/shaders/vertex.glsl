#version 400 core

in vec3 position;
in vec3 color;
in float light;

out vec3 passColor;
out vec3 passLight;
out float passInWater;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 cameraPosition;
uniform float viewDistance;
uniform float inWater;

const vec3[6] normalVectors = vec3[6](
    vec3( 1,  0,  0),
    vec3(-1,  0,  0),
    vec3( 0,  1,  0),
    vec3( 0, -1,  0),
    vec3( 0,  0,  1),
    vec3( 0,  0, -1)
);

const float[6] lights = float[6](
    0.85,
    0.85,
    1.00,
    0.50,
    0.70,
    0.70
);

out float distance;
out float faceLight;
out float passViewDistance;

void main() {

    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    vec4 positionRelativeToCam = viewMatrix * worldPosition;

    gl_Position = projectionMatrix * positionRelativeToCam;
    passColor = color;
    passLight = normalVectors[int(light)];

    distance = length(cameraPosition.xz - worldPosition.xz);

    faceLight = lights[int(light)];
    passViewDistance = viewDistance;
    passInWater = inWater;
}