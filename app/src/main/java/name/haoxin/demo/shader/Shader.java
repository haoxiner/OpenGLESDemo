package name.haoxin.demo.shader;

import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glShaderSource;

/**
 * Created by hx on 16/3/22.
 */
public class Shader {
    private int id;

    public Shader(int type, String shaderCode) {
        id = glCreateShader(type);
        glShaderSource(id, shaderCode);
        glCompileShader(id);
    }

    public int getId() {
        return id;
    }

    public void delete() {
        if (id != 0) {
            glDeleteShader(id);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        delete();
    }
}
