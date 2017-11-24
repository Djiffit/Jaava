package renderEngine.loop;

import entities.Camera;
import entities.Entity;
import entities.Light;
import game.entities.Player;
import input.InputHandler;
import math.Vector3;
import models.ModelData;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import renderEngine.loaders.Loader;
import renderEngine.loaders.OBJFileLoader;
import terrain.Terrain;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import textures.TextureModel;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class MainEngine {

    // The window handle
    public static long window;
    private static int WIDTH = 1920;
    private static int HEIGHT = 1080;
    private GLFWKeyCallback keyCallback;

    private static double lastFrameTime;
    private static double delta;

    public void run() {
        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "GAME MASTER", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
        GLFWWindowSizeCallback cbfunc = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                updateSizes(width, height);
            }
        };
        glfwSetWindowSizeCallback(window, cbfunc);
        glfwSetKeyCallback(window, keyCallback = new InputHandler());
    }

    private void updateSizes(int width, int height) {
        this.WIDTH = width;
        this.HEIGHT = height;
    }

    private static double getCurrentTime() {
        return (org.lwjgl.glfw.GLFW.glfwGetTime());
    }

    public static int getHeight() {
        return HEIGHT;
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
//        glClearColor(0f, 0f, 0f, 0f);
        float[] vertices = {
                -0.5f,0.5f,-0.5f,
                -0.5f,-0.5f,-0.5f,
                0.5f,-0.5f,-0.5f,
                0.5f,0.5f,-0.5f,

                -0.5f,0.5f,0.5f,
                -0.5f,-0.5f,0.5f,
                0.5f,-0.5f,0.5f,
                0.5f,0.5f,0.5f,

                0.5f,0.5f,-0.5f,
                0.5f,-0.5f,-0.5f,
                0.5f,-0.5f,0.5f,
                0.5f,0.5f,0.5f,

                -0.5f,0.5f,-0.5f,
                -0.5f,-0.5f,-0.5f,
                -0.5f,-0.5f,0.5f,
                -0.5f,0.5f,0.5f,

                -0.5f,0.5f,0.5f,
                -0.5f,0.5f,-0.5f,
                0.5f,0.5f,-0.5f,
                0.5f,0.5f,0.5f,

                -0.5f,-0.5f,0.5f,
                -0.5f,-0.5f,-0.5f,
                0.5f,-0.5f,-0.5f,
                0.5f,-0.5f,0.5f

        };

        float[] textureCoords = {

                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0


        };

        int[] indices = {
                0,1,3,
                3,1,2,
                4,5,7,
                7,5,6,
                8,9,11,
                11,9,10,
                12,13,15,
                15,13,14,
                16,17,19,
                19,17,18,
                20,21,23,
                23,21,22

        };

        Loader loader = new Loader();
//        RawModel model = OBJLoader.loadObjModel("grassModel", loader);
//        TextureModel textureModel = new TextureModel(loader.loadTexture("grassTexture"));
//        textureModel.setShineDampener(5);
//        textureModel.setReflectivity(1);
//        textureModel.setHasTransparency(true);
//        textureModel.setFakeLightning(true);
//
//        TexturedModel grass = new TexturedModel(model, textureModel);
//
//        RawModel fernm = OBJLoader.loadObjModel("fern", loader);
//        TextureModel fernModel = new TextureModel(loader.loadTexture("fern"));
////        fernModel.setShineDampener(5);
////        fernModel.setReflectivity(1);
//        fernModel.setHasTransparency(true);
////        fernModel.setFakeLightning(true);
//        TexturedModel fern = new TexturedModel(fernm, fernModel);
//
////        ModelData treeData = OBJFileLoader.loadOBJ("tree1");
////        RawModel treem = loader.loadToVAO(treeData.getVertices(), treeData.getTextureCoords(), treeData.getNormals(), treeData.getIndices());
////        TextureModel treeMo = new TextureModel(loader.loadTexture("tree"));
////        treeModel.setShineDampener(5);
////        treeModel.setReflectivity(1);
////        treeModel.setHasTransparency(false);
////        treeModel.setFakeLightning(true);

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grasst"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirtt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassfield"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("pavement"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, bTexture, gTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        TexturedModel treeModel = loadTexturedModel("lowPolyTree", "lowPolyTree", loader, false);
        TexturedModel grassModel = loadTexturedModel("grassi", "flower", loader, false);
        TexturedModel fernModel = loadTexturedModel("fern", "fern", loader, false);

        TexturedModel[] models = {treeModel, treeModel, grassModel, fernModel};
        List<Entity> entities = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 3000; i++) {
            int index = (int) (Math.random()* 4);
            entities.add(new Entity(models[index], new Vector3(-random.nextFloat()* 900, 0, -random.nextFloat() * 900), 0, 0, 0, 2));

        }

        Light light = new Light( new Vector3(-400, 1000, -400), new Vector3(1, 1, 0));

        TextureModel terrainTexture = new TextureModel(loader.loadTexture("grass"));
//        terrainTexture.setShineDampener(5);
//        terrainTexture.setReflectivity(1);
        Terrain terrain = new Terrain(-1, -1, loader, texturePack, blendMap);
        TexturedModel bunny = loadTexturedModel("bunny", "grey", loader, true);
        Player player = new Player(bunny, new Vector3(-30, 0, -60), 0, 0, 0, 1);
        Camera camera = new Camera(player);
        MasterRenderer renderer = new MasterRenderer();
        lastFrameTime = getCurrentTime();



        while ( !glfwWindowShouldClose(window) ) {
            double currentTime = getCurrentTime();
            InputHandler.updateMousePosition();
            delta = (currentTime - lastFrameTime);
            renderer.processEntity(player);
            lastFrameTime = currentTime;
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            glfwPollEvents();
            camera.move();
            player.move();
            renderer.processTerrain(terrain);
            for(Entity entity:entities){
                renderer.processEntity(entity);
            }
            renderer.render(light, camera);
            glfwSwapBuffers(window); // swap the color buffers
            if (InputHandler.isKeyDown(GLFW_KEY_ESCAPE)) {
                glfwSetWindowShouldClose(window, true);
            }
        }
        renderer.cleanUp();
        loader.cleanUp();
    }


    public static int getWidth() {
        return WIDTH;
    }

    public static void main(String[] args) {
        new MainEngine().run();
    }

    private static TexturedModel loadTexturedModel(String modelFileName, String textureFileName, Loader loader, boolean shiny) {
        final ModelData data = OBJFileLoader.loadOBJ(modelFileName);
        final RawModel rawModel = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(),
                data.getIndices());
        TexturedModel newModel = new TexturedModel(rawModel, new TextureModel(loader.loadTexture(textureFileName)));
        if (shiny) {
            newModel.getTexture().setReflectivity(1);
            newModel.getTexture().setShineDampener(2);
        }
        return newModel;
    }

    public static double getFrameTimeSeconds() {
        return delta;
    }

}