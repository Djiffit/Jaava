package renderEngine.loaders;

import math.Vector2;
import math.Vector3;
import models.ModelData;
import models.Vertex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class OBJFileLoader {

    private static final String LOC = "res/";

    public static ModelData loadOBJ(String fileName) {
        List<Vertex> vertices = new ArrayList<>();
        List<Vector2> textures = new ArrayList<>();
        List<Vector3> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        File objFile = new File(LOC + fileName + ".obj");
        try (BufferedReader reader = new BufferedReader(new FileReader(objFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] currentLine = line.split(" ");
                if (line.startsWith("v ")) {
                    Vector3 vertex = new Vector3(Float.valueOf(currentLine[1]), Float.valueOf(currentLine[2]), Float.valueOf(currentLine[3]));
                    Vertex newVertex = new Vertex(vertices.size(), vertex);
                    vertices.add(newVertex);
                } else if (line.startsWith("vt ")) {
                    Vector2 texture = new Vector2(Float.valueOf(currentLine[1]), Float.valueOf(currentLine[2]));
                    textures.add(texture);
                } else if (line.startsWith("vn ")) {
                    Vector3 normal = new Vector3(Float.valueOf(currentLine[1]), Float.valueOf(currentLine[2]), Float.valueOf(currentLine[3]));
                    normals.add(normal);
                }
                else if (line.startsWith("f ")) {
                    String[] vertex1 = currentLine[1].split("/");
                    String[] vertex2 = currentLine[2].split("/");
                    String[] vertex3 = currentLine[3].split("/");
                    processVertex(vertex1, vertices, indices);
                    processVertex(vertex2, vertices, indices);
                    processVertex(vertex3, vertices, indices);
                }
            }
            System.out.println(normals.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        removeUnusedVertices(vertices);
        float[] verticesArray = new float[vertices.size() * 3];
        float[] texturesArray = new float[vertices.size() * 2];
        float[] normalsArray = new float[vertices.size() * 3];

        float furthest = convertDataToArrays(vertices, textures, normals, verticesArray, texturesArray, normalsArray);
        int[] indicesArray = convertIndicesListToArray(indices);
        ModelData data = new ModelData(verticesArray, texturesArray, normalsArray, indicesArray, furthest);
        return data;
    }

    private static void processVertex(String[] vertex, List<Vertex> vertices, List<Integer> indices) {
        int index = Integer.parseInt(vertex[0]) - 1;
        Vertex currentVertex = vertices.get(index);
        int textureIndex = Integer.parseInt(vertex[1]) - 1;
        int normalIndex = Integer.parseInt(vertex[2]) - 1;
        if (!currentVertex.isSet()) {
            currentVertex.setTextureIndex(textureIndex);
            currentVertex.setNormalIndex(normalIndex);
            indices.add(index);
        } else {
            dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices,
                    vertices);
        }
    }

    private static int[] convertIndicesListToArray(List<Integer> indices) {
        int[] indicesArray = new int[indices.size()];
        for (int i = 0; i < indicesArray.length; i++) {
            indicesArray[i] = indices.get(i);
        }
        return indicesArray;
    }

    private static float convertDataToArrays(List<Vertex> vertices, List<Vector2> textures,
                                             List<Vector3> normals, float[] verticesArray, float[] texturesArray,
                                             float[] normalsArray) {
        float furthestPoint = 0;
        for (int i = 0; i < vertices.size(); i++) {
            Vertex currentVertex = vertices.get(i);
            if (currentVertex.getLength() > furthestPoint) {
                furthestPoint = currentVertex.getLength();
            }
            Vector3 position = currentVertex.getPosition();
            Vector2 textureCoord = textures.get(currentVertex.getTextureIndex());
            Vector3 normalVector = normals.get(currentVertex.getNormalIndex());
            verticesArray[i * 3] = position.x();
            verticesArray[i * 3 + 1] = position.y();
            verticesArray[i * 3 + 2] = position.z();
            texturesArray[i * 2] = textureCoord.x();
            texturesArray[i * 2 + 1] = 1 - textureCoord.y();
            normalsArray[i * 3] = normalVector.x();
            normalsArray[i * 3 + 1] = normalVector.y();
            normalsArray[i * 3 + 2] = normalVector.z();
        }
        return furthestPoint;
    }

    private static void dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex,
                                                       int newNormalIndex, List<Integer> indices, List<Vertex> vertices) {
        if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
            indices.add(previousVertex.getIndex());
        } else {
            Vertex anotherVertex = previousVertex.getDuplicateVertex();
            if (anotherVertex != null) {
                dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex,
                        indices, vertices);
            } else {
                Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());
                duplicateVertex.setTextureIndex(newTextureIndex);
                duplicateVertex.setNormalIndex(newNormalIndex);
                previousVertex.setDuplicateVertex(duplicateVertex);
                vertices.add(duplicateVertex);
                indices.add(duplicateVertex.getIndex());
            }

        }
    }

    private static void removeUnusedVertices(List<Vertex> vertices){
        for(Vertex vertex:vertices){
            if(!vertex.isSet()){
                vertex.setTextureIndex(0);
                vertex.setNormalIndex(0);
            }
        }
    }
}
