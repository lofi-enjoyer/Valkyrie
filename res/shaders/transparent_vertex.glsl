#version 400 core

in vec3 position;
in vec3 color;
in float light;

out vec3 passColor;
out vec3 passLight;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 translation;

const vec3[6] normalVectors = vec3[6](
    vec3( 1,  0,  0),
    vec3(-1,  0,  0),
    vec3( 0,  1,  0),
    vec3( 0, -1,  0),
    vec3( 0,  0,  1),
    vec3( 0,  0, -1)
);

out vec3 toLightVector;
out float visibility;

const float fogDensity = 0.002;
const float fogGradient = 50;

void main() {

    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    vec4 positionRelativeToCam = viewMatrix * worldPosition;

    gl_Position = projectionMatrix * positionRelativeToCam;
    passColor = color;
    passLight = normalVectors[int(light)];

    toLightVector = vec3(0, 400, 0) - (position + translation);

    float distance = length(positionRelativeToCam.xyz);
    visibility = exp(-pow((distance * fogDensity), fogGradient));
    visibility = clamp(visibility, 0.0, 1.0);
}