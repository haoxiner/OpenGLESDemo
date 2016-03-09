package name.haoxin.demo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by hx on 16/3/6.
 */
public class TriangleMesh {
    private int vertexBufferId;

    private int vertexCount;

    private final int BYTE_PER_FLOAT = 4;
    private final int BYTE_PER_INT = 4;

    private final int FLOAT_PER_VERTEX = 3;
    private final int FLOAT_PER_NORMAL = 3;
    private final int FLOAT_PER_UV = 2;

    public Material material;

    public TriangleMesh() {
        material = new Material();
    }

    public void draw(int positionHandle, int uvHandle, int normalHandle) {
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
        glEnableVertexAttribArray(positionHandle);
        glEnableVertexAttribArray(normalHandle);
        glEnableVertexAttribArray(uvHandle);
        glVertexAttribPointer(positionHandle, FLOAT_PER_VERTEX, GL_FLOAT, false, (FLOAT_PER_VERTEX + FLOAT_PER_UV + FLOAT_PER_NORMAL) * BYTE_PER_FLOAT, 0);
        glVertexAttribPointer(uvHandle, FLOAT_PER_UV, GL_FLOAT, false, (FLOAT_PER_VERTEX + FLOAT_PER_UV + FLOAT_PER_NORMAL) * BYTE_PER_FLOAT, FLOAT_PER_VERTEX * BYTE_PER_FLOAT);
        glVertexAttribPointer(normalHandle, FLOAT_PER_NORMAL, GL_FLOAT, false, (FLOAT_PER_VERTEX + FLOAT_PER_UV + FLOAT_PER_NORMAL) * BYTE_PER_FLOAT, (FLOAT_PER_VERTEX + FLOAT_PER_UV) * BYTE_PER_FLOAT);
        glDrawArrays(GL_TRIANGLES, 0, vertexCount);
        glDisableVertexAttribArray(uvHandle);
        glDisableVertexAttribArray(normalHandle);
        glDisableVertexAttribArray(positionHandle);
    }

    public void commit(float[] buffer) {
        int[] bufferId = new int[1];
        glGenBuffers(1, bufferId, 0);
        vertexBufferId = bufferId[0];

        int bufferSize = (buffer.length) * BYTE_PER_FLOAT;
        FloatBuffer floatBuffer = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder()).asFloatBuffer();

        floatBuffer.put(buffer).position(0);
        glBindBuffer(GL_ARRAY_BUFFER, bufferId[0]);
        glBufferData(GL_ARRAY_BUFFER, bufferSize, floatBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        vertexCount = buffer.length / 8;
    }

    public void setMaterial(name.haoxin.demo.util.objmodelloader.builder.Material mtl) {
        material.ambient[0] = (float) mtl.ka.rx;
        material.ambient[1] = (float) mtl.ka.gy;
        material.ambient[2] = (float) mtl.ka.bz;

        material.diffuse[0] = (float) mtl.kd.rx;
        material.diffuse[1] = (float) mtl.kd.gy;
        material.diffuse[2] = (float) mtl.kd.bz;

        material.specular[0] = (float) mtl.ks.rx;
        material.specular[1] = (float) mtl.ks.gy;
        material.specular[2] = (float) mtl.ks.bz;

        material.shiness = (float) mtl.nsExponent;
    }
}
