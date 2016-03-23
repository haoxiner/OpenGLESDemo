package name.haoxin.demo.model;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

import name.haoxin.demo.ShaderProgram;

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
 * Created by hx on 16/3/23.
 */
public class FaceGroup {
    private static final int BYTE_PER_FLOAT = 4;
    private static final int FLOAT_PER_POSITION = 3;
    private static final int FLOAT_PER_NORMAL = 3;
    private static final int FLOAT_PER_UV = 2;

    private static final int POSITION_OFFSET = 0;
    private static final int NORMAL_OFFSET = POSITION_OFFSET + FLOAT_PER_POSITION * BYTE_PER_FLOAT;
    private static final int UV_OFFSET = NORMAL_OFFSET + FLOAT_PER_NORMAL * BYTE_PER_FLOAT;
    private int STRIDE;

    private int[] bufferObjects;
    private Material material;
    private int vertexCount;
    private boolean hasUVCoordinates;

    public FaceGroup(List<Float> vertices, List<Float> uvCoordinates, List<Float> normals, Material material) {
        this.material = material;
        bufferObjects = new int[1];
        glGenBuffers(1, bufferObjects, 0);
        vertexCount = vertices.size() / 3;

        float[] bufferArray = new float[vertices.size() + uvCoordinates.size() + normals.size()];
        if (!uvCoordinates.isEmpty()) {
            STRIDE = (FLOAT_PER_POSITION + FLOAT_PER_NORMAL + FLOAT_PER_UV) * BYTE_PER_FLOAT;
            for (int i = 0, j = 0; i < bufferArray.length; i += STRIDE / BYTE_PER_FLOAT, j += 2) {
                bufferArray[i + 6] = uvCoordinates.get(j);
                bufferArray[i + 7] = uvCoordinates.get(j + 1);
            }
            hasUVCoordinates = true;
        } else {
            STRIDE = (FLOAT_PER_POSITION + FLOAT_PER_NORMAL) * BYTE_PER_FLOAT;
            hasUVCoordinates = false;
        }
        for (int i = 0, j = 0; i < bufferArray.length; i += STRIDE / BYTE_PER_FLOAT, j += 3) {
            bufferArray[i] = vertices.get(j);
            bufferArray[i + 1] = vertices.get(j + 1);
            bufferArray[i + 2] = vertices.get(j + 2);

            bufferArray[i + 3] = normals.get(j);
            bufferArray[i + 4] = normals.get(j + 1);
            bufferArray[i + 5] = normals.get(j + 2);
        }

        int bufferSize = (vertices.size() + uvCoordinates.size() + normals.size()) * BYTE_PER_FLOAT;
        FloatBuffer floatBuffer = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder()).asFloatBuffer();
        floatBuffer.put(bufferArray).position(0);
        glBindBuffer(GL_ARRAY_BUFFER, bufferObjects[0]);
        glBufferData(GL_ARRAY_BUFFER, bufferSize, floatBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void draw(ShaderProgram shaderProgram) {
        shaderProgram.setMaterial(material);
        glBindBuffer(GL_ARRAY_BUFFER, bufferObjects[0]);
        glEnableVertexAttribArray(shaderProgram.getPositionLocation());
        glVertexAttribPointer(shaderProgram.getPositionLocation(), FLOAT_PER_POSITION, GL_FLOAT, false, STRIDE, POSITION_OFFSET);
        glEnableVertexAttribArray(shaderProgram.getNormalLocation());
        glVertexAttribPointer(shaderProgram.getNormalLocation(), FLOAT_PER_NORMAL, GL_FLOAT, false, STRIDE, NORMAL_OFFSET);
        if (hasUVCoordinates) {
            glEnableVertexAttribArray(shaderProgram.getUVLocation());
            glVertexAttribPointer(shaderProgram.getUVLocation(), FLOAT_PER_UV, GL_FLOAT, false, STRIDE, UV_OFFSET);
        }
        glDrawArrays(GL_TRIANGLES, 0, vertexCount);
        if (hasUVCoordinates) {
            glDisableVertexAttribArray(shaderProgram.getUVLocation());
        }
        glDisableVertexAttribArray(shaderProgram.getNormalLocation());
        glDisableVertexAttribArray(shaderProgram.getPositionLocation());
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

}
