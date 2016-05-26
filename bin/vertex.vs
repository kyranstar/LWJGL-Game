#version 330

layout (location=0) in vec3 pos;
layout (location=1) in vec3 inColor;
layout (location=2) in vec3 normal;

// projection * view
uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;

out vec3 exColor;
out vec3 mvVertexNormal;
out vec3 mvVertexPos;

void main()
{
	vec4 mvPos = modelViewMatrix * vec4(pos, 1.0);
    gl_Position =  projectionMatrix * mvPos;
    mvVertexPos = mvPos.xyz;
    mvVertexNormal = normalize(modelViewMatrix * vec4(mvVertexNormal, 0.0)).xyz;
    exColor = inColor;
}