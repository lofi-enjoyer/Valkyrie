#version 400 core

in vec3 passColor;
in vec3 passLight;
in vec3 toLightVector;
in float distance;
in float faceLight;
in float passViewDistance;

out vec4 outColor;

uniform sampler2DArray textureSampler;

const vec3 skyColor = vec3(0.45, 0.71, 1.00);

void main() {

    vec4 textureColour = texture(textureSampler, passColor);

    float visibility = 1;
    if (distance > passViewDistance - 64) {
        visibility = (distance - passViewDistance + 32) / 32.0;
        visibility = clamp(1 - visibility, 0.0, 1.0);
    }

    outColor = textureColour * faceLight;
    outColor = mix(vec4(skyColor, 1.0), outColor, visibility);

}