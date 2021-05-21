package engine.entities;

import engine.graphics.Mesh;

public abstract class Plant extends StaticGameItem {


    public Plant(Mesh mesh) {
        super(mesh);
    }

    public Plant(Mesh[] meshes) {
        super(meshes);
    }

    @Override
    public abstract void update() throws Exception;

    public abstract float getFoodValue();

}