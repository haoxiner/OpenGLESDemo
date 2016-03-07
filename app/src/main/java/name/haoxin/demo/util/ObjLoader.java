package name.haoxin.demo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import name.haoxin.demo.Model;
import name.haoxin.demo.TriangleMesh;
import name.haoxin.demo.Vertex;

/**
 * Created by hx on 16/3/6.
 */
public class ObjLoader {
    public static Model load(InputStream inputStream) {
        Model model = new Model();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        TriangleMesh mesh = null;
        Vertex vertex = null;

        List<Float> vertices = new ArrayList<>();
        List<Float> uvCoordinates = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        try {
            while ((line = bufferedReader.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith("v ")) {
                    String[] floatStr = line.substring(2).split(" ");
                    for (String str : floatStr) {
                        vertices.add(Float.parseFloat(str));
                    }
                } else if (line.startsWith("vn ")) {
                    String[] floatStr = line.substring(2).split(" ");
                    for (String str : floatStr) {
                        normals.add(Float.parseFloat(str));
                    }
                } else if (line.startsWith("vt ")) {
                    String[] floatStr = line.substring(2).split(" ");
                    for (String str : floatStr) {
                        uvCoordinates.add(Float.parseFloat(str));
                    }
                } else if (line.startsWith("f ")) {
                    String[] vertexStr = line.substring(2).split(" ");
                    for (String str : vertexStr) {
                        indices.add(Integer.parseInt(str.split("/")[0]) - 1);
                    }
                } else if (line.startsWith("g")) {
                    if (line.startsWith("g default")) {
                        vertices.clear();
                        indices.clear();
                    } else {

                    }
                } else if (line.startsWith("usemtl ")) {
//                    String mtlName = line.substring(7);
//                    int mtlId;
//                    mesh = new TriangleMesh();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        model.commitVertices(vertices);
        model.commitIndices(indices);
        return model;
    }

}
