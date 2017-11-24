package input;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import renderEngine.loop.MainEngine;
import utils.TriConsumer;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;

public class InputHandler extends GLFWKeyCallback {

    public static boolean[] keys = new boolean[65536];
    private static List<Consumer> mousewheel = new ArrayList<>();
    private static List<BiConsumer> mouseMove = new ArrayList<>();
    private static List<TriConsumer> mouseClick = new ArrayList<>();
    private static double mX;
    private static double mY;
    private static DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
    private static DoubleBuffer y = BufferUtils.createDoubleBuffer(1);

    public InputHandler() {
        GLFW.glfwSetScrollCallback(MainEngine.window, (win, dx, dy) -> mousewheel.forEach(func -> func.accept(dy)));
        GLFW.glfwSetMouseButtonCallback(MainEngine.window, (win, button, action, mods) -> mouseClick.forEach(func -> func.accept(button, action, mods)));
    }

    public static void addMousewheelListener(Consumer<Double> func) {
        mousewheel.add(func);
    }

    public static void addMouseMoveListener(BiConsumer<Double, Double> func) {
        mouseMove.add(func);
    }

    public static void addMouseClickListener(TriConsumer<Integer, Integer, Integer> func) {
        mouseClick.add(func);
    }

    public static void updateMousePosition() {
        x = BufferUtils.createDoubleBuffer(1);
        y = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(MainEngine.window, x, y);
        double newx = x.get();
        double newy = y.get();
        double deltax = newx - mX;
        double deltay = newy - mY;
        if (deltax != 0 || deltay != 0) {
            mouseMove.forEach(func -> func.accept(deltax, deltay));
        }
        mX = newx;
        mY = newy;
    }

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        keys[key] = action != GLFW_RELEASE;
    }

    public static boolean isKeyDown(int keycode) {
        return keys[keycode];
    }
}

