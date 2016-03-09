package name.haoxin.demo;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * Created by hx on 16/3/4.
 */
public class CustomGLSurfaceView extends GLSurfaceView {
    private final CustomGLRenderer glRenderer;

    public CustomGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        glRenderer = new CustomGLRenderer(context);

        setRenderer(glRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private float previousX;
    private float previousY;
    private float[] v0 = new float[4];
    private float[] v1 = new float[4];

    private float slideOldX, slideOldY;
    private boolean slide;

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
//                if (slide) {
//                    slide = false;
//                    if (Math.abs(e.getX() - slideOldX) > getWidth() / 3) {
//                        glRenderer.switchModel();
//                    }
//                } else {
                v0[0] = previousX;
                v0[1] = previousY;
                v1[0] = currentX;
                v1[1] = currentY;
                glRenderer.rotateArcball(v0, v1);
                requestRender();
//                }
                break;
//            case MotionEvent.ACTION_POINTER_DOWN:
//                slideOldX = e.getX();
//                slideOldY = e.getY();
//                slide = true;
//                break;
//            case MotionEvent.ACTION_POINTER_UP:
//                slide = false;
//                break;
            default:
        }

        previousX = currentX;
        previousY = currentY;
        return true;
    }
}
