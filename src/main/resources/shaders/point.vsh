#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 color;
//layout (location = 2) in vec2 texCoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

out vec3 fragColor;
out vec3 fragPos;

void main() {
	gl_Position = projection * view * model * vec4(position, 1.0);
	fragPos = vec3(model * vec4(position, 1.0f));
  	fragColor = color;
}
