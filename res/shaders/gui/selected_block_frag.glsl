#version 400 core

in vec4 passTexture;

out vec4 outColor;

uniform sampler2D textureSampler;

const int atlasSize = 8192;
const int textureSize = 32;
const int texturesPerSide = atlasSize / textureSize;

void main() {

    float xUv = mod(passTexture.z, texturesPerSide) / texturesPerSide + mod(passTexture.x, 1.0) / texturesPerSide;
    float yUv = int(passTexture.z / texturesPerSide) / texturesPerSide + mod(passTexture.y, 1.0) / texturesPerSide;

    vec4 color = texture(textureSampler, vec2(xUv, yUv));

    if (color.a == 0)
      discard;

    outColor = color;

}