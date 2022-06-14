#version 330 core

uniform sampler2D[8] aTexture;
in float textureMap;
in vec3 texCoords;
out vec4 FragColor;

void main(){
    if(textureMap >= 0.0){
        FragColor = texture(aTexture[int(textureMap)], vec2(texCoords.x, texCoords.y));
    }
    else{
        FragColor = vec4(texCoords, 1);
    }
}