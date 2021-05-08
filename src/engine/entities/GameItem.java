package engine.entities;

import engine.graphics.Mesh;
import org.joml.Vector3f;

public abstract class GameItem {

    protected Mesh mesh;

    protected static Vector3f position;

    protected float scale;

    protected Vector3f rotation;

    protected boolean selected;

    public GameItem(Mesh mesh) {
        this.mesh = mesh;
    }

    public abstract Mesh getMesh();

    public abstract void setMesh(Mesh mesh);

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


}
