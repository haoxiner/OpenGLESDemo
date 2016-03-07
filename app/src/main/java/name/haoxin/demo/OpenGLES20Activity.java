package name.haoxin.demo;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

/**
 * Created by hx on 16/3/4.
 */
public class OpenGLES20Activity extends Activity {
    private GLSurfaceView m_GLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_GLView = new CustomGLSurfaceView(this);
        setContentView(m_GLView);
    }
}
