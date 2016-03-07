package name.haoxin.demo;

import static android.opengl.GLES20.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

/**
 * Created by hx on 16/3/6.
 */
public class Model {
    private final int BYTE_PER_FLOAT = 4;
    private final int COORDS_PER_VERTEX = 3;
    private final int BYTE_PER_INT = 4;

    private int vertexBufferObjects[];

    private int shaderProgram;
    private int positionHandle;
    private int mvpHandle;
    private int vertexCount;

    public Model() {
        vertexBufferObjects = new int[3];
        glGenBuffers(3, vertexBufferObjects, 0);
    }

    public void setShaderProgram(int program) {
        shaderProgram = program;
        positionHandle = glGetAttribLocation(shaderProgram, "vPosition");
        mvpHandle = glGetUniformLocation(shaderProgram, "uMVP");
    }

    public void commitVertices(List<Float> vertices) {
        int bufferSize = vertices.size() * BYTE_PER_FLOAT;
        FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder()).asFloatBuffer();
        float[] tmp = new float[vertices.size()];
        for (int i = 0; i <vertices.size();++i){
            tmp[i] = vertices.get(i);
        }
        vertexBuffer.put(tmp).position(0);
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObjects[0]);
        glBufferData(GL_ARRAY_BUFFER, bufferSize, vertexBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void commitIndices(List<Integer> indices) {
        int bufferSize = indices.size() * BYTE_PER_INT;
        IntBuffer indexBuffer = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder()).asIntBuffer();
        int[] tmp = new int[indices.size()];
        for (int i = 0;i<indices.size();++i){
            tmp[i] = indices.get(i);
        }
        indexBuffer.put(tmp).position(0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vertexBufferObjects[2]);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, bufferSize, indexBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        vertexCount = indices.size();
    }

    public void draw(float[] mvp) {
        // Add program to OpenGL ES environment
        glUseProgram(shaderProgram);
        // Enable a handle to the triangle vertices
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObjects[0]);
        glEnableVertexAttribArray(positionHandle);
        // Prepare the triangle coordinate data
        glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GL_FLOAT, false,
                0, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vertexBufferObjects[2]);

        // Pass the projection and view transformation to the shader
        glUniformMatrix4fv(mvpHandle, 1, false, mvp, 0);

        // Draw the triangle
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);

        // Disable vertex array
        glDisableVertexAttribArray(positionHandle);
    }
}
