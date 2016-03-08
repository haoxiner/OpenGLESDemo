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

        List<Float> verticesTable = new ArrayList<>();
        List<Float> uvCoordinatesTable = new ArrayList<>();
        List<Float> normalsTable = new ArrayList<>();

        List<Integer> indices = new ArrayList<>();
        List<Float> vertices = new ArrayList<>();
        List<Float> uvCoordinates = new ArrayList<>();
        List<Float> normals = new ArrayList<>();

        boolean hasOldMesh = false;
        boolean switchMaterial = true;

        try {
            while ((line = bufferedReader.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith("v ")) {
                    if (hasOldMesh) {
                        mesh = new TriangleMesh();
                        mesh.commit(vertices, uvCoordinates, normals);
                        model.addMesh(mesh);
                        verticesTable.clear();
                        normalsTable.clear();
                        uvCoordinatesTable.clear();
                        vertices.clear();
                        indices.clear();
                        normals.clear();
                        uvCoordinates.clear();
                        hasOldMesh = false;
                    }
                    String[] floatStr = line.substring(2).split(" ");
                    for (String str : floatStr) {
                        verticesTable.add(Float.parseFloat(str));
                    }
                } else if (line.startsWith("vn ")) {
                    String[] floatStr = line.substring(3).split(" ");
                    for (String str : floatStr) {
                        normalsTable.add(Float.parseFloat(str));
                    }
                } else if (line.startsWith("vt ")) {
                    String[] floatStr = line.substring(3).split(" ");
                    for (String str : floatStr) {
                        uvCoordinatesTable.add(Float.parseFloat(str));
                    }
                } else if (line.startsWith("f ")) {
                    if (switchMaterial) {
                        hasOldMesh = true;
                        switchMaterial = false;
                    }
                    String[] vertexStr = line.substring(2).split(" ");
                    for (String str : vertexStr) {
                        String[] valueArray = str.split("/");
                        if (valueArray.length > 0 && valueArray[0].length() > 0) {
                            int value = Integer.parseInt(valueArray[0]);
                            int pos = 0;
                            if (value > 0) {
                                pos = (value - 1) * 3;
                            } else {
                                pos = value * 3 + verticesTable.size();
                            }
                            vertices.add(verticesTable.get(pos));
                            pos++;
                            vertices.add(verticesTable.get(pos));
                            pos++;
                            vertices.add(verticesTable.get(pos));
                        }
                        if (valueArray.length > 1 && valueArray[1].length() > 0) {
                            int value = Integer.parseInt(valueArray[1]);
                            int pos = 0;
                            if (value > 0) {
                                pos = (value - 1) * 2;
                            } else {
                                pos = value * 2 + uvCoordinatesTable.size();
                            }
                            uvCoordinates.add(uvCoordinatesTable.get(pos));
                            pos++;
                            uvCoordinates.add(uvCoordinatesTable.get(pos));
                        }
                        if (valueArray.length > 2 && valueArray[2].length() > 0) {
                            int value = Integer.parseInt(valueArray[2]);
                            int pos = 0;
                            if (value > 0) {
                                pos = (value - 1) * 3;
                            } else {
                                pos = value * 3 + normalsTable.size();
                            }
                            normals.add(normalsTable.get(pos));
                            pos++;
                            normals.add(normalsTable.get(pos));
                            pos++;
                            normals.add(normalsTable.get(pos));
                        }
                    }
                } else if (line.startsWith("usemtl ")) {
//                    String mtlName = line.substring(7);
//                    int mtlId;
//                    mesh = new TriangleMesh();
                    switchMaterial = true;
                } else if (line.startsWith("mtllib ")) {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (hasOldMesh) {
            mesh = new TriangleMesh();
            mesh.commit(vertices, uvCoordinates, normals);
            model.addMesh(mesh);
        }
        return model;
    }

}
