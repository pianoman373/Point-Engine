#version 330 core
in vec3 TexCoords;

uniform samplerCube skybox;

layout (location = 0) out vec4 color;
layout (location = 1) out vec4 brightColor;

float luma(vec3 color) {
	return 0.2126 * color.r + 0.7152 * color.g + 0.0722 * color.b;
}

void main()
{
    color = vec4(textureLod(skybox, TexCoords, 0).rgb * 2.2, 1);

	// Check whether fragment output is higher than threshold, if so output as brightness color
	float brightness = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
	if(brightness > 4)
		brightColor = vec4(color.rgb, 1.0);
	else
		brightColor = vec4(0, 0, 0, 1.0);
}
