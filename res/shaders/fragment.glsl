#version 400 core

in vec3 passColor;

out vec4 outColor;

uniform sampler2DArray textureSampler;

void main() {

    outColor = texture(textureSampler, passColor);

}