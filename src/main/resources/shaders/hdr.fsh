#version 330 core
in vec2 TexCoords;
out vec4 color;

uniform sampler2D screenTexture;
uniform sampler2D bloomTexture;
uniform float exposure;

const float blurSizeH = 1.0 / 300.0;
const float blurSizeV = 1.0 / 200.0;

void main()
{
    vec3 hdrColor = texture(screenTexture, TexCoords).rgb;
    vec3 bloomColor = texture(bloomTexture, TexCoords).rgb;
    vec3 combined = hdrColor + bloomColor; // additive blending

    vec4 sum = vec4(0.0);
    for (int x = -4; x <= 4; x++)
        for (int y = -4; y <= 4; y++)
            sum += texture(
                screenTexture,
                vec2(TexCoords.x + x * blurSizeH, TexCoords.y + y * blurSizeV)
            ) / 81.0;
    vec4 outColor = sum;

    // reinhard
    // vec3 result = hdrColor / (hdrColor + vec3(1.0));
    // exposure
    vec3 result = vec3(1.0) - exp(-combined * exposure);
    color = vec4(result, 1.0f);
    //color = outColor;
}  

