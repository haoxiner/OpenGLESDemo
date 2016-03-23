package name.haoxin.demo.model;

import android.graphics.Bitmap;

import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glUniform1i;

/**
 * Created by hx on 16/3/6.
 */
public class Material {
    public float[] ambient = new float[3];
    public float[] diffuse = new float[3];
    public float[] specular = new float[3];
    public float shiness;
    public String textureFileName;

    public void setDefault() {
        for (int i = 0; i < 3; i++) {
            ambient[i] = 0.1f;
            diffuse[i] = 0.6f;
            specular[i] = 0.3f;
        }
        shiness = 20.0f;
        textureFileName = null;
    }
}
