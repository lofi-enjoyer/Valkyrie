#version 400 core

in uvec2 vertex;

out vec3 passColor;
out vec3 passLight;
out float passInWater;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 cameraPosition;
uniform float viewDistance;
uniform float inWater;
uniform float time;
uniform int leavesId;
uniform int waterId;

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

const float leavesMovement = 32;

void main() {

    float y = (vertex.x >> 18u) & 0x1FFu;
    float x = (vertex.x >> 9u) & 0x1FFu;
    float z = (vertex.x) & 0x1FFu;

    uint yUv = (vertex.y >> 18u) & 0x1FFu;
    uint xUv = (vertex.y >> 9u) & 0x1FFu;
    uint zUv = (vertex.y) & 0x1FFu;
    uint wUv = (vertex.y >> 27u) & 0x7u;
    // FIXME: 02/02/2023 Temporary light value
    int light = 5;

    vec3 position = vec3(x, y, z);
    if (zUv == leavesId) {
        position.x += sin(position.y + time) / leavesMovement;
        position.z += cos(position.x + time + 2) / leavesMovement;
        position.y += sin(position.z + time + 5) / leavesMovement;
    }

    if (zUv == waterId) {
        position.y -= 1 / 8.0;
        position.y += sin(time) * (1 / 16.0);
    }

    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    vec4 positionRelativeToCam = viewMatrix * worldPosition;

    gl_Position = projectionMatrix * positionRelativeToCam;
    passColor = vec3(xUv, yUv, zUv);
    passLight = normalVectors[wUv];

    distance = length(cameraPosition.xz - worldPosition.xz);

    faceLight = lights[wUv];
    passViewDistance = viewDistance;
    passInWater = inWater;
}