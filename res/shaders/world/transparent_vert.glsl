#version 400 core

#define PI 3.14159265359

layout (location = 0) in vec3 position;
layout (location = 1) in float data;

out vec3 passColor;
out float passLight;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 cameraPosition;
uniform float time;
uniform float viewDistance;
uniform int leavesId;
uniform int waterId;
uniform float inWater;

const float[6] lights = float[6](
0.85,
0.85,
1.00,
0.50,
0.70,
0.70
);

out float distance;

const float leavesMovement = 32;
const float waterSpeed = 0.25;

void main() {

    uint xUv = (uint(data) >> 0u) & 0x1u;
    uint yUv = (uint(data) >> 1u) &0x1u;
    uint zUv = (uint(data) >> 5u);
    uint light = (uint(data) >> 2u) &0x7u;

    vec3 newPosition = position;
    if (zUv == leavesId) {
        newPosition.x += sin(position.y + time) / leavesMovement;
        newPosition.z += cos(position.x + time + 2) / leavesMovement;
        newPosition.y += sin(position.z + time + 5) / leavesMovement;
    }

    if (zUv == waterId) {
        newPosition.y -= 1 / 8.0;
        newPosition.y += sin(time) * (1 / 16.0);
    }

    vec4 worldPosition = transformationMatrix * vec4(newPosition, 1.0);
    vec4 positionRelativeToCam = viewMatrix * worldPosition;

    gl_Position = projectionMatrix * positionRelativeToCam;
    passColor = vec3(xUv, yUv, zUv);
    passLight = lights[light];

    distance = length(cameraPosition.xz - worldPosition.xz);
}