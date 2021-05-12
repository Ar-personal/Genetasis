package engine.entities;

import engine.graphics.Mesh;
import engine.hud.Hud;
import engine.objects.Terrain;
import engine.utils.OBJLoader;
import main.Scene;
import org.joml.Random;
import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;
import java.util.List;

public class Grass extends Plant {

    protected Scene scene;
    protected Terrain terrain;
    protected Mesh mesh, boundingMesh, awarenessMesh, offSpringMesh = new OBJLoader().loadMesh("/models/grass.obj", new Vector3f(50, 200, 200), false);;
    protected Vector3f position;
    protected Vector3f rotation = new Vector3f();
    protected float[] awarenessCubePositions;
    protected List<Grass> intersectingGrassObjects = new ArrayList<>();
    protected int growthTime = 60;

    protected float foodValue = 15f;
    protected long startTime, elapsedTime, elapsedSeconds;

    protected BoundingBox boundingBox, awarenessBox;

    public Grass(Scene scene, Terrain terrain, Mesh mesh, Vector3f position) throws Exception {
        super(mesh);
        this.scene = scene;
        this.terrain = terrain;
        this.mesh = mesh;
        this.position = position;

        init();
    }

    public void init(){
        startTime = System.currentTimeMillis();
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


        awarenessCubePositions = new float[]{
                // VO
                -0.5f,  0.5f ,  0.5f,
                // V1
                -0.5f , -0.5f,  0.5f,
                // V2
                0.5f , -0.5f,  0.5f ,
                // V3
                0.5f ,  0.5f,  0.5f,
                // V4
                -0.5f,  0.5f, -0.5f,
                // V5
                0.5f ,  0.5f, -0.5f,
                // V6
                -0.5f , -0.5f, -0.5f,
                // V7
                0.5f , -0.5f, -0.5f,
        };


        boundingMesh = new Mesh(positions, null, colours, null, indices);
        boundingBox = new BoundingBox(boundingMesh, new Vector3f(position.x, position.y, position.z));
        boundingBox.setScale(0.1f);


        awarenessMesh = new Mesh(awarenessCubePositions, null, colours, null, indices);
        awarenessBox = new BoundingBox(awarenessMesh, new Vector3f(position.x, position.y, position.z));
        awarenessBox.setScale(1f);
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
        position = new Vector3f(x, y, z);
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
        switch (Hud.gameSpeed){
            case 1:
                growthTime = 60;
                break;
            case 2:
                growthTime = 40;
                break;
            case 3:
                growthTime = 20;
                break;

            default:
                growthTime = 60;
                break;
        }


        elapsedTime = System.currentTimeMillis() - startTime;
        elapsedSeconds = elapsedTime / 1000;

        //add new grass if enough time has passed
        if(intersectingGrassObjects.size() < 5 && elapsedSeconds > growthTime){
            Vector3f spawnPosition = calculateSpawnWithinAwarenessBox();
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
            Grass g = null;
            try {
                g = new Grass(scene, terrain, offSpringMesh, new Vector3f(spawnPosition.x, terrain.getHeight(new Vector3f(spawnPosition.x, spawnPosition.y, spawnPosition.z)), spawnPosition.x));
            } catch (Exception e) {
                e.printStackTrace();
            }
            scene.addStaticGameItem(g);
//            scene.addBoundingBox(g.getBoundingBox());
            scene.addBoundingBox(g.getAwarenessBox());
            startTime = System.currentTimeMillis();
            elapsedTime = 0;
            elapsedSeconds = 0;
        }
    }


    public Vector3f calculateSpawnWithinAwarenessBox(){
        Random random = new Random();
        float xMin = awarenessBox.getMesh().getMinX() * awarenessBox.getScale() + position.x;
        float xMax = awarenessBox.getMesh().getMaxX() * awarenessBox.getScale() + position.x;
        float xDest = (random.nextFloat() * (xMax - xMin) + xMin);
        float yMin = awarenessBox.getMesh().getMinY() * awarenessBox.getScale() + position.y;
        float yMax = awarenessBox.getMesh().getMaxY() * awarenessBox.getScale() + position.y;
        float yDest = (random.nextFloat() * (yMax - yMin) + yMin);
        float zMin = awarenessBox.getMesh().getMinZ() * awarenessBox.getScale() + position.z;
        float zMax = awarenessBox.getMesh().getMaxZ() * awarenessBox.getScale() + position.z;
        float zDest = (random.nextFloat() * (zMax - zMin) + zMin);

        Vector3f potentialLocation = new Vector3f(xDest, yDest, zDest);
        if(checkOutOfBounds(potentialLocation)){
            calculateSpawnWithinAwarenessBox();
        }

        return potentialLocation;
    }

    public boolean checkOutOfBounds(Vector3f vector3f){
        if(terrain == null){
            return false;
        }
        if(vector3f.x > terrain.getTopLeftX() + terrain.getBoundingWidth()){
            System.out.println("grass beyond MaxX");
            return true;
        }

        if(vector3f.x < terrain.getTopLeftX()){
            System.out.println("grass beyond MinX");
            return true;
        }

        if(vector3f.z > terrain.getTopLeftZ() + terrain.getBoundingLength()){
            System.out.println("grass beyond MaxZ");
            return true;
        }

        if(vector3f.z < terrain.getTopLeftZ()){
            System.out.println("grass beyond MinZ");
            return true;
        }
        return false;
    }

    public boolean boxIntersection(BoundingBox thisBox, BoundingBox otherBox){
        float f1 = thisBox.getScale();
        float f2 = otherBox.getScale();
        return (thisBox.getMesh().getMinX() * f1 + thisBox.getPosition().x <= otherBox.getMesh().getMaxX() * f2 + otherBox.getPosition().x && thisBox.getMesh().getMaxX() * f1 + thisBox.getPosition().x >= otherBox.getMesh().getMinX() * f2 + otherBox.getPosition().x) &&
                (thisBox.getMesh().getMinY() * f1 + thisBox.getPosition().y <= otherBox.getMesh().getMaxY() * f2 + otherBox.getPosition().y && thisBox.getMesh().getMaxY() * f1 + thisBox.getPosition().y >= otherBox.getMesh().getMinY() * f2 + otherBox.getPosition().y) &&
                (thisBox.getMesh().getMinZ() * f1 + thisBox.getPosition().z <= otherBox.getMesh().getMaxZ() * f2 + otherBox.getPosition().z && thisBox.getMesh().getMaxZ() * f1 + thisBox.getPosition().z >= otherBox.getMesh().getMinZ() * f2 + otherBox.getPosition().z);
    }

    public void addIntersectingGrassObject(Grass object) {
        if(intersectingGrassObjects == null){
            intersectingGrassObjects = new ArrayList<>();
        }
        intersectingGrassObjects.add(object);
    }

    public void removeIntersectingObject(GameItem object) {
        if(intersectingGrassObjects.contains(object)) {
            intersectingGrassObjects.remove(object);
        }
    }

    public List<Grass> getIntersectingGrassObjects() {
        return intersectingGrassObjects;
    }

    @Override
    public void setRotation(float x, float y, float z) {
        this.rotation = new Vector3f(x, y, z);
    }

    public Mesh getBoundingMesh() {
        return boundingMesh;
    }

    public void setBoundingMesh(Mesh boundingMesh) {
        this.boundingMesh = boundingMesh;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

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

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public float getFoodValue() {
        return foodValue;
    }

    
    public void setFoodValue(float foodValue) {
        this.foodValue = foodValue;
    }

    public BoundingBox getAwarenessBox() {
        return awarenessBox;
    }
}
