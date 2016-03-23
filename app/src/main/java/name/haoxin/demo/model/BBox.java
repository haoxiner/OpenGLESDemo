package name.haoxin.demo.model;

/**
 * Created by hx on 16/3/22.
 */
public class BBox {
    public float minX, minY, maxX, maxY, minZ, maxZ;

    public BBox() {
        minX = Float.MAX_VALUE;
        minY = Float.MAX_VALUE;
        minZ = Float.MAX_VALUE;
        maxX = Float.MIN_VALUE;
        maxY = Float.MIN_VALUE;
        maxZ = Float.MIN_VALUE;
    }

    public void union(float x, float y, float z) {
        if (x < minX) {
            minX = x;
        } else if (x > maxX) {
            maxX = x;
        }
        if (y < minY) {
            minY = y;
        } else if (y > maxY) {
            maxY = y;
        }
        if (z < minZ) {
            minZ = z;
        } else if (z > maxZ) {
            maxZ = z;
        }
    }
}
