#version 330 core

uniform sampler2D[8] aTexture;
in float textureMap;
in vec3 texCoords;
in vec2 position;
out vec4 FragColor;

void main(){
    if(textureMap >= 0.0){
        if(textureMap == 0){
            FragColor = texture(aTexture[0], vec2(texCoords.x, texCoords.y));
        }
        else if(textureMap == 1){
            FragColor = texture(aTexture[1], vec2(texCoords.x, texCoords.y));
        }
        else if(textureMap == 2) {
            FragColor = texture(aTexture[2], vec2(texCoords.x, texCoords.y));
        }
        else if(textureMap == 3){
            FragColor = texture(aTexture[3], vec2(texCoords.x, texCoords.y));
        }
        else if(textureMap == 4){
            FragColor = texture(aTexture[4], vec2(texCoords.x, texCoords.y));
        }
        else if(textureMap == 5){
            FragColor = texture(aTexture[5], vec2(texCoords.x, texCoords.y));
        }
        else if(textureMap == 6){
            FragColor = texture(aTexture[6], vec2(texCoords.x, texCoords.y));
        }
        else{
            FragColor = texture(aTexture[7], vec2(texCoords.x, texCoords.y));
        }
    }
    else{
        FragColor = vec4(texCoords, 0);
    }
}