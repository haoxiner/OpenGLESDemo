package name.haoxin.demo;

import android.content.res.AssetManager;
import android.util.Log;

import static android.opengl.GLES20.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import name.haoxin.demo.util.objmodelloader.Parse;
import name.haoxin.demo.util.objmodelloader.builder.Build;
import name.haoxin.demo.util.objmodelloader.builder.Face;
import name.haoxin.demo.util.objmodelloader.builder.FaceVertex;
import name.haoxin.demo.util.objmodelloader.builder.Material;
import name.haoxin.demo.util.objmodelloader.builder.VertexGeometric;
import name.haoxin.demo.util.objmodelloader.builder.VertexNormal;

/**
 * Created by hx on 16/3/6.
 */
public class Model {
    private int shaderProgram;
    private int positionHandle;
    private int normalHandle;
    private int uvHandle;
    private int worldToCameraHandle;
    private int modelToWorldHandle;

    private int uAmbientHandle;
    private int uDiffuseHandle;
    private int uSpecularHandle;
    private int uShinessHandle;

    public float minX, minY, maxX, maxY, minZ, maxZ;
    public float centerX, centerY, centerZ;

    private List<TriangleMesh> meshes;

    public Model() {
        meshes = new ArrayList<>();
        minX = Float.MAX_VALUE;
        minY = Float.MAX_VALUE;
        minZ = Float.MAX_VALUE;
        maxX = Float.MIN_VALUE;
        maxY = Float.MIN_VALUE;
        maxZ = Float.MIN_VALUE;
    }

    public void addMesh(TriangleMesh mesh) {
        meshes.add(mesh);
    }

    public void setShaderProgram(int program) {
        shaderProgram = program;
        positionHandle = glGetAttribLocation(shaderProgram, "vPosition");
        worldToCameraHandle = glGetUniformLocation(shaderProgram, "uCamera");
        normalHandle = glGetAttribLocation(shaderProgram, "vNormal");
        uvHandle = glGetAttribLocation(shaderProgram, "vUv");
        modelToWorldHandle = glGetUniformLocation(shaderProgram, "uModel");
        uAmbientHandle = glGetUniformLocation(shaderProgram, "uMaterial.ambient");
        uSpecularHandle = glGetUniformLocation(shaderProgram, "uMaterial.specular");
        uDiffuseHandle = glGetUniformLocation(shaderProgram, "uMaterial.diffuse");
        uShinessHandle = glGetUniformLocation(shaderProgram, "uMaterial.shiness");
    }

    public void draw(float[] worldToCamera, float[] modelToWorld) {
        glUseProgram(shaderProgram);
        glUniformMatrix4fv(worldToCameraHandle, 1, false, worldToCamera, 0);
        glUniformMatrix4fv(modelToWorldHandle, 1, false, modelToWorld, 0);
        for (TriangleMesh m : meshes) {
            glUniform3fv(uAmbientHandle, 1, m.material.ambient, 0);
            glUniform3fv(uSpecularHandle, 1, m.material.specular, 0);
            glUniform3fv(uDiffuseHandle, 1, m.material.diffuse, 0);
            glUniform1f(uShinessHandle, m.material.shiness);
            m.draw(positionHandle, uvHandle, normalHandle);
        }
        glUseProgram(0);
    }

