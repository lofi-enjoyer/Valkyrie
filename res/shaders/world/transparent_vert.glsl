#version 400 core

#define PI 3.14159265359

in uint vertex;

out vec3 passColor;
out vec3 passLight;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 cameraPosition;
uniform float time;
uniform float viewDistance;
uniform int leavesId;
uniform int waterId;
uniform float inWater;

const vec3[6] normalVectors = vec3[6](
    vec3( 1,  0,  0),
    vec3(-1,  0,  0),
    vec3( 0,  1,  0),
    vec3( 0, -1,  0),
    vec3( 0,  0,  1),
    vec3( 0,  0, -1)
);

out float distance;

const float leavesMovement = 32;
const float waterSpeed = 0.25;

void main() {

    float y = (vertex >> 23u) & 0x1FFu;
    float x = (vertex >> 16u) & 0x7Fu;
    float z = (vertex >> 9u) & 0x7Fu;

    uint xUv = (vertex & 0x100u) >> 8u;
    uint yUv = (vertex & 0x80u) >> 7u;
    uint zUv = vertex & 0x7Fu;
    // FIXME: 02/02/2023 Temporary light value
    int light = 5;

    vec3 newPosition = vec3(x, y, z);
    if (zUv == leavesId) {
        newPosition.x += sin(y + time) / leavesMovement;
        newPosition.z += cos(x + time + 2) / leavesMovement;
        newPosition.y += sin(z + time + 5) / leavesMovement;
    }

    if (zUv == waterId) {
        newPosition.y -= 1 / 8.0;
        newPosition.y += sin(time) * (1 / 16.0);
    }

    vec4 worldPosition = transformationMatrix * vec4(newPosition, 1.0);
    vec4 positionRelativeToCam = viewMatrix * worldPosition;

    gl_Position = projectionMatrix * positionRelativeToCam;
    passColor = vec3(xUv, yUv, zUv);
    passLight = normalVectors[light];

    distance = length(cameraPosition.xz - worldPosition.xz);
}