precision mediump float;
varying vec3 fragPos;
varying vec3 normal;
void main()
{
  vec3 norm = normalize(normal);

  vec3 lightDir = normalize(vec3(10,10,10) - fragPos);
  float diff = max(dot(norm,lightDir),0.0);
  gl_FragColor = vec4(0.5*norm+0.5,1.0);
}