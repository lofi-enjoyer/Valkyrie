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

    float shadow = passLight;

    vec4 textureColour = texture(textureSampler, passColor);
    if (textureColour.a < 1) {
        if (textureColour.a < 0.5) discard;
        shadow = 1;
    }
    outColor = textureColour * shadow;

}