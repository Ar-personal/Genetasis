package engine.entities;

import engine.graphics.Mesh;
import engine.hud.EntityHud;
import engine.terrain.Terrain;
import main.Scene;
import org.joml.Vector3f;


import java.util.ArrayList;
import java.util.List;

public class Tiger extends Animal {

    protected Mesh mesh;
    protected EntityHud hud;
    protected Vector3f rotation = new Vector3f();
    protected Animal objectTarget;

    protected int thirst;


    protected int health;
    protected int stamina;
    protected boolean runToPrey = false;

    protected long elapsedTime, elapsedSeconds;

    protected List<Animal> intersectingPreyObjects = new ArrayList();

    public Tiger(Scene scene, Mesh mesh, Vector3f position, Terrain terrain, float awareness, float maxHunger, int thirst, float speed, float maxSpeed,  float energy, float maxEnergy,  float size, float maxSize) {
        super(scene, mesh, position, terrain, awareness, maxHunger, speed, maxSpeed, energy, maxEnergy, size, maxSize);
        this.thirst = thirst;

        init();
    }


    @Override
    public void update() {
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

        checkHeight();

        if(isSelected() || boundingBox.isSelected() || awarenessBox.selected){
            System.out.println(runToPrey);
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

        if(destination == null && elapsedSeconds > waitAmt){
            mate();
        }

        //hunt deer
        if(intersectingPreyObjects.size() > 0 && elapsedSeconds > waitAmt * 3) {
            canEat();
        }else{
            runToPrey = false;
        }

        if(elapsedSeconds > waitAmt) {
            hunt();
        }

        if(destination == null && elapsedSeconds > waitAmt){
            followMother();
        }

        if(destination == null && elapsedSeconds > waitAmt){
            findRandomDesto(awarenessBox, position);
        }



        //handle logic for when tiger catches prey
        if(objectTarget != null) {
            if (boxIntersection(boundingBox, objectTarget.getBoundingBox())) {
                hunger += objectTarget.getFoodValue();
                objectTarget.die();
                intersectingPreyObjects.remove(objectTarget);
                objectTarget = null;
                runToPrey = false;
                if(hunger > maxHunger){
                    hunger = maxHunger;
                }
            }
        }

        if(destination != null) {
            destinationPoint.setPosition(destination.x, position.y, destination.z);
        }


        if(destination != null) {
            moveToDestination();
        }


        calculateStats();
    }

    public void hunt(){
        if(runToPrey == true){
            run();
        }

        if(energy < 1){
            runToPrey = false;
            destination = null;
            startTime = System.currentTimeMillis();
            elapsedTime = 0;
            elapsedSeconds = 0;
            intersectingPreyObjects = new ArrayList<>();
        }
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
                    scene.addTiger(new Vector3f(position.x, terrain.getHeight(new Vector3f(position.x, position.y, position.z)), position.z), true, this, (Tiger) malePartner);
                }
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

    public void canEat(){
        if(hunger > maxHunger / 2){
            return;
        }

        if(growth < 60f){
            return;
        }

        objectTarget = intersectingPreyObjects.get(0);
        destination = new Vector3f(objectTarget.getPosition().x, objectTarget.getPosition().y, objectTarget.getPosition().z);
        runToPrey = true;
    }

    public void mate(){
        if(intersectingSameSpecies.size() > 0){
            for (Animal animal : intersectingSameSpecies){
                canMate(animal);
            }
        }
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


    public int getGeneration() {
        return generation;
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
    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    public Vector3f getRotation() {
        return rotation;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public float getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(float maxSize) {
        this.maxSize = maxSize;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }


    public void setHud(EntityHud hud) {
        this.hud = hud;
    }



    public BoundingBox getAwarenessBox() {
        return awarenessBox;
    }

    public void setAwarenessBox(BoundingBox awarenessBox) {
        this.awarenessBox = awarenessBox;
    }

    public BoundingBox getDestinationPoint() {
        return destinationPoint;
    }

    public void setDestinationPoint(BoundingBox destinationPoint) {
        this.destinationPoint = destinationPoint;
    }

    public List<Animal> getIntersectingPreyObjects() {
        return intersectingPreyObjects;
    }

    public void addIntersectingPreyObject(Animal animal) {
        if(!intersectingPreyObjects.contains(animal)) {
            intersectingPreyObjects.add(animal);
        }
    }

    public void removeIntersectingPreyObject(Animal animal) {
        if(intersectingPreyObjects.contains(animal)) {
            intersectingPreyObjects.remove(animal);
        }
    }

    public void setIntersectingPreyObjects(List<Animal> intersectingPreyObjects) {
        this.intersectingPreyObjects = intersectingPreyObjects;
    }

}
