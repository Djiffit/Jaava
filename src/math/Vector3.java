package math;

import org.lwjglx.BufferUtils;

import java.nio.FloatBuffer;

public class Vector3 {

    public float x;
    public float y;
    public float z;

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(float v) {
        this.x = v;
        this.y = v;
        this.z = v;
    }

    public Vector3() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }
    
//    public Vector3(Vector2 v, float f) {
//        set(vec, f);
//    }

    public float x() {
        return this.x;
    }

    public float y() {
        return this.y;
    }

    public float z() {
        return this.z;
    }

    public Vector3 setX(float x) {
        this.x = x;
        return this;
    }

    public Vector3 setY(float y) {
        this.y = y;
        return this;
    }

    public Vector3 setZ(float Z) {
        this.z = z;
        return this;
    }

    public boolean equals(Vector3 v) {
        return v.x == this.x && v.y == this.y && v.z == this.z;
    }

    @Override
    public int hashCode() {
        return (int)(x * (2 << 4) + y * (2 << 2) + z);
    }
    
    public Vector3 set(float f) {
        this.x = f;
        this.y = f;
        this.z = f;
        return this;
    }
    
    public Vector3 set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }
    
    public Vector3 set2(Vector2 vec) {
        return set(vec.x(), vec.y(), 0);
    }
    
    public Vector3 set(Vector2 vec, float f) {
        return set(vec.x(), vec.y(), f);
    }
    
    public Vector3 set(Vector3 vec) {
        return set(vec.x, vec.y, vec.z);
    }
    
    public Vector3 set4(Vector4 vec) {
        return set(vec.x(), vec.y(), vec.z());
    }
    
    public float length() {
        return (float)Math.sqrt(lengthSquare());
    }
    
    public float lengthSquare() {
        return x*x + y*y + z*z;
    }
    
    public Vector3 normalize() {
        float length = 1f / length();
        return set(x * length, y* length, z * length);
    }
    
    public float dot(Vector3 vec) {
        return x * vec.x + y * vec.y + z * vec.z;
    }
    
    public Vector3 cross(Vector3 vec, Vector3 result) {
        return result.set(y * vec.z - vec.y * z, z * vec.x - vec.z * x, x * vec.y - vec.x * y);
    }
    
    public Vector3 add(float x, float y, float z) {
        return set(this.x + x, this.y + y, this.z + z);
    }
    
    public Vector3 sub(Vector3 vec) {
        return sub(vec.x, vec.y, vec.z);
    }
    
    public Vector3 sub(float x, float y, float z) {
        return set(this.x - x, this.y - y, this.z - z);
    }
    
    public Vector3 mult(float f) {
        return mult(f, f, f);
    }
    
    public Vector3 mult(float x, float y, float z) {
        return set(this.x * x, this.y * y, this.z * z);
    }
    
    public Vector3 mult(Vector3 vec) {
        return mult(vec.x, vec.y, vec.z);
    }
    
    public Vector3 divide(float f) {
        return divide(f, f, f);
    }
    
    public Vector3 divide(float x, float y, float z) {
        return set(this.x/x, this.y/y, this.z/z);
    }
    
    public Vector3 divide(Vector3 vec) {
        return divide(vec.x, vec.y, vec.z);
    }

    public Vector3 mod(float f) {
        return set(x % f, y % f, z % f);
    }

    @Override
    public String toString() {
        return "( " + x + ", " + y + ", " + z + " )";
    }

    private final static FloatBuffer direct = BufferUtils.createFloatBuffer(3);

    public FloatBuffer toBuffer() {
        direct.clear();
        direct.put(x).put(y).put(z);
        direct.flip();
        return direct;
    }

    public Vector3 add(Vector3 vec) {
        return add(vec.x, vec.y, vec.z);
    }
}
