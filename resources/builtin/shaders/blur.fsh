#version 330 core
out vec4 FragColor;
in vec2 TexCoords;

uniform sampler2D image;
uniform vec2 offset;
uniform bool horizontal;

uniform float weight[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);

void main()
{
    vec4 c = vec4(0);
    c += 3.0 * texture2D(image, TexCoords - offset - offset);
    c += 5.0 * texture2D(image, TexCoords - offset);
    c += 6.0 * texture2D(image, TexCoords);
    c += 5.0 * texture2D(image, TexCoords + offset);
    c += 3.0 * texture2D(image, TexCoords + offset + offset);
    FragColor = vec4((c / 22.0).rgb, 1.0);
}
