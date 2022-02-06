#version 400 core

in float passTime;

out vec4 outColor;

void main() {

    outColor = vec4(vec3(1), ((sin(passTime * 4) + 1) / 16) + 0.125f);

}