package name.haoxin.demo;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.Matrix;

import static android.opengl.GLES20.*;

import android.opengl.GLSurfaceView;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import name.haoxin.demo.util.ObjLoader;
import name.haoxin.demo.util.TextResourceReader;

/**
 * Created by hx on 16/3/4.
 */
public class CustomGLRenderer implements GLSurfaceView.Renderer {
    private final float[] worldToCamera = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] worldToModel = new float[16];
    private float angle = 0;

    private Model model;

    private Context context;

    float[] a = new float[4];
    float[] b = new float[4];
    float[] inverseModelMat = new float[16];

    public CustomGLRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);

        glEnable(GL_CULL_FACE);
        glFrontFace(GL_CCW);
        glCullFace(GL_BACK);

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open("test.obj");
        } catch (IOException e) {
            e.printStackTrace();
            inputStream = null;
        }
        if (inputStream != null) {
            model = ObjLoader.load(inputStream);

            int vShader = CustomGLRenderer.loadShader(GL_VERTEX_SHADER, TextResourceReader.readTextFileFromResource(context, R.raw.phongv));
            int fShader = CustomGLRenderer.loadShader(GL_FRAGMENT_SHADER, TextResourceReader.readTextFileFromResource(context, R.raw.phongf));
            int program = glCreateProgram();
            glAttachShader(program, vShader);
            glAttachShader(program, fShader);
            glLinkProgram(program);
            model.setShaderProgram(program);

            Matrix.setIdentityM(worldToModel, 0);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 2, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(worldToCamera, 0, projectionMatrix, 0, viewMatrix, 0);
        model.draw(worldToCamera, worldToModel);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1.0f, 100.0f);
    }

    public static int loadShader(int type, String shaderCode) {
        int shader = glCreateShader(type);
        glShaderSource(shader, shaderCode);
        glCompileShader(shader);
        return shader;
    }

    public void rotateArcball(float[] v0, float v1[]) {
        float squareV0 = v0[0] * v0[0] + v0[1] * v0[1];
        float squareV1 = v1[0] * v1[0] + v1[1] * v1[1];
        if (squareV0 <= 1) {
            v0[2] = (float) Math.sqrt(1 - squareV0);
        } else {
            float invLength = 1.0f / (float) (Math.sqrt(squareV0));
            v0[0] *= invLength;
            v0[1] *= invLength;
            v0[2] = 0;
        }
        if (squareV1 <= 1) {
            v1[2] = (float) Math.sqrt(1 - squareV1);
        } else {
            float invLength = 1.0f / (float) (Math.sqrt(squareV1));
            v1[0] *= invLength;
            v1[1] *= invLength;
            v1[2] = 0;
        }
        float angle = (float) (Math.acos(Math.min(v0[0] * v1[0] + v0[1] * v1[1] + v0[2] * v1[2], 1.0)) / Math.PI * 180);

        Matrix.invertM(inverseModelMat, 0, worldToModel, 0);
        Matrix.multiplyMV(a, 0, inverseModelMat, 0, v0, 0);
        Matrix.multiplyMV(b, 0, inverseModelMat, 0, v1, 0);

        float[] previouseWorldToModel = worldToModel.clone();
        float crossX = a[1] * b[2] - a[2] * b[1];
        float crossY = a[2] * b[0] - a[0] * b[2];
        float crossZ = a[0] * b[1] - a[1] * b[0];
        Matrix.rotateM(worldToModel, 0, previouseWorldToModel, 0, angle, crossX, crossY, crossZ);

    }

}
