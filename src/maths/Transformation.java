package maths;

import engine.objects.Camera;
import engine.objects.GameObject;

import static maths.Matrix4f.scale;

public class Transformation {

    private final Matrix4f projectionMatrix;

    private final Matrix4f modelViewMatrix;

    private final Matrix4f viewMatrix;

    public Transformation() {
        projectionMatrix = new Matrix4f();
        modelViewMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
    }

    public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        return projectionMatrix.projection(fov, width / height, zNear, zFar);
    }

    public Matrix4f getViewMatrix(Camera camera) {
        Vector3f cameraPos = camera.getPosition();
        Vector3f rotation = camera.getRotation();

        viewMatrix.identity();
        // First do the rotation so camera rotates over its position
        viewMatrix.rotate((float)Math.toRadians(rotation.getX()), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rotation.getY()), new Vector3f(0, 1, 0));
        // Then do the translation
        viewMatrix.translate(cameraPos);
        return viewMatrix;
    }

    public Matrix4f getModelViewMatrix(GameObject gameObject, Matrix4f viewMatrix) {
        Vector3f rotation = gameObject.getRotation();
        modelViewMatrix.view(rotation, gameObject.getPosition());
        Matrix4f viewCurr = new Matrix4f();
        return viewCurr.multiply(modelViewMatrix, viewMatrix);
    }
}
