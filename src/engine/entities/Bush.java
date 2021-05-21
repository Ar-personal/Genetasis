package engine.entities;

import engine.graphics.Mesh;
import org.joml.Vector3f;

public class Bush extends Plant {

    private Mesh mesh;
    private Vector3f position;
    private Vector3f rotation = new Vector3f();

    public Bush(Mesh mesh, Vector3f position) {
        super(mesh);
        this.mesh = mesh;
        this.position = position;
    }

    @Override
    public void update() {

    }

    @Override
    public float getFoodValue() {
        return 0;
    }


    @Override
    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return null;
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public void setSelected(boolean selected) {

    }


    @Override
    public float getScale() {
        return scale;
    }

    @Override
    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    public Vector3f getRotation() {
        return rotation;
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
    public Vector3f getPosition() {
        return position;
    }

    @Override
    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }
}
