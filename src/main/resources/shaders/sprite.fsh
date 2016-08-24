#version 330 core

in vec2 texCoord;

uniform sampler2D tex;

layout (location = 0) out vec4 color;

void main() {
	vec4 texColor = texture(tex, texCoord);
	
	color = texColor;
}