package entities;

import game.entities.Player;
import input.InputHandler;
import math.Vector3;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class Camera {
    private float distanceFromPlayer = 50;
    private float angleAroundPlayer = 0;
    private float CAMERA_LOOK_MULTIPLIER = 0.1f;
    private float ZOOM_MULTIPLIER = 1.5f;


    private Vector3 position = new Vector3(-15, 20, -15);
    private float pitch = 20;
    private float yaw;
    private float roll;
    private boolean movingCamera = false;

    private Player player;

    public Camera(Player player) {
        this.player = player;
        InputHandler.addMousewheelListener((Double dy) -> {
            this.distanceFromPlayer -= dy * ZOOM_MULTIPLIER;
        });
        InputHandler.addMouseMoveListener((Double dx, Double dy) -> {
            if (movingCamera) {
                this.pitch -= dy * CAMERA_LOOK_MULTIPLIER;
                this.angleAroundPlayer -= dx * CAMERA_LOOK_MULTIPLIER;
            }
        });
        InputHandler.addMouseClickListener((Integer button, Integer action, Integer mods) -> {
            if (button == GLFW_MOUSE_BUTTON_1) {
                if (action == GLFW_RELEASE) {
                    movingCamera = false;
                } else {
                    movingCamera = true;
                }
            }
        });
    }

    private float calculateHorizontalDistance() {
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }

    private float calculateVerticalDistance() {
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
    }

    private void calculateCameraPosition(float horizontald, float verticald) {
        float alpha = player.getrY() + angleAroundPlayer;
        float offsetX = (float) (horizontald * Math.sin(Math.toRadians(alpha)));
        float offsetZ = (float) (horizontald * Math.cos(Math.toRadians(alpha)));
        position.y = Math.max(player.getPosition().y + verticald, 3);
        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;
    }

    public void move() {
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
        this.yaw = 180 - (player.getrY() + angleAroundPlayer);
//        if (InputHandler.isKeyDown(GLFW_KEY_W)) {
//            position.add(0, 0, -2f);
//        }
//
//        if (InputHandler.isKeyDown(GLFW_KEY_S)) {
//            position.add(0, 0, 2f);
//        }
//
//        if (InputHandler.isKeyDown(GLFW_KEY_D)) {
//            position.add(2f, 0, 0);
//        }
//
//        if (InputHandler.isKeyDown(GLFW_KEY_A)) {
//            position.add(-2f, 0, 0);
//        }
//
//        if (InputHandler.isKeyDown(GLFW_KEY_E)) {
//            roll -= 1;
//        }
//
//        if (InputHandler.isKeyDown(GLFW_KEY_Q)) {
//            roll +=1;
//        }
//
//        if (InputHandler.isKeyDown(GLFW_KEY_UP)) {
//            pitch += 1;
//        }
//
//        if (InputHandler.isKeyDown(GLFW_KEY_DOWN)) {
//            pitch -= 1;
//        }
//
//        if (InputHandler.isKeyDown(GLFW_KEY_LEFT)) {
//            yaw += 1;
//        }
//
//        if (InputHandler.isKeyDown(GLFW_KEY_RIGHT)) {
//            yaw -= 1;
//        }
//
//        if (InputHandler.isKeyDown(GLFW_KEY_SPACE)) {
//            position.add(0, 2f, 0);
//        }
//
//
//        if (InputHandler.isKeyDown(GLFW_KEY_Z)) {
//            position.add(0, -2f, 0);
//        }
    }

    public Vector3 getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }
}
