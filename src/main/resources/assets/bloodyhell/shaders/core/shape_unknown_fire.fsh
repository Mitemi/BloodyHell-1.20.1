#version 150

uniform float AnimTime;

in vec3 localPos;
in vec4 vertexColor;
out vec4 fragColor;

float rand(vec2 n) {
    return fract(cos(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);
}

float noise(vec2 n) {
    const vec2 d = vec2(0.0, 1.0);
    vec2 b = floor(n), f = smoothstep(vec2(0.0), vec2(1.0), fract(n));
    return mix(mix(rand(b), rand(b + d.yx), f.x), mix(rand(b + d.xy), rand(b + d.yy), f.x), f.y);
}

float fbm(vec2 n) {
    float total = 0.0, amplitude = 1.0;
    for (int i = 0; i < 4; i++) {
        total += noise(n) * amplitude;
        n += n;
        amplitude *= 0.5;
    }
    return total;
}

void main() {

    vec3 dir = normalize(localPos);


    float theta = atan(dir.z, dir.x);
    float phi = asin(dir.y);

    vec2 polar = vec2(theta, phi);

    vec3 baseColor = vertexColor.rgb;
    vec3 c1 = baseColor * 0.1;
    vec3 c2 = baseColor * 0.1;
    vec3 c3 = baseColor * 0.1;
    vec3 c4 = vec3(1.0, 1.0, 0.1);
    vec3 c5 = vec3(0.1);
    vec3 c6 = vec3(0.7);

    vec2 speed = vec2(0.8, 0.1);


    vec2 p = vec2(polar.x * 5.0, polar.y * 10.0);


    float q = fbm(p - AnimTime * 2.0);
    vec2 r = vec2(fbm(p + q + AnimTime * speed.x - p.x - p.y), fbm(p + q - AnimTime * speed.y));
    vec3 colorOut = mix(c1, c2, fbm(p + r)) + mix(c3, c4, r.x) - mix(c5, c6, r.y);


    float poleFade = smoothstep(1.5, 1.2, abs(phi));


    float luminance = dot(colorOut.rgb, vec3(0.2126, 0.7152, 0.0722));


    float alpha = smoothstep(0.1, 0.4, luminance) * poleFade * vertexColor.a;


    if (alpha <= 0.01) {
        discard;
    }


    fragColor = vec4(colorOut, alpha);
}