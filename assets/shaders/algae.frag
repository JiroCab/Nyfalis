#define HIGHP

#define S2 vec3(49.0, 100.0, 73.0) / 100.0
#define S1 vec3(30.0, 93.0, 68.0) / 100.0
#define NSCALE 200.0 / 2.0

uniform float u_time;
uniform sampler2D u_texture;

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform sampler2D u_noise;

varying vec2 v_texCoords;

const float mscl = 40.0;
const float mth = 8.0;

const float minf = 0.35;
#define S3 vec3(35.0, 40.0, 80.0) / 100.0

void main() {
    vec2 c = v_texCoords;
    vec2 v = vec2(1.0/u_resolution.x, 1.0/u_resolution.y);
    vec2 coords = vec2(c.x / v.x + u_campos.x, c.y / v.y + u_campos.y);

    float btime = u_time / 5000.0;
    float noise = (
    texture2D(u_noise, coords / NSCALE + vec2(btime) * vec2(-0.9, 0.8)).r +
    texture2D(u_noise, coords / NSCALE + vec2(btime * 1.1) * vec2(0.8, -1.0)).r
    ) / 2.0;

    float stime = u_time / 5.0;

    vec4 sampled = texture2D(
        u_texture,
        c + vec2(sin(stime/3.0 + coords.y/0.75) * v.x, 0.0)
    );

    vec3 color = sampled.rgb * vec3(0.9, 0.9, 1.0);

    //Override black failed blending this with this
    if(color.r <= (minf) && color.g <= minf && color.b <= (minf + 0.03)){
        color = S3;
    }

    float tester = mod(
        (coords.x + coords.y + 1.1 + sin(stime / 8.0 + coords.x/5.0 - coords.y/100.0)) +
        sin(stime / 20.0 + coords.y/3.0) * 1.0 +
        sin(stime / 10.0 - coords.y/2.0) * 2.0 +
        sin(stime / 7.0 + coords.y/1.0) * 0.5 +
        sin(coords.x / 3.0 + coords.y / 2.0) +
        sin(stime / 20.0 + coords.x/4.0) * 1.0,
        mscl
    );

    //Highlight wave
    if(tester < mth){
        color *= 1.2;   // brighten
    }

    //algea highlights
    if(noise > 0.5 && noise < 0.74 && tester/2.0 < mth){
        color.rgb = S2 * noise;
    }
    else if(noise > 0.5 && noise < 0.74){
        color.rgb = S1 * noise;
    }

    gl_FragColor = vec4(color.rgb, min(sampled.a * 100.0, 1.0));
}