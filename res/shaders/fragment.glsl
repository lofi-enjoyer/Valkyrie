#version 400 core

in vec3 passColor;
in vec3 passLight;
in vec3 toLightVector;
in float distance;
in float faceLight;
in float passViewDistance;
in float passInWater;

uniform sampler2DArray textureSampler;

const vec3 skyColor = vec3(0.45, 0.71, 1.00);

void main() {

    vec4 textureColour = texture(textureSampler, passColor);

    float visibility = 1;
    if (distance > passViewDistance - 64) {
        visibility = (distance - passViewDistance + 32) / 32.0;
        visibility = clamp(1 - visibility, 0.0, 1.0);
    }

    if (passInWater != 0) {
        textureColour *= vec4(0.5f, 0.5f, 1f, 1f);
    }

    gl_FragColor = textureColour * faceLight;
    gl_FragColor = mix(vec4(skyColor, 1.0), gl_FragColor, visibility);

}