package name.haoxin.demo;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by hx on 16/3/4.
 */
public class OpenGLES20Activity extends Activity {
    private CustomGLSurfaceView glView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.loading);
        setContentView(new CustomGLSurfaceView(this));
    }
}
