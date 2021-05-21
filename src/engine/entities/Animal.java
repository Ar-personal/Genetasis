package engine.entities;

import engine.graphics.Mesh;
import engine.hud.Hud;
import engine.terrain.Terrain;
import main.Scene;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Animal extends DynamicGameItem {

    protected Scene scene;
    protected Terrain terrain;
    protected Vector3f position, moveVector = new Vector3f(), destination;
    protected Mesh mesh, boundingMesh, awarenessMesh, destinationPointMesh;
    protected BoundingBox boundingBox, awarenessBox, destinationPoint;
    protected float[] awarenessCubePositions;
    protected boolean male, aboutToMate = false;
    protected float size, maxSize;
    protected float hunger;
    protected float maxHunger, maxSpeed;
    protected float foodValue;
    protected float speed, defaultSpeed;
    protected int waitAmt = 0;
    protected float growth;
    protected float defaultGrowthAmt = 0.0000001f, defaultHungerDecay = 0.008f, defaultEnergyRegain = 0.01f, defaultEnergyDecay = 0.3f;
    protected float growthAmt = 0.0000001f, hungerDecay = 0.008f, energyRegain = 0.01f, energyDecay = 0.3f;
    protected long startTime;
    protected float awareness;
    protected float energy, maxEnergy;
    protected int generation = 1;
    protected Animal mother, father, malePartner;
    protected List<Animal> intersectingSameSpecies = new ArrayList<>();

    public Animal(Scene scene, Mesh mesh, Vector3f position, Terrain terrain, float awareness, float maxHunger, float speed, float maxSpeed, float energy, float maxEnergy, float size, float maxSize) {
        super(mesh);
        this.scene = scene;
        this.mesh = mesh;
        this.position = position;
        this.terrain = terrain;
        this.awareness = awareness;
        this.maxHunger = maxHunger;
        this.speed = speed;
        this.size = size;
        this.maxSize = maxSize;
        this.maxSpeed = maxSpeed;
        this.energy = energy;
        this.maxEnergy = maxEnergy;

    }

    public void init() {
        Random random = new Random();
        male = random.nextBoolean();
        hunger = maxHunger / 2;
        defaultSpeed = speed;

        startTime = System.currentTimeMillis();

        float[] positions = new float[]{

                mesh.getMinX(), mesh.getMaxY(), mesh.getMaxZ(),
                // V1
                mesh.getMinX(), mesh.getMinY(), mesh.getMaxZ(),
                // V2
                mesh.getMaxX(), mesh.getMinY(), mesh.getMaxZ(),
                // V3
                mesh.getMaxX(), mesh.getMaxY(), mesh.getMaxZ(),
                // V4
                mesh.getMinX(), mesh.getMaxY(), mesh.getMinZ(),
                // V5
                mesh.getMaxX(), mesh.getMaxY(), mesh.getMinZ(),
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

        int[] indices = new int[]{
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
                -0.5f, 0.5f, 0.5f,
                // V1
                -0.5f, -0.5f, 0.5f,
                // V2
                0.5f, -0.5f, 0.5f,
                // V3
                0.5f, 0.5f, 0.5f,
                // V4
                -0.5f, 0.5f, -0.5f,
                // V5
                0.5f, 0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,
        };


        boundingMesh = new Mesh(positions, null, colours, null, indices);
        boundingBox = new BoundingBox(boundingMesh, new Vector3f(position.x, position.y, position.z));
        boundingBox.setScale(size);


        awarenessMesh = new Mesh(awarenessCubePositions, null, colours, null, indices);
        awarenessBox = new BoundingBox(awarenessMesh, new Vector3f(position.x, position.y, position.z));
        awarenessBox.setScale(awareness);


        destinationPointMesh = new Mesh(awarenessCubePositions, null, colours, null, indices);
        destinationPoint = new BoundingBox(destinationPointMesh, new Vector3f(position.x, position.y, position.z));
        destinationPoint.setScale(0.02f);
    }


    public abstract void update();

    public void checkGameSpeed(){
        switch (Hud.gameSpeed){
            case 1:
                speed = defaultSpeed;
                waitAmt = 3;
                growthAmt = defaultGrowthAmt;
                hungerDecay = defaultHungerDecay;
                energyDecay = defaultEnergyDecay;
                energyRegain = defaultEnergyRegain;
                break;
            case 2:
                speed = defaultSpeed * 2;
                waitAmt = 2;
                growthAmt = defaultGrowthAmt * 2;
                hungerDecay = defaultHungerDecay * 2;
                energyDecay = defaultEnergyDecay * 2;
                energyRegain = defaultEnergyRegain * 2;
                break;
            case 3:
                speed = defaultSpeed * 3;
                waitAmt = 1;
                growthAmt = defaultGrowthAmt * 3;
                hungerDecay = defaultHungerDecay * 3;
                energyDecay = defaultEnergyDecay * 3;
                energyRegain = defaultEnergyRegain * 3;
                break;

            default:
                growthAmt = defaultGrowthAmt;
                waitAmt = 3;
                speed = defaultSpeed;
                hungerDecay = defaultHungerDecay;
                energyDecay = defaultEnergyDecay;
                energyRegain = defaultEnergyRegain;
                break;
        }
    }

    public void checkHeight(){
        if(position.y > terrain.getHeight(position) && position.y - 0.005 > terrain.getHeight(position)){
            position.y -= 0.005f;
        }else{
            position.y = terrain.getHeight(position);
            jump();
        }
    }

    public void run(){
        if(energy > 0){
            speed = maxSpeed;
            energy -= energyDecay;
        }else{
            speed = defaultSpeed;
        }
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

    public Vector3f findRandomDesto(BoundingBox region, Vector3f vector3f){
        org.joml.Random random = new org.joml.Random();
        float xMin = region.getMesh().getMinX() * region.getScale() + vector3f.x;
        float xMax = region.getMesh().getMaxX() * region.getScale() + vector3f.x;
        float xDest = (random.nextFloat() * (xMax - xMin) + xMin);
        float yMin = region.getMesh().getMinY() * region.getScale() + vector3f.y;
        float yMax = region.getMesh().getMaxY() * region.getScale() + vector3f.y;
        float yDest = (random.nextFloat() * (yMax - yMin) + yMin);
        float zMin = region.getMesh().getMinZ() * region.getScale() + vector3f.z;
        float zMax = region.getMesh().getMaxZ() * region.getScale() + vector3f.z;
        float zDest = (random.nextFloat() * (zMax - zMin) + zMin);

        destination = new Vector3f(xDest, yDest, zDest);

        //if destination point out of bounds repeat random destination until within bounds
        if(checkOutOfBounds()){
            findRandomDesto(region, vector3f);
        }

        return destination;

    }

    public void calculateStats(){
        if(hunger <= 0f){
            die();
        }
        //aging
        if(size < maxSize){
            size += growthAmt;
            if(size > maxSize) {
                size = maxSize;
            }
        }

        growth = size / maxSize * 100;

        hunger -= hungerDecay;


        if(energy < maxEnergy){
            energy += energyRegain;
        }

        if(energy <= 0){
            energy = 0.1f;
        }

        if(energy > maxEnergy){
            energy = maxEnergy;
        }

    }

    public boolean checkOutOfBounds(){
        if(terrain == null){
            return false;
        }
        if(destination.x > terrain.getTopLeftX() + terrain.getBoundingWidth()){
            System.out.println("beyond MaxX");
            return true;
        }

        if(destination.x < terrain.getTopLeftX()){
            System.out.println("beyond MinX");
            return true;
        }

        if(destination.z > terrain.getTopLeftZ() + terrain.getBoundingLength()){
            System.out.println("beyond MaxZ");
            return true;
        }

        if(destination.z < terrain.getTopLeftZ()){
            System.out.println("beyond MinZ");
            return true;
        }
        return false;
    }


    public void canMate(Animal otherAnimal){

        if(this.male == otherAnimal.isMale()){
            return;
        }

        if(otherAnimal == father || otherAnimal == mother){
            return;
        }

        //if both animal are not atleast half grown then dont mate
        if(this.size < maxSize / 2 || otherAnimal.size < otherAnimal.maxSize / 2){
            return;
        }

        if(this.aboutToMate || otherAnimal.aboutToMate){
            return;
        }

        //make sure both deer are well fed
        if(this.hunger < this.maxHunger / 2 || otherAnimal.hunger < otherAnimal.maxHunger / 2){
            return;
        }
        //they move towards each other
        this.destination = otherAnimal.position;
        this.aboutToMate = true;
        if(otherAnimal.isMale()){
            malePartner = otherAnimal;

        }else{
            malePartner = this;
        }
    }

    public void checkForParents(){

        if(!scene.getGameItems().contains(mother)){
            mother = null;
        }

        if(!scene.getGameItems().contains(father)){
            father = null;
        }
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

    public void die(){

        scene.removeItem(this);
        scene.removeItem(this.destinationPoint);
        scene.removeItem(this.awarenessBox);
        scene.removeItem(this.boundingBox);
        selected = false;

    }

    @Override
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

    public BoundingBox getDestinationPoint() {
        return destinationPoint;
    }

    public void setDestinationPoint(BoundingBox destinationPoint) {
        this.destinationPoint = destinationPoint;
    }

    public boolean boxIntersection(BoundingBox thisBox, BoundingBox otherBox){
        float f1 = thisBox.getScale();
        float f2 = otherBox.getScale();
        return (thisBox.getMesh().getMinX() * f1 + thisBox.getPosition().x <= otherBox.getMesh().getMaxX() * f2 + otherBox.getPosition().x && thisBox.getMesh().getMaxX() * f1 + thisBox.getPosition().x >= otherBox.getMesh().getMinX() * f2 + otherBox.getPosition().x) &&
                (thisBox.getMesh().getMinY() * f1 + thisBox.getPosition().y <= otherBox.getMesh().getMaxY() * f2 + otherBox.getPosition().y && thisBox.getMesh().getMaxY() * f1 + thisBox.getPosition().y >= otherBox.getMesh().getMinY() * f2 + otherBox.getPosition().y) &&
                (thisBox.getMesh().getMinZ() * f1 + thisBox.getPosition().z <= otherBox.getMesh().getMaxZ() * f2 + otherBox.getPosition().z && thisBox.getMesh().getMaxZ() * f1 + thisBox.getPosition().z >= otherBox.getMesh().getMinZ() * f2 + otherBox.getPosition().z);
    }

    public boolean isMale() {
        return male;
    }

    public void setMale(boolean male) {
        this.male = male;
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

    public void addGeneration(int i){
        generation += i;
    }

    public void setMaxSize(float maxSize) {
        this.maxSize = maxSize;
    }

    public boolean isAboutToMate() {
        return aboutToMate;
    }

    public void setAboutToBMate(boolean aboutToMate) {
        this.aboutToMate = aboutToMate;
    }

    public float getHunger() {
        return hunger;
    }

    public void setHunger(float hunger) {
        this.hunger = hunger;
    }

    public float getMaxHunger() {
        return maxHunger;
    }

    public void setMaxHunger(float maxHunger) {
        this.maxHunger = maxHunger;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getDefaultSpeed() {
        return defaultSpeed;
    }

    public void setDefaultSpeed(float defaultSpeed) {
        this.defaultSpeed = defaultSpeed;
    }

    public int getWaitAmt() {
        return waitAmt;
    }

    public void setWaitAmt(int waitAmt) {
        this.waitAmt = waitAmt;
    }

    public float getGrowthAmt() {
        return growthAmt;
    }

    public void setGrowthAmt(float growthAmt) {
        this.growthAmt = growthAmt;
    }

    public float getHungerDecay() {
        return hungerDecay;
    }

    public void setHungerDecay(float hungerDecay) {
        this.hungerDecay = hungerDecay;
    }

    @Override
    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    @Override
    public Terrain getTerrain() {
        return terrain;
    }

    @Override
    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    public float getGrowth() {
        return growth;
    }

    public void setGrowth(float growth) {
        this.growth = growth;
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

    public Mesh getDestinationPointMesh() {
        return destinationPointMesh;
    }

    public void setDestinationPointMesh(Mesh destinationPointMesh) {
        this.destinationPointMesh = destinationPointMesh;
    }

    public float[] getAwarenessCubePositions() {
        return awarenessCubePositions;
    }

    public void setAwarenessCubePositions(float[] awarenessCubePositions) {
        this.awarenessCubePositions = awarenessCubePositions;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public float getAwareness() {
        return awareness;
    }

    public void setAwareness(float awareness) {
        this.awareness = awareness;
    }

    public float getFoodValue() {
        return foodValue;
    }

    public void setFoodValue(float foodValue) {
        this.foodValue = foodValue;
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }


    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    public float getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy(float maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    public Animal getMother() {
        return mother;
    }

    public void setMother(Animal mother) {
        this.mother = mother;
    }

    public Animal getFather() {
        return father;
    }

    public void setFather(Animal father) {
        this.father = father;
    }

    public Animal getMalePartner() {
        return malePartner;
    }

    public void setMalePartner(Animal malePartner) {
        this.malePartner = malePartner;
    }

    public void addIntersectingSameSpecies(Animal object) {
        if(intersectingSameSpecies == null){
            intersectingSameSpecies = new ArrayList<>();
        }
        intersectingSameSpecies.add(object);
    }

    public List<Animal> getIntersectingSameSpecies() {
        return intersectingSameSpecies;
    }

    public void removeIntersectingSameSpecies(Animal object) {
        if(intersectingSameSpecies.contains(object)) {
            intersectingSameSpecies.remove(object);
        }
    }
}
