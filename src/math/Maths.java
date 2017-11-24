package math;

import entities.Camera;

public class Maths {

    public static Matrix4 createTransformationMatrix(Vector3 vec, float rx, float ry, float rz, float scale) {
        Matrix4 matrix = new Matrix4();
        return matrix.translate(vec)
                .rotate((float)Math.toRadians(rx), new Vector3(1, 0, 0))
                .rotate((float)Math.toRadians(ry), new Vector3(0, 1, 0))
                .rotate((float)Math.toRadians(rz), new Vector3(0, 0, 1))
                .scale(scale);
    }

    public static Matrix4 createViewMatrix(Camera camera) {
        Matrix4 viewMatrix = new Matrix4();
        viewMatrix.rotate((float) Math.toRadians(camera.getPitch()), new Vector3(1, 0, 0))
                .rotate((float) Math.toRadians(camera.getYaw()), new Vector3(0, 1, 0))
                .rotate((float) Math.toRadians(camera.getRoll()), new Vector3(0, 0, 1));
        Vector3 cameraPos = camera.getPosition();
        Vector3 negCamPos = new Vector3(-cameraPos.x(), -cameraPos.y(), -cameraPos.z());
        viewMatrix.translate(negCamPos);
        return viewMatrix;
    }
}
