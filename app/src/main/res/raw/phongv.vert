uniform mat4 uCamera;
uniform mat4 uModel;

attribute vec3 vPosition;
attribute vec3 vNormal;
attribute vec2 vTextureUV;
varying vec3 fragPos;
varying vec3 normal;
varying vec2 texUV;

void main()
{
    gl_Position = uCamera * uModel * vec4(vPosition,1.0);
    fragPos = vec3(uModel * vec4(vPosition,1.0));
    normal = vec3(uModel * vec4(vNormal,0.0));
    texUV = vTextureUV;
}