package engine.entities;

import engine.graphics.Mesh;
import maths.Vector3f;

public abstract class Animal extends DynamicGameItem {


    public Animal(Mesh mesh) {
        super(mesh);
    }

    public abstract void update();



}
