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
    vec2 uv = localPos * 2.5;

    float life = vertexColor.a;
    float t = 1.0 - life;

    vec3 warpCoord = vec3(uv * (2.0 - t), AnimTime * 3.0);
    vec2 warp = vec2(
    fbm(warpCoord),
    fbm(warpCoord + vec3(5.2, 1.3, 0.0))
    );
    vec2 warpedUV = uv + (warp - 0.5) * (1.0 + t * 2.5);

    float dist = length(warpedUV);
    float radius = pow(t, 0.4) * 2.5;

    float noiseVal = fbm(vec3(warpedUV * 3.5, AnimTime * 5.0));
    float fire = noiseVal - (dist / max(0.01, radius));

    float intensity = max(0.0, fire);
    intensity = pow(intensity, 1.5) * 7.0;
    intensity *= pow(life, 1.8);

    float flash = exp(-t * 30.0) * exp(-length(uv) * 4.0) * 6.0;
    intensity += flash;

    vec3 colDarkRed = vec3(0.4, 0.0, 0.02);
    vec3 colBrightRed = vec3(1.0, 0.1, 0.0);
    vec3 colOrange = vec3(1.0, 0.5, 0.0);
    vec3 colYellow = vec3(1.0, 0.9, 0.1);
    vec3 colWhiteCore = vec3(1.0, 1.0, 0.9);

    vec3 baseColor = vec3(0.0);

    if (intensity < 0.2) {
        baseColor = mix(vec3(0.0), colDarkRed, smoothstep(0.0, 0.2, intensity));
    } else if (intensity < 0.6) {
        baseColor = mix(colDarkRed, colBrightRed, smoothstep(0.2, 0.6, intensity));
    } else if (intensity < 1.2) {
        baseColor = mix(colBrightRed, colOrange, smoothstep(0.6, 1.2, intensity));
    } else if (intensity < 2.0) {
        baseColor = mix(colOrange, colYellow, smoothstep(1.2, 2.0, intensity));
    } else {
        baseColor = mix(colYellow, colWhiteCore, smoothstep(2.0, 3.5, intensity));
    }

    vec3 finalEmission = baseColor * 2.5;

    float mask = smoothstep(2.5, 1.0, length(uv));
    float alpha = smoothstep(0.0, 0.1, intensity);
    alpha = clamp(alpha * mask, 0.0, 1.0);

    if (alpha <= 0.01) {
        discard;
    }

    fragColor = vec4(finalEmission * alpha, alpha);
}