    public static Model load(AssetManager assetManager, String filename) {
        Build build = new Build();
        Parse parse = null;
        try {
            parse = new Parse(build, assetManager, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Model model = new Model();
        LinkedList<Material> materials = new LinkedList<>();
        ArrayList<ArrayList<Face>> facesByTextureList = createFaceListsByMaterial(build, materials);
        for (ArrayList<Face> faceList : facesByTextureList) {
            TriangleMesh mesh = new TriangleMesh();
            int index = 0;
            ArrayList<Face> triangleList = splitQuads(faceList);
            float[] buffer = new float[triangleList.size() * (3 + 2 + 3) * 3];
            for (Face face : triangleList) {
                for (FaceVertex vertex : face.vertices) {
                    if (vertex.v.x < model.minX) {
                        model.minX = vertex.v.x;
                    } else if (vertex.v.x > model.maxX) {
                        model.maxX = vertex.v.x;
                    }
                    if (vertex.v.y < model.minY) {
                        model.minY = vertex.v.y;
                    } else if (vertex.v.y > model.maxY) {
                        model.maxY = vertex.v.y;
                    }
                    if (vertex.v.z < model.minZ) {
                        model.minZ = vertex.v.z;
                    } else if (vertex.v.z > model.maxZ) {
                        model.maxZ = vertex.v.z;
                    }
                    buffer[index] = vertex.v.x;
                    index++;
                    buffer[index] = vertex.v.y;
                    index++;
                    buffer[index] = vertex.v.z;
                    index++;
                    if (vertex.t == null) {
                        buffer[index] = 0;
                        index++;
                        buffer[index] = 0;
                        index++;
                    } else {
                        buffer[index] = vertex.t.u;
                        index++;
                        buffer[index] = vertex.t.v;
                        index++;
                    }
                    if (vertex.n == null) {
                        face.calculateTriangleNormal();
                        VertexNormal n = face.faceNormal;
                        buffer[index] = n.x;
                        index++;
                        buffer[index] = n.y;
                        index++;
                        buffer[index] = n.z;
                        index++;
                    } else {
                        buffer[index] = vertex.n.x;
                        index++;
                        buffer[index] = vertex.n.y;
                        index++;
                        buffer[index] = vertex.n.z;
                        index++;
                    }

                }
            }
            mesh.commit(buffer);
            mesh.setMaterial(materials.pop());
            model.addMesh(mesh);
        }
        model.centerX = (model.maxX + model.minX) / 2;
        model.centerY = (model.maxY + model.minY) / 2;
        model.centerZ = (model.maxZ + model.minZ) / 2;
        return model;
    }

    // iterate over face list from builder, and break it up into a set of face lists by material, i.e. each for each face list, all faces in that specific list use the same material
    private static ArrayList<ArrayList<Face>> createFaceListsByMaterial(Build builder, LinkedList<Material> materials) {
        ArrayList<ArrayList<Face>> facesByTextureList = new ArrayList<ArrayList<Face>>();
        Material currentMaterial = null;
        ArrayList<Face> currentFaceList = new ArrayList<Face>();
        for (Face face : builder.faces) {
            if (face.material != currentMaterial) {
                if (!currentFaceList.isEmpty()) {
//                    log.log(INFO, "Adding list of " + currentFaceList.size() + " triangle faces with material " + currentMaterial + "  to our list of lists of faces.");
                    facesByTextureList.add(currentFaceList);
                }
//                log.log(INFO, "Creating new list of faces for material " + face.material);
                currentMaterial = face.material;
                materials.add(face.material);
                currentFaceList = new ArrayList<Face>();
            }
            currentFaceList.add(face);
        }
        if (!currentFaceList.isEmpty()) {
//            log.log(INFO, "Adding list of " + currentFaceList.size() + " triangle faces with material " + currentMaterial + "  to our list of lists of faces.");
            facesByTextureList.add(currentFaceList);
        }
        return facesByTextureList;
    }

    // VBOFactory can only handle triangles, not faces with more than 3 vertices.  There are much better ways to 'triangulate' polygons, that
    // can be used on polygons with more than 4 sides, but for this simple test code justsplit quads into two triangles
    // and drop all polygons with more than 4 vertices.  (I was originally just dropping quads as well but then I kept ending up with nothing
    // left to display. :-)  Or at least, not much. )
    private static ArrayList<Face> splitQuads(ArrayList<Face> faceList) {
        ArrayList<Face> triangleList = new ArrayList<Face>();
        for (Face face : faceList) {
            if (face.vertices.size() == 3) {
                triangleList.add(face);
            } else if (face.vertices.size() == 4) {
                FaceVertex v1 = face.vertices.get(0);
                FaceVertex v2 = face.vertices.get(1);
                FaceVertex v3 = face.vertices.get(2);
                FaceVertex v4 = face.vertices.get(3);
                Face f1 = new Face();
                f1.map = face.map;
                f1.material = face.material;
                f1.add(v1);
                f1.add(v2);
                f1.add(v3);
                triangleList.add(f1);
                Face f2 = new Face();
                f2.map = face.map;
                f2.material = face.material;
                f2.add(v1);
                f2.add(v3);
                f2.add(v4);
                triangleList.add(f2);
            }
        }
        return triangleList;
    }
}
