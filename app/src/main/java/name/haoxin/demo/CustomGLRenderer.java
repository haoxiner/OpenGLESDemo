package name.haoxin.demo;

import android.content.Context;
import android.opengl.Matrix;

import static android.opengl.GLES20.*;

import android.opengl.GLSurfaceView;

import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import name.haoxin.demo.util.ObjLoader;
import name.haoxin.demo.util.TextResourceReader;

/**
 * Created by hx on 16/3/4.
 */
public class CustomGLRenderer implements GLSurfaceView.Renderer {
    private final float[] m_MVP = new float[16];
    private final float[] m_proj = new float[16];
    private final float[] m_view = new float[16];
    private float[] m_rotation = new float[16];

    private Model model;

    private Context context;

    public CustomGLRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        // Set the background frame color
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        InputStream inputStream = context.getResources().openRawResource(R.raw.test);
        model = ObjLoader.load(inputStream);

        int vShader = CustomGLRenderer.loadShader(GL_VERTEX_SHADER, TextResourceReader.readTextFileFromResource(context, R.raw.phongv));
        int fShader = CustomGLRenderer.loadShader(GL_FRAGMENT_SHADER, TextResourceReader.readTextFileFromResource(context, R.raw.phongf));
        int program = glCreateProgram();
        glAttachShader(program, vShader);
        glAttachShader(program, fShader);
        glLinkProgram(program);
        model.setShaderProgram(program);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Redraw background color
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        Matrix.setLookAtM(m_view, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(m_MVP, 0, m_proj, 0, m_view, 0);
        float[] scratch = new float[16];
        Matrix.setRotateM(m_rotation, 0, mAngle, 0, -1.0f, -1.0f);
        Matrix.multiplyMM(scratch, 0, m_MVP, 0, m_rotation, 0);
        //
        model.draw(scratch);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(m_proj, 0, -ratio, ratio, -1, 1, 1.0f, 100.0f);
//        Camera.perspectiveM(m_proj, 45, (float) width / (float) height, 1f, 10f);
//        Matrix.orthoM(m_proj, 0, -ratio, ratio, -1, 1, 1.0f, 100.0f);
    }

    public static int loadShader(int type, String shaderCode) {
        int shader = glCreateShader(type);
        glShaderSource(shader, shaderCode);
        glCompileShader(shader);
        return shader;
    }

    public volatile float mAngle;

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }
}
