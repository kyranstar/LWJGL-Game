#version 330

layout (location=0) in vec3 pos;
layout (location=1) in vec3 inColor;

// projection * view
uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;
uniform float time;

out vec3 exColor;
out vec3 mvVertexPos;
out vec4 clipSpace;

#define M_2PI 6.283185307179586476925286766559

vec3 generateWavePos()
{
	//  amplitude * sin(dot(direction, (x, y)) * (2 * pi/wavelength) + time * (speed * (2* pi/wavelength)))
	float wave1 = .003 * sin(dot(vec2(.8,.3), vec2(pos.x, pos.z)) * (M_2PI/.02) + time * (.005 * (M_2PI/.02)));
	float wave2 = .003 * sin(dot(vec2(.2,.6), vec2(pos.x, pos.z)) * (M_2PI/.02) + time * (.001 * (M_2PI/.02)));
	float wave3 = .002 * sin(dot(vec2(.4,.1), vec2(pos.x, pos.z)) * (M_2PI/.2) + time * (.01 * (M_2PI/.2)));
	float height = wave1 + wave2 + wave3;
	return vec3(pos.x, pos.y + height, pos.z);
}

void main()
{
	vec3 newPos = generateWavePos();

	vec4 mvPos = modelViewMatrix * vec4(newPos, 1.0);
	clipSpace = projectionMatrix * mvPos;
    gl_Position =  clipSpace;
    mvVertexPos = mvPos.xyz;
    exColor = inColor;
}

