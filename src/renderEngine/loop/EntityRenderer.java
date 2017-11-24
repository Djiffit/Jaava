package renderEngine.loop;

import entities.Entity;
import math.Maths;
import math.Matrix4;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shaders.StaticShader;
import textures.TextureModel;

import java.util.List;
import java.util.Map;

public class EntityRenderer {


    private StaticShader shader;

    public EntityRenderer(StaticShader shader, Matrix4 projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }


    public void render(Map<TexturedModel, List<Entity>> entities) {
        for( TexturedModel model : entities.keySet()) {
            prepareTexturedModel(model);
            List<Entity> batch = entities.get(model);
            batch.forEach(e -> {
                prepareInstance(e);
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            });
        }
        unbindTexturedModel();
    }

    public void prepareTexturedModel(TexturedModel texturedModel) {
        RawModel model = texturedModel.getRawModel();
        TextureModel texture = texturedModel.getTexture();
        GL30.glBindVertexArray(model.getVaoID());
        // Activate attributelist
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        if (texture.isHasTransparency()) {
            MasterRenderer.disableCulling();
        }

        shader.loadFakeLighting(texture.isHasTransparency());
        shader.loadShineVariables(texture.getShineDampener(), texture.getReflectivity());
        // Texture
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedModel.getTexture().getTextureID());
    }

    public void unbindTexturedModel() {
        MasterRenderer.enableCulling();
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    private void prepareInstance(Entity entity) {
        Matrix4 transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getrX(), entity.getrY(), entity.getrZ(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
    }

//    public void render(Entity entity, StaticShader shader) {
//        TexturedModel texturedModel = entity.getModel();
//        // Bind vao to be used
//
//
//        // Send matrix to shader
//
//        shader.loadShineVariables(texturedModel.getTexture().getShineDampener(), texturedModel.getTexture().getReflectivity());
//        // Texture
//        GL13.glActiveTexture(GL13.GL_TEXTURE0);
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedModel.getTexture().getTextureID());
//        // Draw the model
//        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
//        // Disable list
//        GL20.glDisableVertexAttribArray(0);
//        GL20.glDisableVertexAttribArray(1);
//        GL20.glDisableVertexAttribArray(2);
//        GL30.glBindVertexArray(0);
//    }
}
