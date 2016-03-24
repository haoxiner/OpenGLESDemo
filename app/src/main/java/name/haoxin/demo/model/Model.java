package name.haoxin.demo.model;

import java.util.List;

import name.haoxin.demo.shader.ShaderProgram;

/**
 * Created by hx on 16/3/6.
 */
public class Model {
    private List<FaceGroup> faceGroups;
    public BBox bbox;
    public float centerX, centerY, centerZ;

    public Model(List<Float> vertices, List<FaceGroup> faceGroups) {
        this.faceGroups = faceGroups;
        bbox = new BBox();
        for (int i = 0; i < vertices.size(); i += 3) {
            bbox.union(vertices.get(i), vertices.get(i + 1), vertices.get(i + 2));
        }
        centerX = (bbox.maxX + bbox.minX) / 2;
        centerY = (bbox.maxY + bbox.minY) / 2;
        centerZ = (bbox.maxZ + bbox.minZ) / 2;
    }

    public void draw(ShaderProgram shaderProgram) {
        for (FaceGroup faceGroup : faceGroups) {
            faceGroup.draw(shaderProgram);
        }
    }
}
