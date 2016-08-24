#version 330 core
in vec2 TexCoords;
out vec4 color;

uniform sampler2D screenTexture;

void main()
{
    color = vec4(texture(screenTexture, TexCoords.st));

    //float depth = vec4(texture(screenTexture, TexCoords.st)).r;
    //color = vec4(vec3(depth), 1.0);
}
