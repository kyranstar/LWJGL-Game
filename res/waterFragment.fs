#version 330

in vec3 exColor;
in vec3 mvVertexPos;
in vec4 clipSpace;

out vec4 fragColor;

struct Attenuation
{
    float constant;
    float linear;
    float exponent;
};

struct PointLight
{
    vec3 colour;
    // Light position is assumed to be in view coordinates
    vec3 position;
    float intensity;
    Attenuation att;
};
struct DirectionalLight
{
    vec3 colour;
    vec3 direction;
    float intensity;
};

const int MAX_POINT_LIGHTS = 5;

uniform vec3 ambientLight;
uniform float specularPower;
uniform float reflectance;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform DirectionalLight directionalLight;
uniform mat4 modelViewMatrix;
uniform sampler2D refractTex;
uniform sampler2D reflectTex;

vec4 calcLightColour(vec3 light_colour, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal)
{
    vec4 diffuseColour = vec4(0, 0, 0, 0);
    vec4 specColour = vec4(0, 0, 0, 0);

    // Diffuse Light
    float diffuseFactor = max(dot(normal, to_light_dir), 0.0);
    diffuseColour = vec4(light_colour, 1.0) * light_intensity * diffuseFactor;

    // Specular Light
    // camera position is (0,0,0) in model view space
    vec3 camera_direction = normalize(vec3(0,0,0) - position);
    vec3 from_light_dir = -to_light_dir;
    vec3 reflected_light = normalize(reflect(from_light_dir , normal));
    float specularFactor = max( dot(camera_direction, reflected_light), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specColour = light_intensity  * specularFactor * reflectance * vec4(light_colour, 1.0);

    return (diffuseColour + specColour);
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal)
{
    vec3 light_direction = light.position - position;
    vec3 to_light_dir  = normalize(light_direction);
    vec4 light_colour = calcLightColour(light.colour, light.intensity, position, to_light_dir, normal);

    // Apply Attenuation
    float distance = length(light_direction);
    float attenuationInv = light.att.constant + light.att.linear * distance +
        light.att.exponent * distance * distance;
    return light_colour / attenuationInv;
}
vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal)
{
    return calcLightColour(light.colour, light.intensity, position, normalize(light.direction), normal);
}

void main()
{
	vec3 normal = normalize(cross(dFdx(mvVertexPos), dFdy(mvVertexPos)));
	
	vec2 ndc = (clipSpace.xy/clipSpace.w)/2 + 0.5;
	vec2 reflectCoord = vec2(ndc.x, -ndc.y);
	
	vec4 reflectColor = texture(reflectTex, reflectCoord);
	vec4 refractColor = texture(refractTex, ndc);
    vec4 baseColour = mix(refractColor, reflectColor, 0.5);

    vec4 totalLight = vec4(ambientLight, 1.0);
    for (int i = 0; i < MAX_POINT_LIGHTS; i++)
	{
    	if ( pointLights[i].intensity > 0 )
    	{
    	    totalLight += calcPointLight(pointLights[i], mvVertexPos, normal); 
    	}
	}
   	totalLight += calcDirectionalLight(directionalLight, mvVertexPos, normal); 

    fragColor = baseColour * totalLight;
}
	