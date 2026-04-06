#version 150

uniform float AnimTime;

in vec2 localPos;
in vec4 vertexColor;
out vec4 fragColor;

mat2 rot2d(float a) {
    float s = sin(a), c = cos(a);
    return mat2(c, -s, s, c);
}

float hash12(vec2 p) {
    vec3 p3  = fract(vec3(p.xyx) * .1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

float noise2D(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(hash12(i), hash12(i + vec2(1.0, 0.0)), f.x),
    mix(hash12(i + vec2(0.0, 1.0)), hash12(i + vec2(1.0, 1.0)), f.x), f.y);
}

float fbm2D(vec2 p) {
    float v = 0.0, a = 0.5;
    for (int i = 0; i < 4; i++) {
        v += a * noise2D(p); p *= 2.0; a *= 0.5;
    }
    return v;
}

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

float ridge3D(vec3 p) {
    float v = 0.0, a = 0.5;
    for (int i = 0; i < 4; i++) {
        float n = 1.0 - abs(noise3D(p) - 0.5) * 2.0;
        v += a * n;
        p *= 2.0;
        a *= 0.5;
    }
    return v;
}

void main() {
    vec2 uv = localPos;
    float time = AnimTime;

    float macroWarp = sin(uv.y * 2.5 - time * 5.0) * 0.15
    + cos(uv.y * 1.5 + time * 3.0) * 0.1;
    float microWarp = (fbm2D(vec2(uv.y * 5.0, time * 15.0)) - 0.5) * 0.2;

    float localX = uv.x + macroWarp + microWarp;
    float localDist = abs(localX);

    float beamCore = exp(-localDist * 22.0);

    vec3 noisePos = vec3(localX * 7.0, uv.y * 1.5 - time * 15.0, time * 1.5);
    float ridgeTexture = ridge3D(noisePos);
    ridgeTexture = pow(ridgeTexture, 2.5);

    float intensity = beamCore * ridgeTexture * 4.5;

    float erosionNoise = fbm2D(vec2(localX * 3.0, uv.y * 1.5 - time * 8.0));
    float erosion = smoothstep(0.35, 0.8, erosionNoise);
    intensity -= erosion * 2.5;
    intensity = max(0.0, intensity);

    float verticalFade = smoothstep(1.0, 0.6, abs(uv.y));
    intensity *= verticalFade;

    vec3 colWhite = vec3(1.0, 1.0, 0.9);
    vec3 colYellow = vec3(1.0, 0.8, 0.0);
    vec3 colOrange = vec3(1.0, 0.3, 0.0);
    vec3 colRed = vec3(0.6, 0.0, 0.0);

    vec3 baseColor = vec3(0.0);

    if (intensity < 0.1) {
        baseColor = mix(vec3(0.0), colRed, smoothstep(0.0, 0.1, intensity));
    } else if (intensity < 0.3) {
        baseColor = mix(colRed, colOrange, smoothstep(0.1, 0.3, intensity));
    } else if (intensity < 0.7) {
        baseColor = mix(colOrange, colYellow, smoothstep(0.3, 0.7, intensity));
    } else {
        baseColor = mix(colYellow, colWhite, smoothstep(0.7, 1.0, intensity));
    }

    float bloomMask = exp(-localDist * 6.0) * verticalFade * (1.0 - erosion * 0.5);
    vec3 bloomColor = colOrange * bloomMask * 0.7;

    vec3 finalEmission = (baseColor * 3.5) + bloomColor;

    float alpha = smoothstep(0.01, 0.15, intensity) + (bloomMask * 0.4);
    alpha = clamp(alpha, 0.0, 1.0);

    vec3 preMultipliedRGB = finalEmission * vertexColor.rgb * vertexColor.a;
    float preMultipliedAlpha = alpha * vertexColor.a;

    if (length(preMultipliedRGB) <= 0.01 && preMultipliedAlpha <= 0.01) {
        discard;
    }

    fragColor = vec4(preMultipliedRGB, preMultipliedAlpha);
}