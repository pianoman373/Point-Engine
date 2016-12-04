#version 330 core

layout (location = 0) out vec4 color;
layout (location = 1) out vec4 brightColor;

uniform vec3 lightColor;
uniform vec3 eyePos;

in vec3 fColor;

void main() {
	color = vec4(fColor * 3, 1.0);
	brightColor = vec4(fColor * 3, 1.0);
}
