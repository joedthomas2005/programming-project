#version 330 core

uniform sampler2D[8] aTexture;
in float textureMap;
in vec2 texCoords;

out vec4 FragColor;
void main(){
    if(textureMap == 0){
        FragColor = texture(aTexture[0], texCoords);
    }
    else if(textureMap == 1){
        FragColor = texture(aTexture[1], texCoords);
    }
    else if(textureMap == 2) {
        FragColor = texture(aTexture[2], texCoords);
    }
    else if(textureMap == 3){
        FragColor = texture(aTexture[3], texCoords);
    }
    else if(textureMap == 4){
        FragColor = texture(aTexture[4], texCoords);
    }
    else if(textureMap == 5){
        FragColor = texture(aTexture[5], texCoords);
    }
    else if(textureMap == 6){
        FragColor = texture(aTexture[6], texCoords);
    }
    else{
        FragColor = texture(aTexture[7], texCoords);
    }
}