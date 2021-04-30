package engine.objects;

import engine.entities.StaticGameItem;
import engine.graphics.Material;
import engine.graphics.Mesh;
import engine.utils.OBJLoader;
import main.Texture;
import org.joml.Vector3f;

public class SkyBox extends StaticGameItem {

    public SkyBox(String objModel, String textureFile) throws Exception {
        super(null);
        Mesh skyBoxMesh = new OBJLoader().loadMesh(objModel, null, false);
        Texture skyBoxtexture = new Texture(textureFile);
        skyBoxMesh.setMaterial(new Material(skyBoxtexture, 0.0f));
        setMesh(skyBoxMesh);
        setPosition(0, 0, 0);
    }

    @Override
    public Mesh getMesh() {
        return mesh;
    }

    @Override
    public void setMesh(Mesh mesh) {

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
}
