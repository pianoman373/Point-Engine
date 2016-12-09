#version 330 core

#define MAX_POINT_LIGHTS 100

struct Material {
	sampler2D albedoTex;
	vec3 albedo;
	bool albedoTextured;

	sampler2D normalTex;
	bool normalTextured;

	sampler2D roughnessTex;
	float roughness;
	bool roughnessTextured;

	sampler2D metallicTex;
	float metallic;
	bool metallicTextured;
};

struct DirectionalLight {
    vec3 direction;
    vec3 color;

	bool hasShadowMap;
	sampler2DShadow shadowMap;
};

struct PointLight {
    vec3 position;
    vec3 color;

    float linear;
    float quadratic;
};


in vec2 texCoord;
in vec3 fragNormal;
in vec3 fragPos;
in vec4 fragPosLightSpace;
in vec3 T;
in vec3 B;
in vec3 N;
in vec3 fragTangent;

layout (location = 0) out vec4 color;
layout (location = 1) out vec4 brightColor;

uniform Material material;
uniform DirectionalLight dirLight;
uniform int pointLightCount;
uniform PointLight[MAX_POINT_LIGHTS] pointLights;
uniform vec3 ambient;
uniform vec3 cameraPos;

uniform samplerCube skybox;
uniform bool hasSkybox;

uniform samplerCube irradiance;
uniform bool hasIrradiance;

uniform float exposure;
uniform vec3 eyePos;

vec2 poissonDisk[16] = vec2[](
   vec2( -0.94201624, -0.39906216 ),
   vec2( 0.94558609, -0.76890725 ),
   vec2( -0.094184101, -0.92938870 ),
   vec2( 0.34495938, 0.29387760 ),
   vec2( -0.91588581, 0.45771432 ),
   vec2( -0.81544232, -0.87912464 ),
   vec2( -0.38277543, 0.27676845 ),
   vec2( 0.97484398, 0.75648379 ),
   vec2( 0.44323325, -0.97511554 ),
   vec2( 0.53742981, -0.47373420 ),
   vec2( -0.26496911, -0.41893023 ),
   vec2( 0.79197514, 0.19090188 ),
   vec2( -0.24188840, 0.99706507 ),
   vec2( -0.81409955, 0.91437590 ),
   vec2( 0.19984126, 0.78641367 ),
   vec2( 0.14383161, -0.14100790 )
);

const float kPi = 3.14159265;

vec3 FresnelSchlickRoughness(float cosTheta, vec3 F0, float roughness)
{
	return F0 + (max(vec3(1.0 - roughness), F0) - F0) * pow(1.0 - cosTheta, 5.0);
}

float ShadowCalculation(vec4 fragPosLightSpace)
{
	if (dirLight.hasShadowMap) {
		float visibility = 1.0;
		float bias = 0.003;

		for (int i=0;i<4;i++){
			int index = i;
			visibility -= 0.25*(1.0-texture( dirLight.shadowMap, vec3(fragPosLightSpace.xy + poissonDisk[index]/3000.0, (fragPosLightSpace.z)/fragPosLightSpace.w-bias) ));
		}
		return clamp(visibility, 0, 1);
	}
	else {
		return 1.0;
	}
}

vec3 directionalSpecular(DirectionalLight light, vec3 normal, float roughness, float visibility) {
	//blinn phong specular
	float kShininess = (1-roughness) * 128;
	vec3 viewDir = normalize(eyePos - fragPos);

	float kEnergyConservation = ( 8.0 + kShininess ) / ( 8.0 * kPi );
	vec3 halfwayDir = normalize(-light.direction + viewDir);
	return kEnergyConservation * pow(max(dot(normal, halfwayDir), 0.0), kShininess) * light.color * dot(normal, -light.direction) * visibility;
}

vec3 directionalDiffuse(DirectionalLight light, vec3 normal, float visibility) {
	return clamp(dot(normal, -light.direction) * light.color * visibility, 0, 1000);
	//return light.color * visibility;
}

vec3 pointSpecular(PointLight light, vec3 normal, float roughness) {
	//blinn phong specular
	float kShininess = (1-roughness) * 128;
	vec3 viewDir = normalize(eyePos - fragPos);
	vec3 direction = normalize(light.position - fragPos);

	float kEnergyConservation = ( 8.0 + kShininess ) / ( 8.0 * kPi );
	vec3 halfwayDir = normalize(direction + viewDir);
	return kEnergyConservation * pow(max(dot(normal, halfwayDir), 0.0), kShininess) * light.color * max(dot(normal, direction), 0);
}

vec3 pointDiffuse(PointLight light, vec3 normal) {
	vec3 direction = normalize(light.position - fragPos);
	return clamp(max(dot(normal, direction), 0) * light.color, 0, 1000);
}

void main() {
	vec3 albedo = vec3(0, 0, 0);
	float roughness = 0;
	float metallic = 0;
	vec3 normal = vec3(0, 0, 0);

	//set up all material values from either textures of constants
	if (material.albedoTextured) {
		albedo = texture(material.albedoTex, texCoord).rgb;
	} else {
		albedo = material.albedo;
	}

	if (material.roughnessTextured) {
		roughness = texture(material.roughnessTex, texCoord).r;
	} else {
		roughness = material.roughness;
	}

	if (material.metallicTextured) {
		metallic = (texture(material.metallicTex, texCoord).r * 0.8) + 0.2;
	} else {
		metallic = material.metallic;
	}

	if (material.normalTextured) {
		mat3 TBN = mat3(T, B, N);

		normal = texture(material.normalTex, texCoord).rgb;
		normal = normalize(normal * 2.0 - 1.0);
		normal = normalize(TBN * normal);
	} else {
		normal = normalize(fragNormal);
	}

	vec3 specular = vec3(0);
	vec3 diffuse = vec3(0);

	//compute skybox powered diffuse and specular
	if (hasSkybox) {
	  vec3 I = normalize(fragPos - eyePos);
	  vec3 R = reflect(I, normal);


	  specular += textureLod(skybox, R, roughness * 10).rgb * 2.2;
	}

    if (hasIrradiance) {
	  diffuse += textureLod(irradiance, normal, 0).rgb * 2;
	}
	else {
		diffuse += ambient;
	}

	//calculate the one directional light
	float shadow = ShadowCalculation(fragPosLightSpace);
	specular += directionalSpecular(dirLight, normal, roughness, shadow);
	diffuse += directionalDiffuse(dirLight, normal, shadow);

	//calculate all point lights
	for (int i = 0; i < pointLightCount; i++) {
		if (i < MAX_POINT_LIGHTS) {
			PointLight light = pointLights[i];
			float distance = length(light.position - fragPos);

			float attenuation = 1.0f / (1.0f + light.linear * distance + light.quadratic * (distance * distance));

			diffuse += pointDiffuse(light, normal) * attenuation;
			specular += pointSpecular(light, normal, roughness) * attenuation;
		}
	}

	//output final color
	color = vec4(albedo * mix(diffuse, specular, metallic), 1.0);
	//color = vec4(albedo, 1.0);

	// Check whether fragment output is higher than threshold, if so output as brightness color
	float brightness = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
	if(brightness > 8)
		brightColor = vec4(color.rgb, 1.0);
	else
		brightColor = vec4(0, 0, 0, 1.0);
}
