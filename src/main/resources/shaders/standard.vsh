#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 uv;

out vec2 texCoord;
out vec3 fragNormal;
out vec3 fragPos;
out vec4 fragPosLightSpace;
out vec3 viewPos;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform mat4 lightSpace;

uniform vec3 eyePos;

void main() {
	gl_Position = projection * view * model * vec4(position, 1.0);

	fragPos = vec3(model * vec4(position, 1.0f));
	fragNormal = mat3(transpose(inverse(model))) * normal;
	texCoord = vec2(uv.x, 1 - uv.y);

	mat4 biasMatrix = mat4(
		0.5, 0.0, 0.0, 0.0,
		0.0, 0.5, 0.0, 0.0,
		0.0, 0.0, 0.5, 0.0,
		0.5, 0.5, 0.5, 1.0
	);

	fragPosLightSpace = biasMatrix * lightSpace * vec4(fragPos, 1.0);

	viewPos = eyePos;
}
