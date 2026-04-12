#version 150

uniform float Time;
uniform float Intensity;
uniform vec2 Resolution;

in vec2 texCoord0;
in vec4 vertexColor;
out vec4 fragColor;

void main() {
    if (Intensity <= 0.0) {
        fragColor = vec4(0.0);
        return;
    }

    vec2 uv = (texCoord0 - 0.5) * 2.0;
    uv.x *= Resolution.x / Resolution.y;

    float radius = length(uv);
    float angle = atan(uv.y, uv.x);

    float bend = sin(radius * 2.0 - Time * 1.5) * 0.5;
    float wiggledAngle = angle + (bend * Intensity);

    float tentacles = 0.0;

    tentacles += abs(sin(wiggledAngle * 5.0 + Time)) * 0.3;

    float midWiggle = sin(radius * 5.0 + Time * 3.0) * 0.2;
    tentacles += abs(sin((wiggledAngle + midWiggle) * 12.0 - Time * 2.0)) * 0.15;

    tentacles += abs(sin(wiggledAngle * 25.0 + Time * 5.0)) * 0.05;

    float distortedRadius = radius + (tentacles * Intensity);

    float safeZone = 2.0 - (Intensity * 0.8);

    float alpha = smoothstep(safeZone - 0.2, safeZone + 0.1, distortedRadius);

    vec3 color = vec3(0.0);
    if (distortedRadius > safeZone + 0.2) {
        color = vec3(0.05, 0.0, 0.0);
    }

    fragColor = vec4(color, clamp(alpha, 0.0, 1.0));
}