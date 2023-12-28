#version 330 core
layout (location = 0) in vec2 aPos;
layout (location = 1) in vec2 aTexCoords;

out vec2 TexCoords;

uniform mat4 transformationMatrix;
uniform mat4 projMatrix;
uniform sampler2D fontTexture;

void main()
{
    gl_Position = projMatrix * transformationMatrix * vec4(aPos.x, -aPos.y, 0.0, 1.0);
    TexCoords = aTexCoords;
}