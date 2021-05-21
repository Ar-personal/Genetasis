package engine.entities;

import engine.graphics.Mesh;
import org.joml.Vector3f;

public abstract class DynamicGameItem extends GameItem {

    public DynamicGameItem(Mesh mesh) {
        super(mesh);
    }

    public abstract Vector3f getPosition();

    public abstract void setPosition(float x, float y, float z);

    public float getScale() {
        return scale;
    }

    public abstract void setScale(float scale);

    public abstract Vector3f getRotation();

    public abstract void setRotation(float x, float y, float z);

    @Override
    public abstract void update();


}
