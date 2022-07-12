#version 460 core

layout(location = 0) in vec2 aPosition;
layout(location = 1) in vec3 aTexCoords; //Or color
layout(location = 2) in float aTextureMap;
layout(location = 3) in float aIsUI;

uniform mat4 view;
uniform mat4 projection;

out float textureMap;
out vec2 position;
out vec3 texCoords;
out float isColor;
out float isUI;

void main(){

    if(aIsUI < 0.5){
        gl_Position = projection * view * vec4(aPosition, 0, 1);
    }
    else{
        gl_Position = projection * vec4(aPosition, 0, 1);
    }
    position = aPosition;
    texCoords = aTexCoords;
    textureMap = aTextureMap;
    isUI = aIsUI;
}
