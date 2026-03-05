#version 150

in vec2 texCoord;

uniform float u_time;
uniform float u_alpha;
uniform vec3 u_color;
out vec4 fragColor;


mat2 rotate(float a) {
    float s = sin(a);
    float c = cos(a);
    return mat2(c, -s, s, c);
}


float random(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);
}

void main() {
    vec2 uv = (texCoord - 0.5) * 2.0;
    float radius = length(uv);

    float eventHorizon = 0.35;
    if (radius < eventHorizon) {
        fragColor = vec4(0.0, 0.0, 0.0, u_alpha);
        return;
    }

    float angleOffset = (1.5 / (radius + 0.1)) - u_time * 2.0;
    vec2 spiralUv = uv * rotate(angleOffset);

    float angle = atan(uv.y, uv.x);
    float swirl = angle + (2.0 / (radius + 0.1)) - u_time * 3.0;

    float arm1 = sin(swirl * 4.0) * 0.3 + 0.7;
    float arm2 = sin(swirl * 9.0 - u_time * 4.0) * 0.4 + 0.6;
    float arm3 = sin(swirl * 17.0 + u_time * 2.0) * 0.3 + 0.7;

    float energy = (arm1 * arm2 * arm3) * 1.8;

    float sparks = 0.0;
    vec2 sparkUv = spiralUv * 25.0;

    for (int i = 0; i < 4; i++) {
        sparkUv *= rotate(2.13);

        vec2 cell = floor(sparkUv + u_time * 0.5);
        vec2 local = fract(sparkUv + u_time * 0.5) - 0.5;

        float rng = random(cell);

        float sparkMask = step(0.75, rng);
        float blink = sin(u_time * (8.0 + rng * 10.0) + rng * 6.28) * 0.5 + 0.5;

        sparks += (0.01 / length(local)) * blink * sparkMask;

        sparkUv *= 1.4;
    }


    float eventHorizonGlow = 0.03 / (radius - eventHorizon);
    vec3 finalColor = u_color * (energy + eventHorizonGlow) * 1.2 + (vec3(1.0) * sparks * (1.0 - radius));

    float edgeFade = smoothstep(1.0, eventHorizon + 0.02, radius);
    float finalAlpha = clamp((energy + eventHorizonGlow + sparks) * u_alpha * edgeFade, 0.0, 1.0);

    fragColor = vec4(finalColor, finalAlpha);
}