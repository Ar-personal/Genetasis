package engine.entities;

import engine.graphics.Mesh;
import org.joml.Vector3f;

public class TerrainItem extends StaticGameItem {

    private Mesh mesh;
    private Vector3f position;
    private Vector3f rotation = new Vector3f();

    public TerrainItem(Mesh mesh, Vector3f position) {
        super(mesh);
        this.mesh = mesh;
        this.position = position;
    }

    @Override
    public Vector3f getPosition() {
        return position;
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
    public void update() {

    }

    @Override
    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }


    @Override
    public Mesh getMesh() {
        return mesh;
    }

    @Override
    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
}
