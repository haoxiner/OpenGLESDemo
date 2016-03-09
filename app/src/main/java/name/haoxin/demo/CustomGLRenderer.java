package name.haoxin.demo;

import android.content.Context;
import android.opengl.Matrix;

import static android.opengl.GLES20.*;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import name.haoxin.demo.util.TextResourceReader;

/**
 * Created by hx on 16/3/4.
 */
public class CustomGLRenderer implements GLSurfaceView.Renderer {
    private final float[] worldToCamera = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] arcballMatrix = new float[16];

    private final float[] worldToModel = new float[16];

    private final float[] translate = new float[16];
    private final float[] rotateTranslate = new float[16];
    private float angle = 0;

    private int uViewPosHandle;
    private int uLightPos;

    private Model model;
    private Model cornellBoxModel;
    private Model mitsubaModel;

    private Context context;

    float[] a = new float[4];
    float[] b = new float[4];
    float[] inverseModelMat = new float[16];

    private boolean isCornellBox;
    private int program;

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
//        cornellBoxModel = Model.load(context.getAssets(), "CornellBox-Glossy.obj");
        mitsubaModel = Model.load(context.getAssets(), "mitsuba-sphere.obj");
        model = mitsubaModel;

        int vShader = CustomGLRenderer.loadShader(GL_VERTEX_SHADER, TextResourceReader.readTextFileFromResource(context, R.raw.phongv));
        int fShader = CustomGLRenderer.loadShader(GL_FRAGMENT_SHADER, TextResourceReader.readTextFileFromResource(context, R.raw.phongf));
        program = glCreateProgram();
        glAttachShader(program, vShader);
        glAttachShader(program, fShader);
        glLinkProgram(program);
//        cornellBoxModel.setShaderProgram(program);
        mitsubaModel.setShaderProgram(program);

        glUseProgram(program);
        uViewPosHandle = glGetUniformLocation(program, "uViewPos");
        glUniform3f(uViewPosHandle, model.centerX, model.centerY, model.centerZ + 3);
        uLightPos = glGetUniformLocation(program, "uLightPos");
        glUniform3f(uLightPos, 10, 10, 10);
        glUseProgram(0);

        Matrix.setIdentityM(worldToModel, 0);
        Matrix.setIdentityM(arcballMatrix, 0);

//        model = mitsubaModel;
//        model = mitsubaModel;
//        switchModel();

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        Matrix.setLookAtM(viewMatrix, 0, model.centerX, model.centerY, model.centerZ + 3, model.centerX, model.centerY, model.centerZ, 0f, 1.0f, 0.0f);
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
        if (squareV0 <= 0.999f) {
            v0[2] = (float) Math.sqrt(1 - squareV0);
        } else {
            float invLength = 1.0f / (float) (Math.sqrt(squareV0));
            v0[0] *= invLength;
            v0[1] *= invLength;
            v0[2] = 0;
        }
        if (squareV1 <= 0.999f) {
            v1[2] = (float) Math.sqrt(1 - squareV1);
        } else {
            float invLength = 1.0f / (float) (Math.sqrt(squareV1));
            v1[0] *= invLength;
            v1[1] *= invLength;
            v1[2] = 0;
        }

        float angle = (float) (Math.acos(Math.min(v0[0] * v1[0] + v0[1] * v1[1] + v0[2] * v1[2], 0.999f)) / Math.PI * 180);

        Matrix.invertM(inverseModelMat, 0, arcballMatrix, 0);
        Matrix.multiplyMV(a, 0, inverseModelMat, 0, v0, 0);
        Matrix.multiplyMV(b, 0, inverseModelMat, 0, v1, 0);

        Matrix.setIdentityM(translate, 0);
        Matrix.translateM(translate, 0, -model.centerX, -model.centerY, -model.centerZ);
        float crossX = a[1] * b[2] - a[2] * b[1];
        float crossY = a[2] * b[0] - a[0] * b[2];
        float crossZ = a[0] * b[1] - a[1] * b[0];
        Matrix.rotateM(arcballMatrix, 0, angle, crossX, crossY, crossZ);
        Matrix.multiplyMM(rotateTranslate, 0, arcballMatrix, 0, translate, 0);
        Matrix.setIdentityM(translate, 0);
        Matrix.translateM(translate, 0, model.centerX, model.centerY, model.centerZ);
        Matrix.multiplyMM(worldToModel, 0, translate, 0, rotateTranslate, 0);
    }

//    public void switchModel() {
//        isCornellBox = !isCornellBox;
//        Matrix.setIdentityM(worldToModel, 0);
//        Matrix.setIdentityM(arcballMatrix, 0);
//        if (isCornellBox) {
//            glUseProgram(program);
//            uLightPos = glGetUniformLocation(program, "uLightPos");
//            glUniform3f(uLightPos, 10, 10, 10);
//            glUseProgram(0);
//            model = cornellBoxModel;
//        } else {
//            glUseProgram(program);
//            uLightPos = glGetUniformLocation(program, "uLightPos");
//            glUniform3f(uLightPos, 0, 1.50f, 0);
//            glUseProgram(0);
//            model = mitsubaModel;
//        }
//    }
}
