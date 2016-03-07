package name.haoxin.demo;

import android.opengl.GLES30;

import static android.opengl.GLES20.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by hx on 16/3/6.
 */
public class Shape {
    private int[] vbo;

    private int m_MVPMatrixHandle;
    private int m_positionHandle;
    private int m_colorHandle;
    private final int m_program;

    public Shape(String vertexShaderCode, String fragmentShaderCode) {
        float[] g_vertex_buffer_data = {
                1.0f, 1.0f, -1.0f, // triangle 2 : begin
                -1.0f, -1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f // triangle 2 : end
                };
        vertexCount = g_vertex_buffer_data.length / COORDS_PER_VERTEX;
        FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(g_vertex_buffer_data.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(g_vertex_buffer_data).position(0);

        float g_color_buffer_data[] = {
                0.583f, 0.771f, 0.014f,
                0.609f, 0.115f, 0.436f,
                0.327f, 0.483f, 0.844f,
                0.822f, 0.569f, 0.201f,
                0.435f, 0.602f, 0.223f,
                0.310f, 0.747f, 0.185f,
                0.597f, 0.770f, 0.761f,
                0.559f, 0.436f, 0.730f,
                0.359f, 0.583f, 0.152f,
                0.483f, 0.596f, 0.789f,
                0.559f, 0.861f, 0.639f,
                0.195f, 0.548f, 0.859f,
                0.014f, 0.184f, 0.576f,
                0.771f, 0.328f, 0.970f,
                0.406f, 0.615f, 0.116f,
                0.676f, 0.977f, 0.133f,
                0.971f, 0.572f, 0.833f,
                0.140f, 0.616f, 0.489f,
                0.997f, 0.513f, 0.064f,
                0.945f, 0.719f, 0.592f,
                0.543f, 0.021f, 0.978f,
                0.279f, 0.317f, 0.505f,
                0.167f, 0.620f, 0.077f,
                0.347f, 0.857f, 0.137f,
                0.055f, 0.953f, 0.042f,
                0.714f, 0.505f, 0.345f,
                0.783f, 0.290f, 0.734f,
                0.722f, 0.645f, 0.174f,
                0.302f, 0.455f, 0.848f,
                0.225f, 0.587f, 0.040f,
                0.517f, 0.713f, 0.338f,
                0.053f, 0.959f, 0.120f,
                0.393f, 0.621f, 0.362f,
                0.673f, 0.211f, 0.457f,
                0.820f, 0.883f, 0.371f,
                0.982f, 0.099f, 0.879f};
        FloatBuffer colorBuffer = ByteBuffer.allocateDirect(g_color_buffer_data.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorBuffer.put(g_color_buffer_data).position(0);

        int vShader = CustomGLRenderer.loadShader(GL_VERTEX_SHADER, vertexShaderCode);
        int fShader = CustomGLRenderer.loadShader(GL_FRAGMENT_SHADER, fragmentShaderCode);
        m_program = glCreateProgram();
        glAttachShader(m_program, vShader);
        glAttachShader(m_program, fShader);
        glLinkProgram(m_program);

        vbo = new int[2];
        glGenBuffers(2, vbo, 0);

        glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer.capacity() * BYTES_PER_FLOAT, vertexBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
        glBufferData(GL_ARRAY_BUFFER, colorBuffer.capacity() * BYTES_PER_FLOAT, colorBuffer, GL_STATIC_DRAW);
    }

    private static final int COORDS_PER_VERTEX = 3;
    private final int BYTES_PER_FLOAT = 4;
    private final int vertexCount;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public void draw(float[] mvp) {
        // Add program to OpenGL ES environment
        glUseProgram(m_program);

        // get handle to vertex shader's vPosition member
        m_positionHandle = glGetAttribLocation(m_program, "vPosition");

        // Enable a handle to the triangle vertices
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        glEnableVertexAttribArray(m_positionHandle);
        // Prepare the triangle coordinate data
        glVertexAttribPointer(m_positionHandle, COORDS_PER_VERTEX,
                GL_FLOAT, false,
                0, 0);

        // get handle to fragment shader's vColor member
        m_colorHandle = glGetAttribLocation(m_program, "vColor");
        glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
        glEnableVertexAttribArray(m_colorHandle);

        // Set color for drawing the triangle
        glVertexAttribPointer(m_colorHandle, COORDS_PER_VERTEX,
                GL_FLOAT, false,
                0, 0);

        // get handle to shape's transformation matrix
        m_MVPMatrixHandle = glGetUniformLocation(m_program, "uMVP");

        // Pass the projection and view transformation to the shader
        glUniformMatrix4fv(m_MVPMatrixHandle, 1, false, mvp, 0);

        // Draw the triangle
        glDrawArrays(GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        glDisableVertexAttribArray(m_positionHandle);
    }
}
