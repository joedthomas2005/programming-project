#version 330 core

uniform sampler2D[8] aTexture;
in float textureMap;
in vec2 texCoords;

out vec4 FragColor;

void main(){
    FragColor = texture(aTexture[int(textureMap)], texCoords);
}