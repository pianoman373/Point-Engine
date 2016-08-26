#version 330 core

#define MAX_POINT_LIGHTS 100

struct Material {
	sampler2D diffuse;
	vec3 diffuseColor;
	bool diffuseTextured;

	sampler2D specular;
	vec3 specularColor;
	bool specularTextured;

	float shininess;
};

struct DirectionalLight {
    vec3 direction;
    vec3 color;
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
in vec3 viewPos;
in vec4 fragPosLightSpace;

layout (location = 0) out vec4 color;
layout (location = 1) out vec4 brightColor;

uniform Material material;
uniform DirectionalLight dirLight;
uniform int pointLightCount;
uniform PointLight[MAX_POINT_LIGHTS] pointLights;
uniform float ambient;
uniform vec3 cameraPos;
uniform samplerCube skybox;
uniform float exposure;
uniform vec3 eyePos;
uniform sampler2DShadow shadowMap;

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

// Returns a random number based on a vec3 and an int.
float random(vec3 seed, int i){
	vec4 seed4 = vec4(seed,i);
	float dot_product = dot(seed4, vec4(12.9898,78.233,45.164,94.673));
	return fract(sin(dot_product) * 43758.5453);
}

float ShadowCalculation(vec4 fragPosLightSpace)
{
    // Get closest depth value from light's perspective (using [0,1] range fragPosLight as coords)
    //float closestDepth = texture(shadowMap, vec3(fragPosLightSpace.xy, (fragPosLightSpace.z)/fragPosLightSpace.w-0.0005));
    // Get depth of current fragment from light's perspective
	float visibility = 1.0;
	float bias = 0.0009;

	for (int i=0;i<4;i++){
		// use either :
		//  - Always the same samples.
		//    Gives a fixed pattern in the shadow, but no noise
		int index = i;
		//  - A random sample, based on the pixel's screen location.
		//    No banding, but the shadow moves with the camera, which looks weird.
		//int index = int(16.0*random(gl_FragCoord.xyy, i))%16;
		//  - A random sample, based on the pixel's position in world space.
		//    The position is rounded to the millimeter to avoid too much aliasing
		// int index = int(16.0*random(floor(fragPos.xyz*1000.0), i))%16;

		// being fully in the shadow will eat up 4*0.2 = 0.8
		// 0.2 potentially remain, which is quite dark.
		visibility -= 0.2*(1.0-texture( shadowMap, vec3(fragPosLightSpace.xy + poissonDisk[index]/5000.0, (fragPosLightSpace.z)/fragPosLightSpace.w-bias) ));
	}

	/*if(projCoords.z > 1.0)
        shadow = 0.0;

    return clamp(1 - shadow, ambient, 1.0);*/

	return visibility;
}

vec3 calcDirectionalLight(DirectionalLight light, vec3 diffuseTex, vec3 specularTex) {
	float shadow = ShadowCalculation(fragPosLightSpace);

	//diffuse
	vec3 norm = normalize(fragNormal);
	vec3 lightDir = normalize(-light.direction);
	float diff = max(dot(norm, lightDir), 0.0);
	vec3 diffuse = light.color * diff * shadow;

	//specular
	vec3 viewDir = normalize(viewPos - fragPos);
	vec3 reflectDir = reflect(-lightDir, norm);
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
	vec3 specular = spec * light.color * specularTex * diff * shadow;

    vec3 result = (diffuse + specular);
    return result;
}

vec3 calcPointLight(PointLight light, vec3 diffuseTex, vec3 specularTex) {
	float distance = length(light.position - fragPos);
	float attenuation = 1.0f / (1.0f + light.linear * distance + light.quadratic * (distance * distance));

	//diffuse
	vec3 norm = normalize(fragNormal);
	vec3 lightDir = normalize(light.position - fragPos);
	float diff = max(dot(norm, lightDir), 0.0f);
	vec3 diffuse = light.color * diff;

	//specular
	vec3 viewDir = normalize(viewPos - fragPos);
	vec3 reflectDir = reflect(-lightDir, norm);
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
	vec3 specular = spec * light.color * specularTex;

    vec3 result = (diffuse + specular) * attenuation;
    return result;
}

const float blurSizeH = 1.0 / 300.0;
const float blurSizeV = 1.0 / 200.0;

void main() {
	vec3 diffuseTex = vec3(0, 0, 0);
	vec3 specularTex = vec3(0, 0, 0);

	if (material.diffuseTextured) {
		diffuseTex = texture(material.diffuse, texCoord).rgb;
	} else {
		diffuseTex = material.diffuseColor;
	}

	if (material.specularTextured) {
		specularTex = texture(material.specular, texCoord).rgb;
	} else {
		specularTex = material.specularColor;
	}

	//vec3 lightCombined = calcPointLight(pointLights[0], diffuseTex, specularTex);
	vec3 lightCombined = vec3(0, 0, 0);

	for (int i = 0; i < pointLightCount; i++) {
		if (i < MAX_POINT_LIGHTS) {
			lightCombined += calcPointLight(pointLights[i], diffuseTex, specularTex);
		}
	}

	float shadow = ShadowCalculation(fragPosLightSpace);
	lightCombined += calcDirectionalLight(dirLight, diffuseTex, specularTex);

	lightCombined = clamp(lightCombined, ambient, 100.0f);


  //color = vec4(reflectcolor, 1.0f);

  vec3 I = normalize(fragPos - eyePos);
  vec3 R = reflect(I, normalize(fragNormal));
  vec3 reflectColor = texture(skybox, R).rgb;

  diffuseTex = mix(diffuseTex, reflectColor, clamp((length(specularTex)), 0, 1));

  color = vec4(lightCombined * diffuseTex, 1.0f);
  //color = vec4(specularTex, 1.0f);
  //color = vec4(calcDirectionalLight(dirLight, diffuseTex, specularTex), 1.0f);

  // Check whether fragment output is higher than threshold, if so output as brightness color
  float brightness = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
  if(brightness > 1.5 / exposure)
  	brightColor = vec4(color.rgb, 1.0);
  else
	brightColor = vec4(0, 0, 0, 1.0);
}
