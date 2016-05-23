#version 330

layout (location=0) in vec3 pos;
layout (location=1) in vec3 inColor;

// projection * view
uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;

out vec3 exColor;

void main()
{
    gl_Position = projectionMatrix * modelViewMatrix * vec4(pos, 1.0);
    exColor = inColor;
}