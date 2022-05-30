#version 330 core

layout(location = 0) in vec2 aPosition;
layout(location = 1) in vec2 aTexCoords;
layout(location = 2) in float aTextureMap;
uniform mat4 view;
uniform mat4 projection;

out float textureMap;
out vec2 texCoords;

void main(){
    gl_Position = projection * view * vec4(aPosition, 0, 1);
    texCoords = aTexCoords;
    textureMap = aTextureMap;
}
