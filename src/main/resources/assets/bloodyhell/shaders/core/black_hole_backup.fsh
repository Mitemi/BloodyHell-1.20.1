#version 150

in vec2 texCoord;

uniform float u_time;
uniform float u_alpha;
uniform vec3 u_color;
out vec4 fragColor;

#define NUM_LAYERS 10.0

// 2D Rotation Matrix
mat2 Rot(float a) {
    float c = cos(a), s = sin(a);
    return mat2(c, -s, s, c);
}

// Generates individual star shapes with flares
float Star(vec2 uv, float flare) {
    float d = length(uv);
    float m = 0.02 / d;

    float rays = max(0.0, 1.0 - abs(uv.x * uv.y * 1000.0));
    m += rays * flare;

    uv *= Rot(3.14159 / 4.0);
    rays = max(0.0, 1.0 - abs(uv.x * uv.y * 1000.0));
    m += rays * 0.3 * flare;

    m *= smoothstep(1.0, 0.2, d);

    return m;
}

// Pseudo-random hash function
float Hash21(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

// Generates a grid layer of twinkling stars
vec3 StarLayer(vec2 uv) {
    vec3 col = vec3(0.0);

    vec2 gv = fract(uv) - 0.5;
    vec2 id = floor(uv);

    for(int y = -1; y <= 1; y++) {
        for(int x = -1; x <= 1; x++) {
            vec2 offs = vec2(float(x), float(y));

            float n = Hash21(id + offs);
            float size = fract(n * 345.32);

            vec2 p = vec2(n, fract(n * 34.0));

            float star = Star(gv - offs - p + 0.5, smoothstep(0.8, 1.0, size) * 0.6);

            vec3 hueShift = fract(n * 2345.2) * vec3(0.2, 0.3, 0.9) * 123.2;

            vec3 randColor = sin(hueShift) * 0.5 + 0.5;

            // Mix the randomized variance with the passed base color
            vec3 color = mix(u_color, randColor, 0.4);
            color = color * vec3(1.0, 1.0, 1.0 + size);

            star *= sin(u_time * 3.0 + n * 6.2831) * 0.4 + 1.0;

            // Add the calculated star to the layer accumulation
            col += star * size * color;
        }
    }
    return col;
}

// Normal vector helper
vec2 N(float angle) {
    return vec2(sin(angle), cos(angle));
}

// Adapted from ShaderToy ftt3R7 (Kaleidoscope Starfield)
void main() {
    // Center the UV coordinates (-1.0 to 1.0)
    vec2 origUv = (texCoord - 0.5) * 2.0;
    vec2 uv = origUv;

    // Save original distance from center to create the circular portal mask
    float distFromCenter = length(uv);

    float t = u_time * 0.02; // Slowed down rotation slightly for a larger portal

    // --- Fractal Folding (Kaleidoscope Effect) ---
    uv.x = abs(uv.x);
    uv.y += tan((5.0 / 6.0) * 3.14159) * 0.5;

    vec2 n = N((5.0 / 6.0) * 3.14159);
    float d = dot(uv - vec2(0.5, 0.0), n);
    uv -= n * max(0.0, d) * 2.0;

    n = N((2.0 / 3.0) * 3.14159);
    float scale = 1.0;
    uv.x += 1.5 / 1.25;

    for(int i = 0; i < 5; i++) {
        scale *= 1.25;
        uv *= 1.25;
        uv.x -= 1.5;

        uv.x = abs(uv.x);
        uv.x -= 0.5;
        uv -= n * min(0.0, dot(uv, n)) * 2.0;
    }

    uv *= Rot(t);
    vec3 col = vec3(0.0);

    // Render parallax star layers
    for(float i = 0.0; i < 1.0; i += 1.0 / NUM_LAYERS) {
        float depth = fract(i + t);
        float s = mix(20.0, 0.5, depth);
        float fade = depth * smoothstep(1.0, 0.9, depth);
        col += StarLayer(uv * s + i * 453.2) * fade;
    }

    // --- Portal Masking & Black Hole Center ---

    // Define the boundaries
    float blackHoleRadius = 0.35; // The pitch-black center
    float portalEdge = 0.9;       // Where the stars start fading out completely

    float alpha = u_alpha;

    if (distFromCenter < blackHoleRadius) {
        // Pure black center
        col = vec3(0.0);
    } else {
        // Smooth transition from the black hole into the starfield
        float edgeFade = smoothstep(blackHoleRadius, blackHoleRadius + 0.15, distFromCenter);
        col *= edgeFade;

        // --- Accretion Disk Glow ---
        float angle = atan(origUv.y, origUv.x);
        float wave = sin(angle * 10.0 + u_time * 5.0) * 0.05;

        float glow = 0.01 / (distFromCenter - blackHoleRadius);
        glow += 0.02 / abs(distFromCenter - (blackHoleRadius + 0.1 + wave));

        // Add tinted glow to the starfield
        col += u_color * glow * edgeFade;

        // Fade out the outer edges so it's a circle, not a hard square
        alpha *= smoothstep(1.0, portalEdge, distFromCenter);

        // Keep alpha high where the glow is intense to prevent transparency washing it out
        alpha = max(alpha, clamp(glow, 0.0, 1.0) * u_alpha * smoothstep(1.0, portalEdge, distFromCenter));
    }

    fragColor = vec4(col, alpha);
}