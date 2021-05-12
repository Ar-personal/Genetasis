package engine.entities;

import engine.graphics.Mesh;
import maths.Vector3f;

public abstract class Plant extends StaticGameItem {


    public Plant(Mesh mesh) {
        super(mesh);
    }

    public Plant(Mesh[] meshes) {
        super(meshes);
    }

    @Override
    public abstract void update();

    public abstract float getFoodValue();

    public abstract void setFoodValue(float foodValue);
}