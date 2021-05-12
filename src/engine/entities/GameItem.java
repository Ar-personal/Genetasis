package engine.entities;

import engine.graphics.Mesh;
import org.joml.Vector3f;

public abstract class GameItem {

    protected Mesh mesh;

    protected Mesh[] meshes;

    protected static Vector3f position;

    protected float scale;

    protected Vector3f rotation;

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

    public abstract void update();

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


}
