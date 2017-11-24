package renderEngine.loaders;

import math.Vector2;
import math.Vector3;
import models.RawModel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OBJLoader {

    public static RawModel loadObjModel(String fileName, Loader loader) {
        try (FileReader fr = new FileReader(new File("res/" + fileName + ".obj"))) {
            BufferedReader reader = new BufferedReader(fr);
            String line;
            List<Vector3> vertices = new ArrayList<>();
            List<Vector2> textures = new ArrayList<>();
            List<Vector3> normals = new ArrayList<>();
            List<Integer> indices = new ArrayList<>();
            float[] verticesArray = {};
            float[] normalsArray = {};
            float[] textureArray = {};
            int[] indicesArray = {};
            while (true) {
                line = reader.readLine();
                String[] currentLine = line.split(" ");
                if (line.startsWith("v ")) {
                    Vector3 vertex = new Vector3(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    vertices.add(vertex);
                } else if (line.startsWith("vt ")) {
                    Vector2 texture = new Vector2(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]));
                    textures.add(texture);
                } else if (line.startsWith("vn ")) {
                    Vector3 vertex = new Vector3(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    normals.add(vertex);
                } else if (line.startsWith("f ")) {
                    textureArray = new float[vertices.size() * 2];
                    normalsArray = new float[vertices.size() * 3];
                    break;
                }
            }

            while (line != null) {
                if (!line.startsWith("f ")) {
                    line = reader.readLine();
                    continue;
                }
                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");
                processVertex(vertex1, indices, textures, normals, textureArray, normalsArray);
                processVertex(vertex2, indices, textures, normals, textureArray, normalsArray);
                processVertex(vertex3, indices, textures, normals, textureArray, normalsArray);
                line = reader.readLine();
            }
            verticesArray = new float[vertices.size() * 3];
            indicesArray = new int[indices.size()];

            int vertexpointer = 0;
            for (Vector3 v : vertices) {
                verticesArray[vertexpointer++] = v.x();
                verticesArray[vertexpointer++] = v.y();
                verticesArray[vertexpointer++] = v.z();
            }

            for (int i = 0; i < indices.size(); i++) {
                indicesArray[i] = indices.get(i);
            }

            return loader.loadToVAO(verticesArray, textureArray, normalsArray, indicesArray);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector2> textures, List<Vector3> normals, float[] textureArray, float[] normalsArray) {
        int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
        indices.add(currentVertexPointer);
        Vector2 currentTex = textures.get(Integer.parseInt(vertexData[1]) -1);
        textureArray[currentVertexPointer*2] = currentTex.x();
        textureArray[currentVertexPointer*2 + 1] = 1 - currentTex.y();
        Vector3 currentNorm = normals.get(Integer.parseInt(vertexData[2]) - 1);
        normalsArray[currentVertexPointer * 3] = currentNorm.x();
        normalsArray[currentVertexPointer * 3 + 1] = currentNorm.y();
        normalsArray[currentVertexPointer * 3 + 2] = currentNorm.z();
    }

}
