#version 400 core

in vec3 passColor;
in float passLight;

out vec4 outColor;

uniform sampler2DArray textureSampler;

const vec3[6] normalVectors = vec3[6](
    vec3( 1,  0,  0),
    vec3(-1,  0,  0),
    vec3( 0,  1,  0),
    vec3( 0, -1,  0),
    vec3( 0,  0,  1),
    vec3( 0,  0, -1)
);

void main() {

    outColor = texture(textureSampler, passColor) * passLight;

}