package renderEngine.loop;

import entities.Camera;
import entities.Entity;
import entities.Light;
import math.Matrix4;
import models.TexturedModel;
import org.lwjgl.opengl.GL11;
import shaders.StaticShader;
import shaders.TerrainShader;
import terrain.Terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer {

    private StaticShader shader = new StaticShader();
    private Matrix4 projectionMatrix;

    private static final float FOV = 105;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000000f;
    private static final float RED = 0.05f;
    private static final float GREEN = 0.7f;
    private static final float BLUE = 0.95f;

    private EntityRenderer entityRenderer;
    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader = new TerrainShader();

    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();
    private List<Terrain> terrains = new ArrayList<>();

    public MasterRenderer() {
        enableCulling();
        createProjectionMatrix();
        entityRenderer = new EntityRenderer(shader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
    }

    public static void enableCulling() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void disableCulling() {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public void render(Light light, Camera camera) {
        prepare();
        shader.start();
        shader.loadSkyColor(RED, GREEN, BLUE);
        shader.loadLight(light);
        shader.loadViewMatrix(camera);
        entityRenderer.render(entities);
        shader.stop();
        terrainShader.start();
        terrainShader.loadSkyColor(RED, GREEN, BLUE);
        terrainShader.loadLight(light);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrains.clear();
        entities.clear();
    }

    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClearColor(RED, GREEN, BLUE, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if (batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    public void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    public void cleanUp() {
        shader.cleanUp();
        terrainShader.cleanUp();
    }

    public void createProjectionMatrix() {
        float aspectRatio = (float) MainEngine.getWidth() / (float) MainEngine.getHeight();
        float y_scale = (float)(1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio;
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4();
        float matrix[] = {x_scale, 0, 0, 0,
                0, y_scale, 0, 0,
                0, 0, -((FAR_PLANE + NEAR_PLANE) / frustum_length), -1,
                0, 0, -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length), 0};

        projectionMatrix.set(matrix);

    }
}
