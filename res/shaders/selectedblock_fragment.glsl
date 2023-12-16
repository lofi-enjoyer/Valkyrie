#version 400 core

in vec3 passTexture;

out vec4 outColor;

uniform sampler2D textureSampler;

void main() {

    float xUv = mod(passTexture.z, (1024 / 8)) / (1024 / 8.0) + mod(passTexture.x, 1.0) / (1024 / 8);
    float yUv = int(passTexture.z / (1024 / 8.0)) / (1024 / 8.0) + mod(passTexture.y, 1.0) / (1024 / 8);

    outColor = texture(textureSampler, vec2(xUv, yUv));

}