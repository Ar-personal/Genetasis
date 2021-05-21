package engine.entities;

import engine.graphics.Mesh;
import engine.hud.Hud;
import engine.terrain.Terrain;
import engine.hud.EntityHud;
import main.Scene;
import org.joml.Random;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Deer extends Animal {

    protected Mesh mesh;
    protected EntityHud hud;
    protected Vector3f rotation = new Vector3f();
    protected Plant objectTarget;

    protected int thirst;

    protected boolean runningFromPredator = false;
    protected int health;
    protected int stamina;

    protected long elapsedTime, elapsedSeconds, elapsedEscapeTime, elapsedEscapeSeconds, startEscapeTime;

    protected List<Plant> intersectingPlantObjects = new ArrayList<>();

    protected List<Animal> intersectingPredatorObjects = new ArrayList<>();

    public Deer(Scene scene, Mesh mesh, Vector3f position, Terrain terrain, float awareness, float maxHunger, int thirst, float speed, float maxSpeed,  float energy, float maxEnergy,  float size, float maxSize) {
        super(scene, mesh, position, terrain, awareness, maxHunger, speed, maxSpeed, energy, maxEnergy, size, maxSize);
        this.thirst = thirst;

        foodValue = 300f;

        init();

    }


    @Override
    public void update() {
        //check for game speedups
        checkGameSpeed();


        if(scene.getWindow().getOpts().unlockFrameRate){
            waitAmt = waitAmt / scene.getWindow().getOpts().updateAmt;
        }


        boundingBox.setPosition(position.x, position.y, position.z);
        awarenessBox.setPosition(position.x, position.y, position.z);
        scale = size;
        boundingBox.setScale(scale);




        elapsedTime = System.currentTimeMillis() - startTime;
        elapsedSeconds = elapsedTime / 1000;

        //make sure deer doesn't go below the terrain and regularly jumps
        checkHeight();

        //update hud to display specific stats of this deer entity
        if(isSelected() || boundingBox.isSelected() || awarenessBox.selected){
            System.out.println(growth);
            hud.setGameItemType(this);
            hud.setAwareness(awareness);
            hud.setHealth(health);
            hud.setHunger(hunger);
            hud.setMaxHunger(maxHunger);
            hud.setSize(size);
            hud.setStamina(stamina);
            hud.setThirst(thirst);
            hud.setSpeed(speed);
            hud.setMaxSpeed(maxSpeed);
            hud.setEnergy(energy);
            hud.setMaxEnergy(maxEnergy);
            hud.setMale(male);
            hud.setMaxSize(maxSize);
            hud.setGeneration(generation);
        }

        checkForParents();
        handlePredators();

        if(runningFromPredator){
            run();
        }

        if(destination == null && elapsedSeconds > waitAmt){
            mate();
        }

        if(destination == null && elapsedSeconds > waitAmt){
            canEat();
        }

        if(destination == null && elapsedSeconds > waitAmt){
            followMother();
        }

        if(destination == null && elapsedSeconds > waitAmt){
            findRandomDesto(awarenessBox, position);
        }



        if(objectTarget != null) {
            if (boxIntersection(boundingBox, objectTarget.getBoundingBox())) {
                hunger += objectTarget.getFoodValue();
                scene.removeItem(objectTarget);
                scene.removeItem(objectTarget.getBoundingBox());
                intersectingPlantObjects.remove(objectTarget);
                objectTarget = null;
                if(hunger > maxHunger){
                    hunger = maxHunger;
                }
            }
        }

        if(destination != null) {
            destinationPoint.setPosition(destination.x, position.y, destination.z);
        }

        //checking if the deer can eat must come after the target being set to null

        if(destination != null) {
            moveToDestination();
        }

        calculateStats();
    }


    public void moveToDestination() {
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

        //check if the deer reaches its current destination, if yes destination is set to null and be reset by the update method
        if (boxIntersection(getBoundingBox(), getDestinationPoint())) {
            if(aboutToMate){
                hunger -= maxHunger / 2;
                if(!male){
                    if(malePartner == null){

                    }
                    scene.addDeer(new Vector3f(position.x, terrain.getHeight(new Vector3f(position.x, position.y, position.z)), position.z), true,  this, (Deer) malePartner);
                }
            }
            if(runningFromPredator){
                runningFromPredator = false;
            }
            //collision detected
            startTime = System.currentTimeMillis();
            elapsedTime = 0;
            elapsedSeconds = 0;
            destination = null;
            aboutToMate = false;
        }else{
            //keep moving towards destination vector3
            position.add(moveVector.mul(speed));

        }
    }

    public void handlePredators(){

            if (intersectingPredatorObjects.size() > 0) {
                runningFromPredator = true;
                if(destination == null) {
                    destination = findRandomDesto(terrain.getBoundingBox(), position);
                }
                destinationPoint.setPosition(destination.x, destination.y, destination.z);
            }


        if(runningFromPredator){
            //if already running, run for a while then begin anew
            if(intersectingPredatorObjects.size() == 0 && elapsedTime > waitAmt * 2){
                destination = null;
                intersectingPlantObjects = new ArrayList<>();
                runningFromPredator = false;
                startTime = System.currentTimeMillis();
                elapsedTime = 0;
                elapsedSeconds = 0;
            }
        }


    }

    public void canEat(){
        //is well fed and doesn't need to eat
        if(hunger > (maxHunger / 4) * 3){
            return;
        }
        if(intersectingPlantObjects.size() > 0) {
            objectTarget = intersectingPlantObjects.get(0);
            destination = new Vector3f(objectTarget.getPosition().x, objectTarget.getPosition().y, objectTarget.getPosition().z);
            if (isSelected()) {
                System.out.println("can eat");
                System.out.println(destination.x);
                System.out.println(destination.y); }

        }
    }

    public void mate(){
        if(intersectingSameSpecies.size() > 0){
            for (Animal deer : intersectingSameSpecies){
                canMate(deer);
            }
        }
    }

    public void canMate(Deer otherDeer){
        //set direction of both deers to each other
        //intersect add new deer to scene at female location


        //maybe add a timer variable to prevent constant checking if incompatible mates
        //deers are same sex
        if(this.male == otherDeer.male){
            return;
        }

        if(otherDeer == mother || otherDeer == father){
            if(selected){
                System.out.println("cannot mate with parents");
            }
            return;
        }

        //if both deer are not atleast half grown then dont mate
        if(this.size < maxSize / 2 || otherDeer.size < otherDeer.maxSize / 2){
            return;
        }

        if(this.aboutToMate || otherDeer.aboutToMate){
            return;
        }

        //make sure both deer are well fed
        if(this.hunger < this.maxHunger / 2 || otherDeer.hunger < otherDeer.maxHunger / 2){
            return;
        }
        //they move towards each other
        this.destination = otherDeer.position;
        this.aboutToMate = true;
    }

    public void followMother(){
        if(mother == null){
            return;
        }

        if(growth >= 50f){
            return;
        }

        if(!scene.getGameItems().contains(mother)){
            return;
        }

        destination = findRandomDesto(mother.awarenessBox, mother.position);
    }

    public void addIntersectingPlantObject(Plant object) {
        if(intersectingPlantObjects == null){
            intersectingPlantObjects = new ArrayList<>();
        }
        intersectingPlantObjects.add(object);
    }

    public void removeIntersectingObject(GameItem object) {
        if(intersectingPlantObjects.contains(object)) {
            intersectingPlantObjects.remove(object);
        }
    }


    public List<Plant> getIntersectingObjects() {
        return intersectingPlantObjects;
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


    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
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

    public BoundingBox getAwarenessBox() {
        return awarenessBox;
    }

    public void setAwarenessBox(BoundingBox awarenessBox) {
        this.awarenessBox = awarenessBox;
    }

    public int getGeneration() {
        return generation;
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

    public EntityHud getHud() {
        return hud;
    }

    public void setHud(EntityHud hud) {
        this.hud = hud;
    }



    public void addIntersectingPredatorObject(Animal object) {
        if(!intersectingPredatorObjects.contains(object)) {
            intersectingPredatorObjects.add(object);
        }
    }

    public void removeIntersectingPredatorObject(Animal object) {
        if(intersectingPredatorObjects.contains(object)) {
            intersectingPredatorObjects.remove(object);
        }
    }

    public List<Animal> getIntersectingPredatorObjects() {
        return intersectingPredatorObjects;
    }

    public void setIntersectingPredatorObjects(List<Animal> intersectingPredatorObjects) {
        this.intersectingPredatorObjects = intersectingPredatorObjects;
    }



    @Override
    public void setPosition(Vector3f position) {
        this.position = position;
    }

}
