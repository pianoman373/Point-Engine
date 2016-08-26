#version 330 core
in vec3 TexCoords;

uniform samplerCube skybox;

layout (location = 0) out vec4 color;
layout (location = 1) out vec4 brightColor;

void main()
{    
    color = texture(skybox, TexCoords);

    brightColor = vec4(0, 0, 0, 1.0f);
}
  
