#version 400 core

in vec4 data;

out vec4 passTexture;
out float passLight;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;

const float[6] lights = float[6](
0.85,
0.85,
1.00,
0.50,
0.70,
0.70
);

void main() {

    uint xUv = (uint(data.a) >> 0u) & 0x1u;
    uint yUv = (uint(data.a) >> 1u) &0x1u;
    uint zUv = (uint(data.a) >> 7u);
    uint light = (uint(data.a) >> 2u) &0x7u;
    uint cull = (uint(data.a) >> 5u) &0x1u;
    uint wave = (uint(data.a) >> 6u) &0x1u;

    vec4 worldPosition = transformationMatrix * vec4(data.xyz, 1.0);

    gl_Position = projectionMatrix * worldPosition;

    passTexture = vec4(xUv, yUv, zUv, cull);
    passLight = lights[light];
}