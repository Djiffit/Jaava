package entities;

import math.Vector3;
import models.TexturedModel;

public class Entity {
    private TexturedModel model;
    private Vector3 position;
    private float rX, rY, rZ;
    private float scale;

    public Entity(TexturedModel model, Vector3 position, float rX, float rY, float rZ, float scale) {
        this.model = model;
        this.position = position;
        this.rX = rX;
        this.rY = rY;
        this.rZ = rZ;
        this.scale = scale;
    }

    public TexturedModel getModel() {
        return model;
    }

    public void setModel(TexturedModel model) {
        this.model = model;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public float getrX() {
        return rX;
    }

    public void setrX(float rX) {
        this.rX = rX;
    }

    public float getrY() {
        return rY;
    }

    public void setrY(float rY) {
        this.rY = rY;
    }

    public float getrZ() {
        return rZ;
    }

    public void setrZ(float rZ) {
        this.rZ = rZ;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void increasePosition(float dx, float dy, float dz) {
        this.position.set(position.add(dx, dy, dz));
    }

    public void increaseRotation(float dx, float dy, float dz) {
        this.rX += dx;
        this.rY += dy;
        this.rZ += dz;
    }
}
