#version 440 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector;
in vec3 toCameraVector;
in float visibility;

out vec4 out_Color;

uniform sampler2D textureSampler;
uniform vec3 lightColor;
uniform float shineDampener;
uniform float reflectivity;
uniform vec3 skyColor;

void main(void) {

    vec3 unitNormal = normalize(surfaceNormal);
    vec3 lightNormal = normalize(toLightVector);
    vec3 normalCamera = normalize(toCameraVector);

    float nDotl = dot(unitNormal, lightNormal);
    float brightness = max(nDotl, 0.2);
    vec3 diffuse = brightness * lightColor;
    vec3 lightDirection = -normalCamera;
    vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
    float specularFactor = dot(reflectedLightDirection, normalCamera);
    specularFactor = max(specularFactor, 0);
    float dampedFactor = pow(specularFactor, shineDampener);
    vec3 finalSpecular = vec3(0, 0, 0);

    if (nDotl > -5.05) {
        finalSpecular = dampedFactor * lightColor * reflectivity;
    }

    vec4 textureColour = texture(textureSampler, pass_textureCoords);

    if (textureColour.a < 0.5) {
        discard;
    }

    out_Color = vec4(diffuse, 1.0) * textureColour + vec4(finalSpecular, 0);
    out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);

}
