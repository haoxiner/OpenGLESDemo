precision mediump float;
varying vec3 fragPos;
varying vec3 normal;
void main()
{
//    gl_FragColor = vec4(vec3(normalize(normal)*0.5+0.5),1.0);
//    gl_FragColor = vec4(0.5,0.5,0.5,1.0);

    vec3 norm = normalize(normal);
    vec3 lightPos = vec3(0,2,0);
    vec3 lightDir = normalize(lightPos - fragPos);

    vec3 viewPos = vec3(0,0,2);
    vec3 viewDir = normalize(viewPos - fragPos);
    vec3 reflectDir = normalize(reflect(-lightDir,norm));

    float diff = max(dot(lightDir,norm),0.0);
    float spec = pow(max(dot(viewDir,reflectDir),0.0),32);

    gl_FragColor = vec4(vec3(spec+diff+0.1),1.0);
}