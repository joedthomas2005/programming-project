#version 330 core

uniform sampler2D[8] aTexture;
in float textureMap;
in vec3 texCoords;
in float isColor;
out vec4 FragColor;

void main(){
    if(isColor < 0.5){
        FragColor = texture(aTexture[int(textureMap)], vec2(texCoords.x, texCoords.y));
    }
    else{
        FragColor = vec4(texCoords, 1);
    }
}