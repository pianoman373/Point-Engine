#version 330 core

layout (location = 0) out vec4 outColor;
layout (location = 1) out vec4 brightColor;

uniform vec3 color;

void main() {
	outColor = vec4(color * 4, 1.0f);
	brightColor = vec4(color * 2, 1);
}
