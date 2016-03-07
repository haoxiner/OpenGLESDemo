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
public class TriangleMesh {
    private int vertexBufferId;
    private List<Integer> indexBufferIdList;
    private List<Integer> materialIdList;
    private List<Integer> vertexCountList;

    private final int BYTE_PER_FLOAT = 4;
    private final int FLOAT_PER_VERTEX = 3;
    private final int BYTE_PER_INT = 4;

    TriangleMesh() {
        int[] bufferId = new int[1];
        glGenBuffers(1, bufferId, 0);
        vertexBufferId = bufferId[0];
    }

    public void draw(float[] mvp, int positionHandle) {
        glEnableVertexAttribArray(positionHandle);
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
        glVertexAttribPointer(positionHandle, FLOAT_PER_VERTEX, GL_FLOAT, false, 2, 0);
        for (int i = 0; i < indexBufferIdList.size(); i++) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferIdList.get(i));
            glDrawElements(GL_TRIANGLES, vertexCountList.get(i), GL_UNSIGNED_INT, 0);
        }
        glDisableVertexAttribArray(positionHandle);

    }

    public void commitVertices(List<Float> vertices) {
        int bufferSize = vertices.size() * BYTE_PER_FLOAT;
        FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder()).asFloatBuffer();
        float[] tmp = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); ++i) {
            tmp[i] = vertices.get(i);
        }
        vertexBuffer.put(tmp).position(0);
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
        glBufferData(GL_ARRAY_BUFFER, bufferSize, vertexBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void commitIndices(List<Integer> indices) {
        int bufferSize = indices.size() * BYTE_PER_INT;
        IntBuffer indexBuffer = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder()).asIntBuffer();
        int[] tmp = new int[indices.size()];
        for (int i = 0; i < indices.size(); ++i) {
            tmp[i] = indices.get(i);
        }
        indexBuffer.put(tmp).position(0);
        int[] bufferId = new int[1];
        glGenBuffers(1, bufferId, 0);
        indexBufferIdList.add(bufferId[0]);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferId[0]);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, bufferSize, indexBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        vertexCountList.add(indices.size());
    }

}
