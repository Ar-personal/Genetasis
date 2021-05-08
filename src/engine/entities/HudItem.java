package engine.entities;

import engine.graphics.Mesh;
import org.joml.Vector3f;

public class HudItem extends StaticGameItem{

    private Mesh mesh;
    private Vector3f position = new Vector3f();
    private Vector3f rotation = new Vector3f();

    public HudItem(Mesh mesh) {
        super(mesh);
        this.mesh = mesh;
    }

    @Override
    public Vector3f getPosition() {
        return position;
    }

    @Override
    public void update() {

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
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
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
    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }
}
