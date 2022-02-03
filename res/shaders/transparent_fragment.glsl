#version 400 core

in vec3 passColor;
in vec3 passLight;
in vec3 toLightVector;
in float visibility;

out vec4 outColor;

uniform sampler2DArray textureSampler;

const vec3 skyColor = vec3(0.45, 0.71, 1.00);

void main() {

    vec4 textureColour = texture(textureSampler, passColor);

    outColor = textureColour;
    outColor = mix(vec4(skyColor, 1.0), outColor, visibility);

}