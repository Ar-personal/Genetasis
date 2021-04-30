package engine.entities;

import engine.graphics.Mesh;
import org.joml.Vector3f;

public class BoundingBox extends DynamicGameItem {

    private Mesh mesh;
    private Vector3f position;
    private Vector3f rotation = new Vector3f();

    public BoundingBox(Mesh mesh, Vector3f position) {
        super(mesh);
        this.mesh = mesh;
        this.position = position;
    }

    @Override
    public Vector3f getPosition() {
        return position;
    }

    @Override
    public void update() {

    }

    @Override
    public Mesh getMesh() {
        return mesh;
    }

    @Override
    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }


    @Override
    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    @Override
    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    @Override
    public Vector3f getRotation() {
        return rotation;
    }
}
