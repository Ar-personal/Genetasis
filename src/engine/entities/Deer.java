package engine.entities;

import engine.graphics.Mesh;
import engine.objects.Terrain;
import main.Box3D;
import main.Scene;
import org.joml.Random;
import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;
import org.lwjglx.util.glu.GLU;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class Deer extends Animal {

    protected Scene scene;
    protected Mesh mesh;
    protected Vector3f position, destination, moveVector = new Vector3f();;
    protected Vector3f rotation = new Vector3f();
    protected Plant objectTarget;
    protected float awareness = 1f;
    protected float hunger = 20f;
    protected float maxHunger;
    protected int thirst = 0;
    protected float size;
    protected float speed;
    protected float awarenessScale;
    protected int health = 0;
    protected int stamina;
    protected int energy;
    protected float[] awarenessCubePositions;
    protected boolean wandering = false, goingToFood = false, idle = false;
    protected long startTime, elapsedTime, elapsedSeconds;


    protected float width, height, length;
    protected float angle = 0;


    protected Mesh boundingMesh, awarenessMesh, destinationPointMesh;
    protected BoundingBox boundingBox, awarenessBox, destinationPoint;
    protected Terrain terrain;
    protected List<Plant> intersectingPlantObjects = null;

    public Deer(Scene scene, Mesh mesh, Vector3f position, float awareness, float maxHunger, int thirst, float speed, int health, int stamina, int energy, float size) {
        super(mesh);
        this.scene = scene;
        this.mesh = mesh;
        this.position = position;
        this.awareness = awareness;
        this.maxHunger = maxHunger;
        this.thirst = thirst;
        this.speed = speed;
        this.health = health;
        this.stamina = stamina;
        this.energy = energy;
        this.size = size;

        init();
        elapsedTime = System.currentTimeMillis() - startTime;
        elapsedSeconds = elapsedTime / 1000;
        walkToPoint();
    }

    public void init(){
        startTime = System.currentTimeMillis();
        setRotation(0, 0, 0);

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

        height = mesh.getHeight();

        boundingMesh = new Mesh(positions, null, colours, null, indices,  false);
        boundingBox = new BoundingBox(boundingMesh, new Vector3f(position.x, position.y, position.z));
        boundingBox.setScale(size);


        awarenessMesh = new Mesh(awarenessCubePositions, null, colours, null, indices, false);
        awarenessBox = new BoundingBox(awarenessMesh, new Vector3f(position.x, position.y, position.z));

        //todo figure out how to scale the mesh to the same size as true box size
        awarenessBox.setScale(awareness);


        destinationPointMesh = new Mesh(awarenessCubePositions, null, colours, null, indices, false);
        destinationPoint = new BoundingBox(destinationPointMesh, new Vector3f(position.x, position.y, position.z));
        destinationPoint.setScale(0.1f);


    }


    @Override
    public void update() {
        //deer gets hungry

        calculateStats();

        if(position.y > terrain.getHeight(position) && position.y - 0.005 > terrain.getHeight(position)){
            position.y -= 0.005f;
        }else{
            position.y = terrain.getHeight(position);
            jump();
        }

        boundingBox.setPosition(position.x, position.y, position.z);
        awarenessBox.setPosition(position.x, position.y, position.z);

        elapsedTime = System.currentTimeMillis() - startTime;
        elapsedSeconds = elapsedTime / 1000;

//        System.out.println("elapsed seconds : " + elapsedSeconds);
        if(canEat() && idle){
            System.out.println("walking to food");
            destination.x = objectTarget.getPosition().x;
            destination.y = objectTarget.getPosition().y;
            destination.z = objectTarget.getPosition().z;
            idle = false;
        }

        Random random = new Random();
        int r = random.nextInt(6);
        if (!canEat() && idle && elapsedSeconds > r) {
            System.out.println("calculating new destination");
            walkToPoint();
        }

        destinationPoint.setPosition(destination.x, position.y, destination.z);
        if(destination != null && elapsedSeconds > r) {
            moveToDestination();
        }


        //check for intersections with food only when the deer isn't idle, may need to specify idle to if deer is eating or doing another activity
        if(objectTarget != null && !idle) {
            if (boxIntersection(boundingBox, objectTarget.getBoundingBox())) {
                System.out.println("collide");
                scene.removeItem(objectTarget);
                scene.removeItem(objectTarget.getBoundingBox());
                intersectingPlantObjects.remove(objectTarget);
                hunger += objectTarget.getFoodValue();
                if(hunger > maxHunger){
                    hunger = maxHunger;
                }
            }
        }

    }

    public void calculateStats(){
        hunger -= 0.001f;
        if(hunger <= 0){
            scene.removeItem(this);
            scene.removeItem(this.boundingBox);
            scene.removeItem(this.awarenessBox);
        }
    }

    public void moveToDestination(){
        //get unit vector of the destination
        moveVector.x = destination.x - position.x;
        moveVector.y = 0;
        moveVector.z = destination.z - position.z;
        moveVector.normalize();

//            rotation = new Vector3f(0,  90f, 0);
//            Vector3f p = new Vector3f(position);
//            Vector3f dd = new Vector3f(destination);
//            Vector3f d = dd.sub(p);
//            Vector3f directionA = p.normalize();
//            Vector3f directionB = d.normalize();
//
//            float angle = (float) Math.acos(directionA.dot(directionB));
//            float angleDeg = (float) Math.toDegrees(angle);
//            float ang = rotation.y - angleDeg;
////
////            while(ang < 0){
////                ang += 360f;
////            }
////
//            System.out.println(angle + " " + angleDeg + " " + ang);
//            float a = rotation.y - ang;
//            float theta = 0;
//            if(a < 180f){
//                theta -= 1f;
//            }else if(a > 180f){
//                theta += 1f;
//                rotation = new Vector3f(0,  rotation.y + theta, 0);
//            }
//            boundingBox.setRotation(0, rotation, 0);
//            awarenessBox.setRotation(0, ang, 0);

        //check if the deer is within a certain threshold in range of the target, speed may be a good step
        //check for collission
        if (destination.x - position.x > speed || destination.z - position.z > speed) {
            position.x += (moveVector.x * speed);
            position.z += (moveVector.z * speed);
        }

//            if (destination.z - position.z > 0.01f) {
//                position.z += (moveVector.z * speed);
//                System.out.println("moving z axis");
//            }

        //if deer box intersects food box
        //add hunger value
        //


        if(destination.x - position.x < speed && destination.z - position.z < speed){
            //collision detected
            idle = true;
            System.out.println("reached destination " + canEat() + " " + idle + " hunger: " + hunger);
            startTime = System.currentTimeMillis();
            elapsedTime = 0;
            elapsedSeconds = 0;
        }
    }


    public void walkToPoint(){
        idle = false;
        destination = new Vector3f();
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

    public boolean canEat(){
        if(hunger <= maxHunger / 2f) {
            if (intersectingPlantObjects != null) {
                for (Plant g : intersectingPlantObjects) {
                    if (g instanceof Grass) {
                        objectTarget = g;
                        goingToFood = true;
                        return true;
                    }
                }
            }
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


    public void addIntersectingPlantObject(Plant object) {
        if(intersectingPlantObjects == null){
            intersectingPlantObjects = new ArrayList<>();
        }
        intersectingPlantObjects.add(object);
    }

    public void removeIntersectingObject(GameItem object) {
        if(intersectingPlantObjects == null){
            intersectingPlantObjects = new ArrayList<Plant>();
        }
        intersectingPlantObjects.remove(object);
    }


    public List<Plant> getIntersectingObjects() {
        return intersectingPlantObjects;
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

    public float getHunger() {
        return hunger;
    }

    public void setHunger(float hunger) {
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
