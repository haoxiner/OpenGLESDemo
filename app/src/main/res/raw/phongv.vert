uniform mat4 uMVP;
attribute vec3 vPosition;
attribute vec3 vNormal;
attribute vec2 vUv;
varying vec3 fragPos;
varying vec3 normal;
void main()
{
  gl_Position = uMVP * vec4(vPosition,1.0);
  fragPos = vPosition;
  normal = vNormal;
}