#version 400 core

in vec3 passTexture;

out vec4 outColor;

uniform sampler2DArray textureSampler;

void main() {

    outColor = texture(textureSampler, passTexture);

}