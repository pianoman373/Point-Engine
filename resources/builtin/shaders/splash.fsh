#version 330 core
in vec2 TexCoords;
out vec4 color;

uniform sampler2D screenTexture;
uniform float fade;

void main()
{
    color = vec4(texture(screenTexture, TexCoords.st).rgb * clamp(fade, 0, 1), 1);
}
