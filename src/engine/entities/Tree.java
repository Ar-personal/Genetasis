package engine.entities;

import engine.graphics.Mesh;
import org.joml.Vector3f;

public class Tree extends Plant {

    private Mesh mesh, boundingMesh;
    private Mesh[] meshes;
    private BoundingBox boundingBox;
    private Vector3f position;
    private Vector3f rotation = new Vector3f();

    public Tree(Mesh mesh, Vector3f position) {
        super(mesh);
        this.mesh = mesh;
        this.position = position;

        init();
    }

    public Tree(Mesh[] meshes, Vector3f position) {
        super(meshes);
        this.meshes = meshes;
        this.position = position;

        initMeshes();
    }

    public void init(){
        float[] positions = new float[]{

                mesh.getMinX(),  mesh.getMaxY(),  mesh.getMaxZ(),
                // V1
                mesh.getMinX(), mesh.getMinY(),  mesh.getMaxZ(),
                // V2
                mesh.getMaxX(), mesh.getMinY(),  mesh.getMaxZ(),
                // V3
                mesh.getMaxX(),  mesh.getMaxY(),  mesh.getMaxZ(),
                // V4
                mesh.getMinX(),  mesh.getMaxY(), mesh.getMinZ(),
                // V5
                mesh.getMaxX(),  mesh.getMaxY(), mesh.getMinZ(),
                // V6
                mesh.getMinX(), mesh.getMinY(), mesh.getMinZ(),
                // V7
                mesh.getMaxX(), mesh.getMinY(), mesh.getMinZ(),
        };

        float[] colours = new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
        };


        int[] indices = new int[] {
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                4, 0, 3, 5, 4, 3,
                // Right face
                3, 2, 7, 5, 3, 7,
                // Left face
                6, 1, 0, 6, 0, 4,
                // Bottom face
                2, 1, 6, 2, 6, 7,
                // Back face
                7, 6, 4, 7, 4, 5,
        };


        boundingMesh = new Mesh(positions, null, colours, null, indices);
        boundingBox = new BoundingBox(boundingMesh, new Vector3f(position.x, position.y, position.z));
        boundingBox.setScale(0.1f);
    }


    public void initMeshes(){
        float[] positions = new float[]{

                meshes[0].getMinX(),  meshes[0].getMaxY(),  meshes[0].getMaxZ(),
                // V1
                meshes[0].getMinX(), meshes[0].getMinY(),  meshes[0].getMaxZ(),
                // V2
                meshes[0].getMaxX(), meshes[0].getMinY(),  meshes[0].getMaxZ(),
                // V3
                meshes[0].getMaxX(),  meshes[0].getMaxY(),  meshes[0].getMaxZ(),
                // V4
                meshes[0].getMinX(),  meshes[0].getMaxY(), meshes[0].getMinZ(),
                // V5
                meshes[0].getMaxX(),  meshes[0].getMaxY(), meshes[0].getMinZ(),
                // V6
                meshes[0].getMinX(), meshes[0].getMinY(), meshes[0].getMinZ(),
                // V7
                meshes[0].getMaxX(), meshes[0].getMinY(), meshes[0].getMinZ(),
        };

        float[] colours = new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
        };


        int[] indices = new int[] {
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                4, 0, 3, 5, 4, 3,
                // Right face
                3, 2, 7, 5, 3, 7,
                // Left face
                6, 1, 0, 6, 0, 4,
                // Bottom face
                2, 1, 6, 2, 6, 7,
                // Back face
                7, 6, 4, 7, 4, 5,
        };


        boundingMesh = new Mesh(positions, null, colours, null, indices);
        boundingBox = new BoundingBox(boundingMesh, new Vector3f(position.x, position.y, position.z));
        boundingBox.setScale(0.1f);
    }

    @Override
    public Mesh getMesh() {
        return mesh;
    }

    @Override
    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    @Override
    public Vector3f getPosition() {
        return position;
    }

    @Override
    public void setPosition(float x, float y, float z) {
        this.position = new Vector3f(x, y, z);
    }

    @Override
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
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
        this.rotation = new Vector3f(x, y, z);
    }

    @Override
    public float getFoodValue() {
        return 0;
    }

}
