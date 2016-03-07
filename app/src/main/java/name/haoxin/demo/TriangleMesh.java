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
public class TriangleMesh {
    private int vertexBufferId;

    private List<Integer> vertexBufferIdList;
    private List<Integer> indexBufferIdList;
    private List<Integer> normalAndUvBufferIdList;
    private List<Integer> materialIdList;
    private List<Integer> vertexCountList;

    private final int BYTE_PER_FLOAT = 4;
    private final int BYTE_PER_INT = 4;

    private final int FLOAT_PER_VERTEX = 3;
    private final int FLOAT_PER_NORMAL = 3;
    private final int FLOAT_PER_UV = 2;

    public TriangleMesh() {

        vertexBufferIdList = new ArrayList<>();
        normalAndUvBufferIdList = new ArrayList<>();
        materialIdList = new ArrayList<>();
        vertexCountList = new ArrayList<>();
    }

    public void draw(float[] mvp, int positionHandle) {

    }

    public void draw(int positionHandle, int uvHandle, int normalHandle) {

        for (int i = 0; i < vertexBufferIdList.size(); i++) {
            glBindBuffer(GL_ARRAY_BUFFER, vertexBufferIdList.get(i));
            glEnableVertexAttribArray(positionHandle);
            glEnableVertexAttribArray(normalHandle);
            glEnableVertexAttribArray(uvHandle);
            glVertexAttribPointer(positionHandle, FLOAT_PER_VERTEX, GL_FLOAT, false, (FLOAT_PER_VERTEX + FLOAT_PER_UV + FLOAT_PER_NORMAL) * BYTE_PER_FLOAT, 0);
            glVertexAttribPointer(uvHandle, FLOAT_PER_UV, GL_FLOAT, false, (FLOAT_PER_VERTEX + FLOAT_PER_UV + FLOAT_PER_NORMAL) * BYTE_PER_FLOAT, FLOAT_PER_VERTEX * BYTE_PER_FLOAT);
            glVertexAttribPointer(normalHandle, FLOAT_PER_NORMAL, GL_FLOAT, false, (FLOAT_PER_VERTEX + FLOAT_PER_UV + FLOAT_PER_NORMAL) * BYTE_PER_FLOAT, (FLOAT_PER_VERTEX + FLOAT_PER_UV) * BYTE_PER_FLOAT);
            glDrawArrays(GL_TRIANGLES, 0, vertexCountList.get(i));
            glDisableVertexAttribArray(uvHandle);
            glDisableVertexAttribArray(normalHandle);
            glDisableVertexAttribArray(positionHandle);
        }
    }

    public void commit(List<Float> vertices, List<Float> uvCoordinates, List<Float> normals) {
        int[] bufferId = new int[1];
        glGenBuffers(1, bufferId, 0);
        vertexBufferIdList.add(bufferId[0]);

        int bufferSize = (vertices.size() + normals.size() + uvCoordinates.size()) * BYTE_PER_FLOAT;
        FloatBuffer buffer = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder()).asFloatBuffer();

        float[] tmp = new float[vertices.size() + normals.size() + uvCoordinates.size()];
        for (int index = 0, vertexIndex = 0, uvIndex = 0, normalIndex = 0; index < tmp.length; ) {
            for (int i = 0; i < 3; i++) {
                tmp[index] = vertices.get(vertexIndex);
                ++index;
                ++vertexIndex;
            }
            for (int i = 0; i < 2; i++) {
                tmp[index] = uvCoordinates.get(uvIndex);
                ++index;
                ++uvIndex;
            }
            for (int i = 0; i < 3; i++) {
                tmp[index] = normals.get(normalIndex);
                ++index;
                ++normalIndex;
            }
        }

        buffer.put(tmp).position(0);
        glBindBuffer(GL_ARRAY_BUFFER, bufferId[0]);
        glBufferData(GL_ARRAY_BUFFER, bufferSize, buffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        vertexCountList.add(vertices.size() / 3);
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
            for (int i = 0; i < 3; i++) {
                tmp[index] = normals.get(normalIndex);
                ++index;
                ++normalIndex;
            }
            for (int i = 0; i < 2; i++) {
                tmp[index] = uvCoordinates.get(uvIndex);
                ++index;
                ++uvIndex;
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
