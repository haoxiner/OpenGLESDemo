package name.haoxin.demo;

import android.content.Context;

import static android.opengl.GLES20.*;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import name.haoxin.demo.model.Model;
import name.haoxin.demo.shader.Shader;
import name.haoxin.demo.shader.ShaderProgram;
import name.haoxin.demo.util.ObjLoader;
import name.haoxin.demo.util.TextResourceReader;

/**
 * Created by hx on 16/3/4.
 */
public class CustomGLRenderer implements GLSurfaceView.Renderer {
    private Camera camera;
    private Model model;
    private Context context;
    private ShaderProgram shaderProgram;
    private ArcBall arcBall;
    private float distance;
    private boolean ready;

    public CustomGLRenderer(Context context) {
        this.context = context;
        camera = new Camera();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // opengl settings
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glEnable(GL_CULL_FACE);
        glFrontFace(GL_CCW);
        glCullFace(GL_BACK);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        // model

        model = ObjLoader.load("teapot.obj", context.getAssets());

        distance = 150;
        // shader
        Shader vShader = new Shader(GL_VERTEX_SHADER, TextResourceReader.readTextFileFromResource(context, R.raw.phongv));
        Shader fShader = new Shader(GL_FRAGMENT_SHADER, TextResourceReader.readTextFileFromResource(context, R.raw.phongf));
        shaderProgram = new ShaderProgram(vShader, fShader);
        shaderProgram.use();
        shaderProgram.setViewPosition(model.centerX, model.centerY, model.centerZ + distance);
        shaderProgram.setLightPosition(model.centerX, model.centerY, model.centerZ + distance);
        shaderProgram.release();
        // arcball
        arcBall = new ArcBall(model.centerX, model.centerY, model.centerZ);
        ready = true;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (ready) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            camera.lookAt(model.centerX, model.centerY, model.centerZ + distance, model.centerX, model.centerY, model.centerZ, 0f, 1.0f, 0.0f);
            shaderProgram.use();
            shaderProgram.setWorldToCameraMatrix(camera.getWorldToCameraMatrix());
            shaderProgram.setModelToWorldMatrix(arcBall.getModelToWorldMatrix());
            model.draw(shaderProgram);
            shaderProgram.release();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        camera.frustum(-ratio, ratio, -1, 1, 1.0f, 1000.0f);
    }

    public void rotateArcball(float[] v0, float v1[]) {
        if (ready) {
            arcBall.rotate(v0, v1);
        }
    }
}
