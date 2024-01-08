#version 400 core

in vec3 passColor;
in vec3 passLight;
in float distance;

out vec4 outColor;

uniform sampler2D textureSampler;
uniform float inWater;
uniform float viewDistance;

const vec3 skyColor = vec3(0.45, 0.71, 1.00);

const int atlasSize = 8192;
const int textureSize = 32;
const int texturesPerSide = atlasSize / textureSize;

void main() {

    float xUv = mod(passColor.z, texturesPerSide) / texturesPerSide + mod(passColor.x, 1.0) / texturesPerSide;
    float yUv = int(passColor.z / texturesPerSide) / texturesPerSide + mod(passColor.y, 1.0) / texturesPerSide;

    vec4 textureColour = texture(textureSampler, vec2(xUv, yUv));

    float visibility = 1;
    if (distance > viewDistance - 64) {
        visibility = (distance - viewDistance + 32) / 32.0;
        visibility = clamp(1 - visibility, 0.0, 1.0);
    }

    if (inWater != 0) {
        textureColour *= vec4(0.5, 0.5, 1, 1);
    }

    outColor = textureColour;
    outColor = mix(vec4(skyColor, 1.0), outColor, visibility);

}