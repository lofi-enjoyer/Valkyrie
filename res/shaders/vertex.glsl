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

    float y = (vertex.x >> 18u) & 0x1FFu;
    float x = (vertex.x >> 9u) & 0x1FFu;
    float z = (vertex.x) & 0x1FFu;

    uint yUv = (vertex.y >> 18u) & 0x1FFu;
    uint xUv = (vertex.y >> 9u) & 0x1FFu;
    uint zUv = (vertex.y) & 0x1FFu;
    // FIXME: 02/02/2023 Temporary light value
    int light = 5;

    vec3 position = vec3(x, y, z);

    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    vec4 positionRelativeToCam = viewMatrix * worldPosition;

    gl_Position = projectionMatrix * positionRelativeToCam;
    passColor = vec3(xUv, yUv, zUv);
    passLight = normalVectors[light];

    distance = length(cameraPosition.xz - worldPosition.xz);

    faceLight = lights[5];
    passViewDistance = viewDistance;
    passInWater = inWater;
}