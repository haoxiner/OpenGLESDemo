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
    private List<Integer> normalAndUvBufferIdList;
    private List<Integer> materialIdList;
    private List<Integer> vertexCountList;

    private final int BYTE_PER_FLOAT = 4;
    private final int BYTE_PER_INT = 4;

    private final int FLOAT_PER_VERTEX = 3;
    private final int FLOAT_PER_NORMAL = 3;
    private final int FLOAT_PER_UV = 2;

    TriangleMesh() {
        int[] bufferId = new int[1];
        glGenBuffers(1, bufferId, 0);
        vertexBufferId = bufferId[0];
    }

    public void draw(float[] mvp, int positionHandle, int normalHandle, int uvHandle) {
        glEnableVertexAttribArray(positionHandle);
        glEnableVertexAttribArray(normalHandle);
        glEnableVertexAttribArray(uvHandle);

        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
        glVertexAttribPointer(positionHandle, FLOAT_PER_VERTEX, GL_FLOAT, false, 0, 0);

        for (int i = 0; i < indexBufferIdList.size(); i++) {
            glBindBuffer(GL_ARRAY_BUFFER, normalAndUvBufferIdList.get(i));
            glVertexAttribPointer(normalHandle, FLOAT_PER_NORMAL, GL_FLOAT, false, FLOAT_PER_UV, 0);
            glVertexAttribPointer(uvHandle, FLOAT_PER_UV, GL_FLOAT, false, FLOAT_PER_NORMAL, FLOAT_PER_NORMAL);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferIdList.get(i));
            glDrawElements(GL_TRIANGLES, vertexCountList.get(i), GL_UNSIGNED_INT, 0);
        }
        glDisableVertexAttribArray(uvHandle);
        glDisableVertexAttribArray(normalHandle);
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

    public void commitNormalsAndUvCoordinates(List<Float> normals, List<Float> uvCoordinates) {
        int bufferSize = (normals.size() + uvCoordinates.size()) * BYTE_PER_FLOAT;
        FloatBuffer buffer = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder()).asFloatBuffer();
        float[] tmp = new float[normals.size() + uvCoordinates.size()];
        for (int index = 0, normalIndex = 0, uvIndex = 0; index < tmp.length; ) {
            for (; normalIndex < normalIndex + 3; normalIndex++) {
                tmp[index] = normals.get(normalIndex);
                ++index;
            }
            for (; uvIndex < uvIndex + 2; uvIndex++) {
                tmp[index] = uvCoordinates.get(uvIndex);
                ++index;
            }
        }
        buffer.put(tmp).position(0);
        int[] bufferId = new int[1];
        glGenBuffers(1, bufferId, 0);
        normalAndUvBufferIdList.add(bufferId[0]);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferId[0]);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, bufferSize, buffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}
