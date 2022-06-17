#version 330 core

uniform sampler2D[8] aTexture;
uniform vec2 lightPosition;
uniform vec3 lightColour;
in float textureMap;
in vec3 texCoords;
in vec2 position;
in float isUI;
out vec4 FragColor;

vec4 getColour(vec2 vertexPosition, vec4 unlitColor, vec3 lightColour, float lightIntensity, vec2 lightPosition){
    float distance = sqrt(pow(vertexPosition.x - lightPosition.x, 2) + pow(vertexPosition.y - lightPosition.y, 2));
    vec3 finalColour = unlitColor.xyz * lightColour;
    //0.01 is just the best coefficient for distance I've found
    //and the +1 is to prevent the intensity being infinite at distance = 0
    return vec4(finalColour * lightIntensity * (1.0/(0.01 * distance + 1)), unlitColor.w);
}

void main(){
    vec4 unlit;
    if(textureMap >= 0.0){
        unlit = texture(aTexture[int(textureMap)], vec2(texCoords.x, texCoords.y));
    }
    else{
        unlit = vec4(texCoords, 1);
    }
    if(isUI < 0.5){
        FragColor = getColour(position, unlit, vec3(1, 0, 0), 2, vec2(300, 300))
        + getColour(position, unlit, vec3(0, 0, 1), 2, vec2(-300, -300));
    } else{
        FragColor = unlit;
    }
}
