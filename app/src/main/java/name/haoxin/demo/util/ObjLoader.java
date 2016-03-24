package name.haoxin.demo.util;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.haoxin.demo.model.FaceGroup;
import name.haoxin.demo.model.Material;
import name.haoxin.demo.model.Model;

/**
 * Created by hx on 16/3/6.
 */
public class ObjLoader {
    public static Map<String, Material> loadMtl(String filename, AssetManager assetManager) throws IOException {
        Map<String, Material> mtlMap = new HashMap<>();
        InputStreamReader inputStreamReader = new InputStreamReader(assetManager.open(filename));
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        Material material = null;
        String materialName = null;

        while ((line = bufferedReader.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0) {
                continue;
            }
            if (line.startsWith("newmtl ")) {
                if (material != null) {
                    mtlMap.put(materialName, material);
                }
                material = new Material();
                materialName = line.substring(7).trim();
            } else if (line.startsWith("Kd ")) {
                String[] diffuse = line.substring(3).trim().split(" ");
                material.diffuse[0] = Float.parseFloat(diffuse[0]);
                material.diffuse[1] = Float.parseFloat(diffuse[1]);
                material.diffuse[2] = Float.parseFloat(diffuse[2]);
            } else if (line.startsWith("Ka ")) {
                String[] ambient = line.substring(3).trim().split(" ");
                material.ambient[0] = Float.parseFloat(ambient[0]);
                material.ambient[1] = Float.parseFloat(ambient[1]);
                material.ambient[2] = Float.parseFloat(ambient[2]);
            } else if (line.startsWith("Ks ")) {
                String[] specular = line.substring(3).trim().split(" ");
                material.specular[0] = Float.parseFloat(specular[0]);
                material.specular[1] = Float.parseFloat(specular[1]);
                material.specular[2] = Float.parseFloat(specular[2]);
            } else if (line.startsWith("Ns ")) {
                String shiness = line.substring(3).trim();
                material.shiness = Float.parseFloat(shiness);
            } else if (line.startsWith("map_Kd ")) {
                String textureFileName = line.substring(7).trim();
                material.loadTexture(assetManager.open(textureFileName));
            }
        }

        if (material != null) {
            mtlMap.put(materialName, material);
        }

        return mtlMap;
    }

    public static Model load(String filename, AssetManager assetManager) {
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        List<Float> verticesTable = new ArrayList<>();
        List<Float> uvCoordinatesTable = new ArrayList<>();
        List<Float> normalsTable = new ArrayList<>();

        List<Float> vertices = new ArrayList<>();
        List<Float> uvCoordinates = new ArrayList<>();
        List<Float> normals = new ArrayList<>();

        String line;
        FaceGroup faceGroup = null;
        Map<String, Material> mtlMap = null;

        Material material = new Material();
        material.setDefault();

        List<FaceGroup> faceGroups = new ArrayList<>();

        try {
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith("v ")) {
                    String[] floatStr = line.substring(2).trim().split(" ");
                    for (String str : floatStr) {
                        verticesTable.add(Float.parseFloat(str));
                    }
                } else if (line.startsWith("vn ")) {
                    String[] floatStr = line.substring(3).trim().split(" ");
                    for (String str : floatStr) {
                        normalsTable.add(Float.parseFloat(str));
                    }
                } else if (line.startsWith("vt ")) {
                    String[] floatStr = line.substring(3).trim().split(" ");
                    for (String str : floatStr) {
                        uvCoordinatesTable.add(Float.parseFloat(str));
                    }
                } else if (line.startsWith("f ")) {
                    String[] vertexStr = line.substring(2).trim().split(" ");
                    if (vertexStr.length == 3) {
                        for (String str : vertexStr) {
                            String[] valueArray = str.split("/");
                            if (valueArray.length > 0 && valueArray[0].length() > 0) {
                                int value = Integer.parseInt(valueArray[0]);
                                if (value > 0) {
                                    --value;
                                } else {
                                    value += (verticesTable.size() / 3);
                                }
                                vertices.add(verticesTable.get(value * 3));
                                vertices.add(verticesTable.get(value * 3 + 1));
                                vertices.add(verticesTable.get(value * 3 + 2));
                            }
                            if (valueArray.length > 1 && valueArray[1].length() > 0) {
                                int value = Integer.parseInt(valueArray[1]);
                                if (value > 0) {
                                    --value;
                                } else {
                                    value += (uvCoordinatesTable.size() / 2);
                                }
                                uvCoordinates.add(uvCoordinatesTable.get(value * 2));
                                uvCoordinates.add(uvCoordinatesTable.get(value * 2 + 1));
                            }
                            if (valueArray.length > 2 && valueArray[2].length() > 0) {
                                int value = Integer.parseInt(valueArray[2]);
                                if (value > 0) {
                                    --value;
                                } else {
                                    value += (normalsTable.size() / 3);
                                }
                                normals.add(normalsTable.get(value * 3));
                                normals.add(normalsTable.get(value * 3 + 1));
                                normals.add(normalsTable.get(value * 3 + 2));
                            }
                        }
                    }
                } else if (line.startsWith("usemtl ")) {
                    if (!vertices.isEmpty()) {
                        faceGroup = new FaceGroup(vertices, uvCoordinates, normals, material);
                        faceGroups.add(faceGroup);
                    }
                    vertices.clear();
                    uvCoordinates.clear();
                    normals.clear();

                    String materialName = line.substring(7).trim();
                    if (mtlMap != null && mtlMap.containsKey(materialName)) {
                        material = mtlMap.get(materialName);
                    }
                } else if (line.startsWith("mtllib ")) {
                    String mtlFileName = line.substring(7).trim();
                    try {
                        mtlMap = loadMtl(mtlFileName, assetManager);
                    } catch (IOException e) {
                        mtlMap = null;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStreamReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!vertices.isEmpty()) {
            faceGroup = new FaceGroup(vertices, uvCoordinates, normals, material);
            faceGroups.add(faceGroup);
        }
        vertices.clear();
        uvCoordinates.clear();
        normals.clear();
        normalsTable.clear();
        uvCoordinatesTable.clear();
        Model model = new Model(verticesTable, faceGroups);
        verticesTable.clear();
        return model;
    }

}
