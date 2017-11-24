package renderEngine.loop;

import math.Maths;
import math.Matrix4;
import math.Vector3;
import models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shaders.TerrainShader;
import terrain.Terrain;
import textures.TerrainTexturePack;

import java.util.List;

public class TerrainRenderer {

    private TerrainShader shader;
    public TerrainRenderer(TerrainShader shader, Matrix4 projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.loadTextureUnit();
        shader.stop();
    }

    public void render(List<Terrain> terrains) {
        for (Terrain t : terrains) {
            prepareTerrain(t);
            loadModelMatrix(t);
            prepareTerrain(t);
            GL11.glDrawElements(GL11.GL_TRIANGLES, t.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            unbindTexturedModel();
        }
    }

    public void prepareTerrain(Terrain terrain) {
        RawModel model = terrain.getModel();
        GL30.glBindVertexArray(model.getVaoID());
        // Activate attributelist
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        bindTextures(terrain);
//        shader.loadShineVariables(terrain.getTexture().getShineDampener(), terrain.getTexture().getReflectivity());
        shader.loadShineVariables(1, 0);
    }

    private void bindTextures(Terrain terrain) {
        TerrainTexturePack pack = terrain.getTexturePack();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, pack.getBackgroundTexture().getTextureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, pack.getrTexture().getTextureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, pack.getbTexture().getTextureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, pack.getgTexture().getTextureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE4);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getTextureID());

    }

    public void unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    private void loadModelMatrix(Terrain terrain) {
        Matrix4 transformationMatrix = Maths.createTransformationMatrix(new Vector3(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
        shader.loadTransformationMatrix(transformationMatrix);
    }
}
