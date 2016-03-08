uniform mat4 uMVP;
uniform mat4 uModel;
attribute vec3 vPosition;
attribute vec3 vNormal;
attribute vec2 vUv;
varying vec3 fragPos;
varying vec3 normal;
void main()
{
    gl_Position = uMVP * uModel * vec4(vPosition,1.0);
    fragPos = vec3(uModel * vec4(vPosition,1.0));
    normal = vec3(uModel * vec4(vNormal,0.0));
//    normal = vNormal;
}