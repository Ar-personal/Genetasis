package engine.entities;

import engine.graphics.Mesh;
import engine.terrain.Terrain;
import org.joml.Random;
import org.joml.Vector3f;

public abstract class GameItem {

    protected Mesh mesh;
    protected Terrain terrain;

    protected Mesh[] meshes;

    protected Vector3f position, destination, rotation;

    protected float scale;

    protected boolean selected;

    public GameItem(Mesh mesh) {
        this.meshes = new Mesh[]{mesh};
    }

    public GameItem(Mesh[] meshes) {
        this.meshes = meshes;
    }

    public Mesh getMesh() {
        return meshes[0];
    }

    public Mesh[] getMeshes() {
        return meshes;
    }

    public void setMeshes(Mesh[] meshes) {
        this.meshes = meshes;
    }

    public void setMesh(Mesh mesh) {
        this.meshes = new Mesh[]{mesh};
    }

    public abstract Vector3f getPosition();

    public abstract float getScale();

    public abstract void setScale(float scale);

    public abstract Vector3f getRotation();

    public abstract void update() throws Exception;

    public abstract void setRotation(float x, float y, float z);

    public abstract void setPosition(float x, float y, float z);

    public abstract BoundingBox getBoundingBox();

    public abstract boolean isSelected();

    public abstract  void setSelected(boolean selected);

    public void cleanup() {
        int numMeshes = this.meshes != null ? this.meshes.length : 0;
        for (int i = 0; i < numMeshes; i++) {
            this.meshes[i].cleanUp();
        }
    }


    public Terrain getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }
}
