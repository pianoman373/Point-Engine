#version 330 core
layout (location = 0) in vec3 position;
out vec3 TexCoords;

uniform mat4 projection;
uniform mat4 view;


void main()
{
	mat4 newview = view;
	newview[3][0] = 0;
	newview[3][1] = 0;
	newview[3][2] = 0;

    vec4 pos = projection * newview * vec4(position, 1.0);
    gl_Position = pos;
    TexCoords = position;
}
