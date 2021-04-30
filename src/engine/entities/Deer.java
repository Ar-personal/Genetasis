package engine.entities;

import engine.graphics.Mesh;
import engine.objects.Terrain;
import main.Box3D;
import org.joml.Random;
import org.joml.Vector3f;

public class Deer extends Animal {

    protected Mesh mesh;
    protected Vector3f position, destination, moveVector;
    protected Vector3f rotation = new Vector3f();
    protected float awareness = 1f;
    protected int hunger = 0;
    protected int thirst = 0;
    protected float size;
    protected float speed;
    protected int health = 0;
    protected int stamina;
    protected int energy;
    protected float[] awarenessCubePositions;
    protected boolean walkingToPoint = false;
    protected long startTime, elapsedTime, elapsedSeconds;

    protected float width, height, length;

    protected Mesh boundingMesh, awarenessMesh, destinationPointMesh;
    protected BoundingBox boundingBox, awarenessBox, destinationPoint;
    protected Terrain terrain;

    public Deer(Mesh mesh, Vector3f position, float awareness, int hunger, int thirst, float speed, int health, int stamina, int energy, float size) {
        super(mesh);
        this.mesh = mesh;
        this.position = position;
        this.awareness = awareness;
        this.hunger = hunger;
        this.thirst = thirst;
        this.speed = speed;
        this.health = health;
        this.stamina = stamina;
        this.energy = energy;
        this.size = size;

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
                -0.5f,  0.5f,  0.5f,
                // V1
                -0.5f, -0.5f,  0.5f,
                // V2
                0.5f, -0.5f,  0.5f,
                // V3
                0.5f,  0.5f,  0.5f,
                // V4
                -0.5f,  0.5f, -0.5f,
                // V5
                0.5f,  0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,
        };

        height = mesh.getHeight();

        boundingMesh = new Mesh(positions, null, colours, null, indices,  false);
        boundingBox = new BoundingBox(boundingMesh, new Vector3f(position.x, position.y, position.z));
        boundingBox.setScale(size);


        awarenessMesh = new Mesh(awarenessCubePositions, null, colours, null, indices, false);
        awarenessBox = new BoundingBox(awarenessMesh, new Vector3f(position.x, position.y + (awareness * 0.25f), position.z));
        awarenessBox.setScale(awareness);


        destinationPointMesh = new Mesh(awarenessCubePositions, null, colours, null, indices, false);
        destinationPoint = new BoundingBox(destinationPointMesh, new Vector3f(0, 0, 0));
        destinationPoint.setScale(0.1f);


    }


    @Override
    public void update() {

        if(position.y > terrain.getHeight(position)){
            position.y -= 0.005f;

        }else{
            position.y = terrain.getHeight(position);
            jump();
        }

        boundingBox.setPosition(position.x, position.y, position.z);
        awarenessBox.setPosition(position.x, position.y +(awareness * 0.25f), position.z);

        elapsedTime = System.currentTimeMillis() - startTime;
        elapsedSeconds = elapsedTime / 1000;

        System.out.println("elapsed seconds : " + elapsedSeconds);
        if(!walkingToPoint) {
            if(elapsedSeconds > 5) {
                System.out.println("calculating new destination");
                walkToPoint();
            }
        }else{
            moveVector = new Vector3f();
            moveVector.x = destination.x - position.x;
            moveVector.y = 0;
            moveVector.z = destination.z - position.z;
            moveVector.normalize();

            setRotation(destination.x, destination.y, destination.z);

            if (destination.x - position.x > 0.001f || destination.z - position.z > 0.001f) {
                position.x += (moveVector.x * speed);
                position.z += (moveVector.z * speed);
                System.out.println("moving x axis");
            }

//            if (destination.z - position.z > 0.01f) {
//                position.z += (moveVector.z * speed);
//                System.out.println("moving z axis");
//            }

            if(destination.x - position.x < 0.01 && destination.z - position.z < 0.001){
                walkingToPoint = false;
                System.out.println("reached destination");
                startTime = System.currentTimeMillis();
                elapsedTime = 0;
                elapsedSeconds = 0;
            }
        }
    }


    public void walkToPoint(){
        walkingToPoint = true;

        destination = new Vector3f();
        Random random = new Random();
        float xMin = awarenessBox.getMesh().getMinX();
        float xMax = awarenessBox.getMesh().getMaxX();
        float xDest = random.nextFloat() * (xMax - xMin) + xMin;
        float yMin = awarenessBox.getMesh().getMinY();
        float yMax = awarenessBox.getMesh().getMaxY();
        float yDest = random.nextFloat() * (yMax - yMin) + yMin;
        float zMin = awarenessBox.getMesh().getMinZ();
        float zMax = awarenessBox.getMesh().getMaxZ();
        float zDest = random.nextFloat() * (zMax - zMin) + zMin;

        destination.x = xDest;
        destination.y = yDest;
        destination.z = zDest;

        destinationPoint.setPosition(destination.x, position.y, destination.z);
    }

    public void jump(){
        float velocity = 0.8f;
        for(int i = 0; i < 1000; i++){
            position.y += 0.00015f * velocity;
            Vector3f boundPrev = boundingBox.getPosition();
            boundPrev.y += 0.00015f * velocity;
            boundingBox.setPosition(boundPrev.x, boundPrev.y, boundPrev.z);

            velocity -= velocity / 1000;
        }
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    @Override
    protected void move() {

    }

    @Override
    public Vector3f getPosition() {
        return position;
    }

    @Override
    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
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
    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }


    @Override
    public Mesh getMesh() {
        return mesh;
    }

    @Override
    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }


    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getAwareness() {
        return awareness;
    }

    public void setAwareness(float awareness) {
        this.awareness = awareness;
    }

    public int getHunger() {
        return hunger;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    public int getThirst() {
        return thirst;
    }

    public void setThirst(int thirst) {
        this.thirst = thirst;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public Mesh getBoundingMesh() {
        return boundingMesh;
    }

    public void setBoundingMesh(Mesh boundingMesh) {
        this.boundingMesh = boundingMesh;
    }

    public Mesh getAwarenessMesh() {
        return awarenessMesh;
    }

    public void setAwarenessMesh(Mesh awarenessMesh) {
        this.awarenessMesh = awarenessMesh;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public BoundingBox getAwarenessBox() {
        return awarenessBox;
    }

    public void setAwarenessBox(BoundingBox awarenessBox) {
        this.awarenessBox = awarenessBox;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    public BoundingBox getDestinationPoint() {
        return destinationPoint;
    }

    public void setDestinationPoint(BoundingBox destinationPoint) {
        this.destinationPoint = destinationPoint;
    }
}
