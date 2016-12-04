#version 330 core
in vec2 TexCoords;
in vec2 screenPos;
out vec4 color;

uniform sampler2D screenTexture;
uniform sampler2D bloomTexture1;
uniform sampler2D bloomTexture2;
uniform sampler2D bloomTexture3;
uniform float exposure;
uniform bool doBloom;

float A = 0.2;
float B = 0.3;
float C = 0.09;
float D = 0.3;
float E = 0.024;
float F = 0.4;

vec3 Uncharted2Tonemap(vec3 x) {

	/*--------------------------------*/
	return ((x*(A*x+C*B)+D*E)/(x*(A*x+B)+D*F))-E/F;
}

float vignette() {
    float dist = distance(TexCoords, vec2(0.5)) * 2.0;
    dist /= 1.5142;

    dist = pow(dist, 1.1);

    return 1.0 - dist;
}

void main()
{
    vec3 hdrColor = texture(screenTexture, TexCoords).rgb;
    vec4 combined;
    vec3 bloomColor = vec3(0);
    if (doBloom) {
        bloomColor = texture(bloomTexture1, TexCoords).rgb + texture(bloomTexture2, TexCoords).rgb + texture(bloomTexture3, TexCoords).rgb;
    }

    color = vec4(Uncharted2Tonemap(hdrColor) + bloomColor, 1.0);
}
