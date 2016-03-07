uniform mat4 uMVP;
//attribute vec3 vColor;
attribute vec3 vPosition;
varying vec3 color;
void main()
{
  gl_Position = uMVP * vec4(vPosition,1.0);
  color = vec3(1.0,0.0,0.0);
}