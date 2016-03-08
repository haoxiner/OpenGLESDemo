package name.haoxin.demo;

import static android.opengl.GLES20.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hx on 16/3/6.
 */
public class Model {
    private int shaderProgram;
    private int positionHandle;
    private int normalHandle;
    private int uvHandle;
    private int worldToCameraHandle;
    private int modelToWorldHandle;

    private List<TriangleMesh> meshes;

    public Model() {
        meshes = new ArrayList<>();
    }

    public void addMesh(TriangleMesh mesh) {
        meshes.add(mesh);
    }

    public void setShaderProgram(int program) {
        shaderProgram = program;
        positionHandle = glGetAttribLocation(shaderProgram, "vPosition");
        worldToCameraHandle = glGetUniformLocation(shaderProgram, "uMVP");
        normalHandle = glGetAttribLocation(shaderProgram, "vNormal");
        uvHandle = glGetAttribLocation(shaderProgram, "vUv");
        modelToWorldHandle = glGetUniformLocation(shaderProgram, "uModel");
    }

    public void draw(float[] worldToCamera, float[] modelToWorld) {
        glUseProgram(shaderProgram);
        glUniformMatrix4fv(worldToCameraHandle, 1, false, worldToCamera, 0);
        glUniformMatrix4fv(modelToWorldHandle, 1, false, modelToWorld, 0);
        for (TriangleMesh m : meshes) {
            m.draw(positionHandle, uvHandle, normalHandle);
        }
        glUseProgram(0);
    }
}
