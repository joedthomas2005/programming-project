#version 330 core

uniform sampler2D[8] aTexture;
uniform vec2 lightPosition;
uniform vec3 lightColour;
in float textureMap;
in vec3 texCoords;
in vec2 position;
out vec4 FragColor;

void main(){
    vec4 unlit;
    if(textureMap >= 0.0){
        unlit = texture(aTexture[int(textureMap)], vec2(texCoords.x, texCoords.y));
    }
    else{
        unlit = vec4(texCoords, 1);
    }

    FragColor = vec4(unlit.xyz * vec3(1, 1, 1) * (100/(sqrt(pow(position.x - 0, 2) + pow(position.y - 0, 2)))), unlit.w);

}