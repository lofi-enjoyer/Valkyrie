#version 400 core

#define PI 3.14159265359

in vec3 position;
in vec3 color;
in float light;

out vec3 passColor;
out vec3 passLight;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 cameraPosition;
uniform float time;
uniform float viewDistance;
uniform int leavesId;
uniform int waterId;
uniform float inWater;

const vec3[6] normalVectors = vec3[6](
    vec3( 1,  0,  0),
    vec3(-1,  0,  0),
    vec3( 0,  1,  0),
    vec3( 0, -1,  0),
    vec3( 0,  0,  1),
    vec3( 0,  0, -1)
);

out vec3 toLightVector;
out float distance;

const float leavesMovement = 32;
const float waterSpeed = 0.25;

void main() {

    vec3 newPosition = vec3(position);
    if (color.z == leavesId) {
        newPosition.x += sin(position.y + time) / leavesMovement;
        newPosition.z += cos(position.x + time + 2) / leavesMovement;
        newPosition.y += sin(position.z + time + 5) / leavesMovement;
    }

    if (color.z == waterId && light == 2) {
        newPosition.y -= 0.1875f;
        newPosition.y += (sin(((position.x + 16.0 * time * waterSpeed) / 16.0) * PI * 2)) * sin(((position.z + 16.0 * time * waterSpeed) / 8.0) * PI * 2) * 0.0625f;
    }

    vec4 worldPosition = transformationMatrix * vec4(newPosition, 1.0);
    vec4 positionRelativeToCam = viewMatrix * worldPosition;

    gl_Position = projectionMatrix * positionRelativeToCam;
    passColor = color;
    passLight = normalVectors[int(light)];

    toLightVector = vec3(0, 400, 0) - (newPosition);

    distance = length(cameraPosition.xz - worldPosition.xz);
}