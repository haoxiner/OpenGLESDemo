package name.haoxin.demo;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * Created by hx on 16/3/4.
 */
public class CustomGLSurfaceView extends GLSurfaceView {
    private float previousX;
    private float previousY;
    private float[] from = new float[4];
    private float[] to = new float[4];

    private CustomGLRenderer glRenderer;

    public CustomGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        glRenderer = new CustomGLRenderer(context);
        setRenderer(glRenderer);
        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        int w = getWidth();
        int h = getHeight();
        float currentX = (2 * x - w) / (float) (w);
        float currentY = (h - 2 * y) / (float) (h);

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(currentX - previousX) > 0.001f || Math.abs(currentY - previousY) > 0.001f) {
                    from[0] = previousX;
                    from[1] = previousY;
                    to[0] = currentX;
                    to[1] = currentY;
                    glRenderer.rotateArcball(from, to);
                    requestRender();
                }
                break;

            default:
        }

        previousX = currentX;
        previousY = currentY;
        return true;
    }
}
