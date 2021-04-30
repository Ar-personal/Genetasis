package engine.entities;

import engine.graphics.Mesh;
import org.joml.Vector3f;

public abstract class StaticGameItem extends GameItem {

    public StaticGameItem(Mesh mesh) {
        super(mesh);
    }

    public abstract Vector3f getPosition();

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public abstract float getScale();

    public abstract void setScale(float scale);

    public abstract Vector3f getRotation();

    @Override
    public abstract void update();

    public abstract void setRotation(float x, float y, float z);
}
