#version 460 core

struct light{
    vec2 position;
    vec3 color;
    float intensity;
};

uniform sampler2D[8] uTexture;
uniform light[10] uLights;
uniform vec4 ambient;
in float textureMap;
in vec3 texCoords;
in vec2 position;
in float isUI;
out vec4 FragColor;

vec4 getColor(vec2 vertexPosition, vec4 unlitColor, vec3 lightColor, float lightIntensity, vec2 lightPosition){
    float distance = sqrt(pow(vertexPosition.x - lightPosition.x, 2) + pow(vertexPosition.y - lightPosition.y, 2));
    vec3 finalColour = unlitColor.xyz * lightColor;
    //0.01 is just the best coefficient for distance I've found
    //and the +1 is to prevent the intensity being infinite at distance = 0
    return vec4(finalColour * lightIntensity * (1.0/(0.01 * distance + 1)), unlitColor.w);
}

void main(){
    vec4 unlit;
    if(textureMap >= 0.0){
        unlit = texture(uTexture[int(textureMap)], vec2(texCoords.x, texCoords.y));
    }
    else{
        unlit = vec4(texCoords, 1);
    }
    if(isUI < 0.5){

        vec4 litColor = unlit * vec4(ambient.xyz, 1) * ambient.w;
        for(int i = 0; i < 10; i++){
            if(uLights[i].intensity != 0.0){
                litColor += getColor(position, unlit, uLights[i].color, uLights[i].intensity, uLights[i].position);
            }
        }
        FragColor = litColor;
    } else{
        FragColor = unlit;
    }
}

