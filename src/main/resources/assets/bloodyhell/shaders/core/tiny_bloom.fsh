#version 150

uniform float AnimTime;

in vec2 localPos;
in vec4 vertexColor;
out vec4 fragColor;

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

void main() {
    vec2 uv = localPos;
    float time = AnimTime * 8.0;

    vec2 noiseOffset = vec2(
    noise2D(uv * 3.0 + time),
    noise2D(uv * 3.0 - time + 15.0)
    );

    vec2 warpedUV = uv + (noiseOffset - 0.5) * 0.5;

    float dist = length(warpedUV);

    if (dist > 1.0) {
        discard;
    }

    float intensity = pow(max(0.0, 0.7 - dist), 1.5);

    float coreMask = smoothstep(0.3, 0.0, dist);
    vec3 colCore = vec3(1.0, 1.0, 0.95);
    vec3 colBloom = vertexColor.rgb;

    vec3 baseColor = mix(colBloom, colCore, coreMask);

    vec3 finalEmission = baseColor * intensity * 3.5;

    float alpha = smoothstep(0.0, 0.5, intensity) * 0.4;
    vec3 preMultipliedRGB = finalEmission * vertexColor.a;
    float preMultipliedAlpha = alpha * vertexColor.a;

    if (length(preMultipliedRGB) <= 0.01 && preMultipliedAlpha <= 0.01) {
        discard;
    }

    fragColor = vec4(preMultipliedRGB, preMultipliedAlpha);
}