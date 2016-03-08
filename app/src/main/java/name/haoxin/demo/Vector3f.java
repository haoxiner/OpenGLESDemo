package name.haoxin.demo;

/**
 * Created by hx on 16/3/8.
 */
public class Vector3f {
    public static float Dot(float[] a, float[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    public static float Dot(float x1, float y1, float z1, float x2, float y2, float z2) {
        return x1 * x2 + y1 * y2 + z1 * z2;
    }

    public static float[] Cross(float x1, float y1, float z1, float x2, float y2, float z2) {
        float[] c = new float[3];
        c[0] = y1 * z2 - y2 * z1;
        c[1] = x2 * z1 - x1 * z2;
        c[2] = x1 * y2 - x2 * y1;
        return c;
    }

    public static float[] Cross(float[] a, float[] b) {
        float[] c = new float[3];
        c[0] = a[1] * b[2] - b[1] * a[2];
        c[1] = b[0] * a[2] - a[0] * b[2];
        c[2] = a[0] * b[1] + b[0] * a[1];
        return c;
    }
}
