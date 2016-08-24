#version 330 core

layout (location = 0) out vec4 color;
layout (location = 1) out vec4 brightColor;

uniform vec3 lightColor;
uniform vec3 eyePos;

in vec3 fragColor;
in vec3 fragPos;

void main() {
	float distance = length(eyePos - fragPos);

	float alpha = clamp(2 * (1 / (distance * 10)), 0.3, 5.0);

	color = vec4(fragColor * 3, 1.0);
	brightColor = vec4(fragColor * 3, 1.0);
}
