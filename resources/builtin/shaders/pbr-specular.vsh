#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 vertexNormal;
layout (location = 2) in vec2 uv;
layout (location = 3) in vec3 tangent;

out vec2 texCoord;
out vec3 fragNormal;
out vec3 fragPos;
out vec4 fragPosLightSpace;
out vec3 T;
out vec3 B;
out vec3 N;

struct DirectionalLight {
    vec3 direction;
    vec3 color;

	bool hasShadowMap;
	sampler2DShadow shadowMap;
};

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform DirectionalLight dirLight;
uniform mat4 lightSpace;

uniform vec3 eyePos;

void main() {
	gl_Position = projection * view * model * vec4(position, 1.0);

	fragPos = vec3(model * vec4(position, 1.0f));
	fragNormal = mat3(transpose(inverse(model))) * vertexNormal;
	texCoord = vec2(uv.x, uv.y);

	T = normalize(vec3(model * vec4(tangent,   0.0)));
    N = normalize(vec3(model * vec4(vertexNormal,    0.0)));
	B = normalize(cross(T, N));

	if (dirLight.hasShadowMap) {
		mat4 biasMatrix = mat4(
			0.5, 0.0, 0.0, 0.0,
			0.0, 0.5, 0.0, 0.0,
			0.0, 0.0, 0.5, 0.0,
			0.5, 0.5, 0.5, 1.0
		);

		fragPosLightSpace = biasMatrix * lightSpace * vec4(fragPos, 1.0);
	}
}
