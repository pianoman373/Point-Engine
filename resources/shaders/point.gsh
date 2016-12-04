#version 330 core
layout (points) in;
layout (points, max_vertices = 1) out;

in vec3 vColor[];

out vec3 fColor;

void main() {
    fColor = vColor[0];

    //gl_Position = gl_in[0].gl_Position + vec4(-0.1, 0.0, 0.0, 0.0);
    //EmitVertex();

    gl_Position = gl_in[0].gl_Position;
    EmitVertex();

    EndPrimitive();
}
