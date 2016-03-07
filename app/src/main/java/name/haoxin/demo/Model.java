package name.haoxin.demo;

import static android.opengl.GLES20.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
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
    private int mvpHandle;

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
        mvpHandle = glGetUniformLocation(shaderProgram, "uMVP");
        normalHandle = glGetAttribLocation(shaderProgram, "vNormal");
        uvHandle = glGetAttribLocation(shaderProgram, "vUv");
    }

    public void draw(float[] mvp) {
        glUseProgram(shaderProgram);
        glUniformMatrix4fv(mvpHandle, 1, false, mvp, 0);
        for (TriangleMesh m : meshes) {
            m.draw(positionHandle, uvHandle, normalHandle);
        }
        glUseProgram(0);
    }
}
