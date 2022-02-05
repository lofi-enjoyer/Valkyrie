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
uniform float time;

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
const float leavesMovement = 32;

void main() {

    vec3 newPosition = vec3(position);
    if (color.z == 3) {
        newPosition.x += sin(position.y + time) / leavesMovement;
        newPosition.z += cos(position.x + time + 2) / leavesMovement;
        newPosition.y += sin(position.z + time + 5) / leavesMovement;
    }

    vec4 worldPosition = transformationMatrix * vec4(newPosition, 1.0);
    vec4 positionRelativeToCam = viewMatrix * worldPosition;

    gl_Position = projectionMatrix * positionRelativeToCam;
    passColor = color;
    passLight = normalVectors[int(light)];

    toLightVector = vec3(0, 400, 0) - (newPosition + translation);

    float distance = length(positionRelativeToCam.xyz);
    visibility = exp(-pow((distance * fogDensity), fogGradient));
    visibility = clamp(visibility, 0.0, 1.0);
}