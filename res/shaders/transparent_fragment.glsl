#version 400 core

in vec3 passColor;
in vec3 passLight;
in vec3 toLightVector;
in float distance;

out vec4 outColor;

uniform sampler2DArray textureSampler;
uniform float inWater;
uniform float viewDistance;

const vec3 skyColor = vec3(0.45, 0.71, 1.00);

void main() {

    vec4 textureColour = texture(textureSampler, passColor);

    float visibility = 1;
    if (distance > viewDistance - 64) {
        visibility = (distance - viewDistance + 32) / 32.0;
        visibility = clamp(1 - visibility, 0.0, 1.0);
    }

    if (inWater != 0) {
        textureColour *= vec4(0.5f, 0.5f, 1f, 1f);
    }

    outColor = textureColour;
    outColor = mix(vec4(skyColor, 1.0), outColor, visibility);

}