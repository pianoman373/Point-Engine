#version 330 core
out vec4 color;

in vec3 FragPos;
in vec3 Normal;

uniform vec3 eyePos;


void main()
{
    // Ambient
    float ambientStrength = 0.1f;
    vec3 ambient = ambientStrength * vec3(1);

    // Diffuse
    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(vec3(100, 10, 0) - FragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * vec3(1);

    // Specular
    float specularStrength = 1.0f;
    vec3 viewDir = normalize(eyePos - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 128);
    vec3 specular = specularStrength * spec * vec3(1);

    vec3 result = (ambient + diffuse + specular) * vec3(0.5);
    color = vec4(result, 1.0f);
}
