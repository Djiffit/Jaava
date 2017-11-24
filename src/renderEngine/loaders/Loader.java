package renderEngine.loaders;

import models.RawModel;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

public class Loader {

    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();

    public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normals);
        unbindVAO();
        return new RawModel(vaoID, indices.length);
    }

    private int createVAO() {
        // Create vertex array
        int vaoID = GL30.glGenVertexArrays();
        vaos.add(vaoID);
        // Bind array to id
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    public void cleanUp() {
        vaos.forEach(GL30::glDeleteVertexArrays);
        vbos.forEach(GL15::glDeleteBuffers);
        textures.forEach(GL11::glDeleteTextures);
    }

    public int loadTexture(String fileName) {
        try (FileInputStream is = new FileInputStream("res/" + fileName + ".png")) {
            BufferedImage image = TextureLoader.loadImage(is);
            int textureID = TextureLoader.loadTexture(image);
            glBindTexture(GL_TEXTURE_2D, textureID);
            GL30.glGenerateMipmap(GL_TEXTURE_2D);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.5f);

            textures.add(textureID);
            return textureID;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        // Bind buffer to id
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        // Put VBO into VAO
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
        // Unbind current VBO
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    // Use index buffers to draw elements instead of specifying all vertices
    private void bindIndicesBuffer(int[] indices) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private void unbindVAO() {
        GL30.glBindVertexArray(0);
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        // Create buffer for writing data and put data into the buffer
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        // Flip buffer so it is ready for reading, since we are done writing into it
        buffer.flip();
        return buffer;
    }
}
