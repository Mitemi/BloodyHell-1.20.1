#version 150

uniform float AnimTime;

in vec2 localPos;
in vec4 vertexColor;
out vec4 fragColor;

float hash(vec3 p) {
    p  = fract(p * .1031);
    p += dot(p, p.zyx + 31.32);
    return fract((p.x + p.y) * p.z);
}

float noise3D(vec3 p) {
    vec3 i = floor(p);
    vec3 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);

    return mix(mix(mix( hash(i + vec3(0,0,0)), hash(i + vec3(1,0,0)), f.x),
    mix( hash(i + vec3(0,1,0)), hash(i + vec3(1,1,0)), f.x), f.y),
    mix(mix( hash(i + vec3(0,0,1)), hash(i + vec3(1,0,1)), f.x),
    mix( hash(i + vec3(0,1,1)), hash(i + vec3(1,1,1)), f.x), f.y), f.z);
}

float fbm(vec3 p) {
    float v = 0.0, a = 0.5;
    mat3 rot = mat3(0.00, 0.80, 0.60, -0.80, 0.36, -0.48, -0.60, -0.48, 0.64);
    for (int i = 0; i < 4; i++) {
        v += a * noise3D(p);
        p = rot * p * 2.0;
        a *= 0.5;
    }
    return v;
}

void main() {
    vec2 uv = localPos;
    float time = AnimTime;

    float aggregatedIntensity = 0.0;
    const int NUM_BEAMS = 4;
    const float PI = 3.14159265;

    for (int i = 0; i < NUM_BEAMS; i++) {
        float fi = float(i);

        float phaseOffset = fi * (PI / 2.0);
        float twistFrequency = uv.y * 6.0;
        float twistSpeed = time * 5.0;

        float pathNoise = (fbm(vec3(uv.y * 3.0, fi * 2.5, time * 0.8)) - 0.5) * 0.25;
        float xOffset = sin(twistFrequency - twistSpeed + phaseOffset) * 0.35 + pathNoise;

        float currentLocalX = uv.x + xOffset;
        float currentDistToPath = abs(currentLocalX);

        float currentCore = exp(-currentDistToPath * 45.0);

        float flowOffset = fi * 3.1 + time * 12.0;
        vec3 currentFlowCoord = vec3(currentLocalX * 5.0, uv.y * 2.0 - flowOffset, time * 0.8);
        float currentPlasmaFlow = fbm(currentFlowCoord);

        float currentAura = exp(-currentDistToPath * 15.0) * currentPlasmaFlow * 1.8;

        float depthMultiplier = cos(twistFrequency - twistSpeed + phaseOffset) * 0.5 + 0.8;

        aggregatedIntensity += (currentCore + currentAura) * depthMultiplier;
    }

    float mask = smoothstep(1.0, 0.7, abs(uv.y));
    aggregatedIntensity *= mask;

    float intensity = min(1.5, aggregatedIntensity * 0.5);

    vec3 colDarkRed = vec3(0.5, 0.0, 0.0);
    vec3 colBrightRed = vec3(1.0, 0.1, 0.0);
    vec3 colOrange = vec3(1.0, 0.5, 0.0);
    vec3 colYellow = vec3(1.0, 0.9, 0.1);
    vec3 colWhiteCore = vec3(1.0, 1.0, 0.9);

    vec3 baseColor = vec3(0.0);

    if (intensity < 0.2) {
        baseColor = mix(vec3(0.0), colDarkRed, smoothstep(0.0, 0.2, intensity));
    } else if (intensity < 0.5) {
        baseColor = mix(colDarkRed, colBrightRed, smoothstep(0.2, 0.5, intensity));
    } else if (intensity < 0.8) {
        baseColor = mix(colBrightRed, colOrange, smoothstep(0.5, 0.8, intensity));
    } else if (intensity < 1.1) {
        baseColor = mix(colOrange, colYellow, smoothstep(0.8, 1.1, intensity));
    } else {
        baseColor = mix(colYellow, colWhiteCore, smoothstep(1.1, 1.5, intensity));
    }

    vec3 finalEmission = baseColor * 2.5;

    float alphaIntensity = aggregatedIntensity * 0.4;
    float alpha = smoothstep(0.0, 0.15, alphaIntensity);
    alpha = clamp(alpha * mask, 0.0, 1.0);

    vec3 preMultipliedRGB = finalEmission * vertexColor.rgb * vertexColor.a;
    float preMultipliedAlpha = alpha * vertexColor.a;

    if (length(preMultipliedRGB) <= 0.01 && preMultipliedAlpha <= 0.01) {
        discard;
    }

    fragColor = vec4(preMultipliedRGB, preMultipliedAlpha);
}