#version 330 core
out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D fontTexture;

void main()
{
    vec2 dims = textureSize(fontTexture, 0);
    FragColor = texture(fontTexture, vec2(TexCoords.x, TexCoords.y));
}