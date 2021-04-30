package main;

import engine.entities.GameItem;
import engine.entities.StaticGameItem;
import engine.graphics.Mesh;
import org.joml.Vector3f;

public class Box3D extends StaticGameItem {

    public float x;

    public float y;

    public float z;

    public float width;

    public float height;

    public Mesh mesh;

    private Vector3f position = new Vector3f();

    public Box3D(Mesh mesh) {
        super(mesh);
        this.mesh = mesh;
        this.position = position;
    }

    public boolean contains(float x2, float y2, float z2) {
        return x2 >= x
                && y2 >= y
                && x2 < x + width
                && y2 < y + height;
    }

    @Override
    public Mesh getMesh() {
        return mesh;
    }

    @Override
    public void update() {

    }

    @Override
    public void setRotation(float v, float v1, float v2) {

    }

    @Override
    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
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
    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    @Override
    public Vector3f getPosition() {
        return position;
    }


    @Override
    public Vector3f getRotation() {
        return null;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }
}
