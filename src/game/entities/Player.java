package game.entities;

import entities.Entity;
import input.InputHandler;
import math.Vector3;
import models.TexturedModel;
import renderEngine.loop.MainEngine;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Entity {


    private static final float RUN_SPEED = 80;
    private static final float TURN_SPEED = 360;
    private static final float GRAVITY = -50;
    private static final float JUMP = 33;

    private static final float TERRAIN_FLOOR = 0;
    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;

    private boolean isInAir = false;

    public Player(TexturedModel model, Vector3 position, float rX, float rY, float rZ, float scale) {
        super(model, position, rX, rY, rZ, scale);
    }

    public void move() {
        checkInputs();
        float deltaTime = (float) MainEngine.getFrameTimeSeconds();
        super.increaseRotation(0, currentTurnSpeed * deltaTime, 0);
        float distance = currentSpeed * deltaTime;
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getrY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getrY())));
        super.increasePosition(dx, 0, dz);
        upwardsSpeed += GRAVITY * deltaTime;
        super.increasePosition(0, upwardsSpeed * deltaTime, 0);
        if (super.getPosition().y() < TERRAIN_FLOOR) {
            upwardsSpeed = 0;
            isInAir = false;
            super.setPosition(new Vector3(super.getPosition().x(), TERRAIN_FLOOR, super.getPosition().z()));
        }
    }

    private void jump() {
        if (!isInAir) {
            this.upwardsSpeed = JUMP;
            isInAir = true;
        }
    }

    private void checkInputs() {
        if (InputHandler.isKeyDown(GLFW_KEY_W)) {
            this.currentSpeed = RUN_SPEED;
        } else if (InputHandler.isKeyDown(GLFW_KEY_S)) {
            this.currentSpeed = -RUN_SPEED;
        } else {
            this.currentSpeed = 0;
        }

        if (InputHandler.isKeyDown(GLFW_KEY_D)) {
            this.currentTurnSpeed = -TURN_SPEED;
        } else if (InputHandler.isKeyDown(GLFW_KEY_A)) {
            this.currentTurnSpeed = TURN_SPEED;
        } else {
            this.currentTurnSpeed = 0;
        }

        if (InputHandler.isKeyDown(GLFW_KEY_SPACE)) {
            jump();
        }
    }
}
