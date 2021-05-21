package main;

import engine.entities.*;
import engine.graphics.Fog;
import engine.graphics.Mesh;
import engine.hud.EntityHud;
import engine.hud.Hud;
import engine.lights.SceneLight;
import engine.graphics.Window;

import engine.terrain.Terrain;
import engine.utils.OBJLoader;
import org.joml.Vector3f;

import java.util.*;

public class Scene {

    private Map<Mesh, List<GameItem>> meshMap;
    private Map<Mesh, List<GameItem>> tempMap;
    private List<GameItem> gameItems = new ArrayList<>();
    private List<GameItem> gameItemsIteration = new ArrayList<>();
    private List<GameItem> itemsToBeAdded = new ArrayList<>();
    private List<GameItem> removedItems = new ArrayList<>();
    private List<Deer> deers = new ArrayList<>();
    private List<Tiger> tigerList = new ArrayList<>();
    private List<Grass> grassList = new ArrayList<>();

    private Mesh deerMesh = new OBJLoader().loadMesh("/models/Deer.obj", new Vector3f(60, 15, 30), false);
    private Mesh deerChildMesh = new OBJLoader().loadMesh("/models/Deer.obj", new Vector3f(60, 255, 30), false);

    private Mesh grassMesh = new OBJLoader().loadMesh("/models/grass.obj", new Vector3f(50, 255, 50), false);

    private Mesh tigerMesh = new OBJLoader().loadMesh("/models/Tiger.obj", new Vector3f(192, 142, 0), false);

    private Hud hud;
    private EntityHud entityHud;

    private Game game;
    private Window window;
    private Terrain terrain;
    private SceneLight sceneLight;

    private Fog fog;

    private int deerMales, deerFemales, tigerMales, tigerFemales;
    private boolean entitySelected = false;

    public Scene(Game game, Terrain terrain, Window window) throws Exception {
        this.game = game;
        this.terrain = terrain;
        this.window = window;
        meshMap = new HashMap();
        tempMap = new HashMap();
        hud = new Hud();
        hud.init(window);
        entityHud = new EntityHud();
        entityHud.init(window);


        fog = Fog.NOFOG;
    }

    public Map<Mesh, List<GameItem>> getGameMeshes() {
        return meshMap;
    }


    public void setMeshMap(Map<Mesh, List<GameItem>> meshMap) {
        this.meshMap = meshMap;
    }

    public void addStaticGameItem(StaticGameItem staticGameItem) {
        Mesh[] meshes = staticGameItem.getMeshes();
        for(Mesh mesh : meshes) {
            List<GameItem> list = tempMap.get(mesh);
            if (list == null) {
                list = new ArrayList<>();
                tempMap.put(mesh, list);
            }
            list.add(staticGameItem);
            gameItems.add(staticGameItem);
        }
    }

        public void addDynamicGameItem(DynamicGameItem dynamicGameItem) {
            Mesh[] meshes = dynamicGameItem.getMeshes();
            for(Mesh mesh : meshes) {
                List<GameItem> list = tempMap.get(mesh);
                if (list == null) {
                    list = new ArrayList<>();
                    tempMap.put(mesh, list);
                }
                list.add(dynamicGameItem);
                gameItems.add(dynamicGameItem);
            }
        }

    public void addBoundingBox(GameItem boundingBoxItem) {
        Mesh[] meshes = boundingBoxItem.getMeshes();
        for(Mesh mesh : meshes) {
            List<GameItem> list = tempMap.get(mesh);
            if (list == null) {
                list = new ArrayList<>();
                tempMap.put(mesh, list);

            }
            list.add(boundingBoxItem);
            gameItems.add(boundingBoxItem);
        }
    }





    public void updateMeshMap(){
        if(!tempMap.isEmpty()){
            for(Mesh m : tempMap.keySet()){
                List list = meshMap.get(m);
                if (list == null) {
                    list = new ArrayList<>();
                    meshMap.put(m, list);
                }
                list.addAll(tempMap.get(m));
            }
        }
        tempMap = new HashMap<>();
    }




    public void update() throws Exception {
        //add every game item in hashmap to gameitem list
//        for(Mesh m : meshMap.keySet()){
//            List list = meshMap.get(m);
//            for(Object g : list){
//                if(!gameItems.contains(g)){
//                    gameItems.add((GameItem) g);
//                }
//            }
//        }

        if(gameItems.size() > 0){
            gameItemsIteration.addAll(gameItems);
            gameItems = new ArrayList<>();
        }

        if(removedItems.size() > 0){
            gameItemsIteration.removeAll(removedItems);
            deers.removeAll(removedItems);
            tigerList.removeAll(removedItems);
            grassList.removeAll(removedItems);
            removedItems = new ArrayList<>();
        }


        if(gameItemsIteration.size() > 1) {
            entitySelected = false;
            for (GameItem gameItem : gameItemsIteration) {
                //while looping through entities check if any are selected, if not then don't render any hud's for entities
                if(gameItem.isSelected()){
                    entitySelected = true;
                }

                gameItem.update();
                if(gameItem instanceof Grass) {
                    if (!grassList.contains(gameItem)) {
                        grassList.add((Grass) gameItem);
                    }

                    for (Grass g : grassList) {
                        if (g == gameItem) {
                            continue;
                        }

                        if (((Grass) gameItem).boxIntersection(((Grass) gameItem).getAwarenessBox(), g.getBoundingBox())) {
                            if(!((Grass) gameItem).getIntersectingGrassObjects().contains(g)){
                                ((Grass) gameItem).addIntersectingGrassObject(g);
                            }
                        } else {
                            if (((Grass) gameItem).getIntersectingGrassObjects().contains(g)) {
                                ((Grass) gameItem).removeIntersectingObject(g);
                            }
                        }

                    }
                }

                if(gameItem instanceof Tiger){
                    if(!tigerList.contains(gameItem)){
                        tigerList.add((Tiger) gameItem);
                    }

                    for(Tiger t : tigerList) {
                        if (t == gameItem) {
                            continue;
                        }
                        //add tiger for potential mating
                        if (((Tiger) gameItem).boxIntersection(((Tiger) gameItem).getAwarenessBox(), t.getBoundingBox())) {
                            if (!((Tiger) gameItem).getIntersectingSameSpecies().contains(t)) {
                                ((Tiger) gameItem).addIntersectingSameSpecies(t);
                            } else {
                                if (((Tiger) gameItem).getIntersectingSameSpecies().contains(t)) {
                                    ((Tiger) gameItem).removeIntersectingSameSpecies(t);
                                }
                            }
                        }
                    }

                    //tiger prey handling
                    for(Deer d : deers) {
                        if (((Tiger) gameItem).boxIntersection(((Tiger) gameItem).getAwarenessBox(), d.getBoundingBox())) {
                            if((!((Tiger) gameItem).getIntersectingPreyObjects().contains(d)) && ((Tiger) gameItem).getEnergy() > 5f){
                                ((Tiger) gameItem).addIntersectingPreyObject(d);
                            }
                        } else {
                                ((Tiger) gameItem).removeIntersectingPreyObject(d);
                        }
                    }
                }

                //deer logic
                if(gameItem instanceof Deer){
                    if(!deers.contains(gameItem)){
                        deers.add((Deer) gameItem);
                    }
                    //handle deer breeding
                    for(Deer d : deers) {
                        //dont check itself
                        if (d == gameItem) {
                            continue;
                        }

                        if (((Deer) gameItem).boxIntersection(((Deer) gameItem).getAwarenessBox(), d.getBoundingBox())) {
                            if(!((Deer) gameItem).getIntersectingSameSpecies().contains(d)) {
                                ((Deer) gameItem).addIntersectingSameSpecies(d);
                            }
                        } else {
                            if (((Deer) gameItem).getIntersectingSameSpecies() != null) {
                                if (((Deer) gameItem).getIntersectingObjects().contains(d)) {
                                    ((Deer) gameItem).removeIntersectingObject(d);
                                }

                                if (d.getIntersectingSameSpecies().contains(gameItem)) {
                                    d.removeIntersectingSameSpecies((Deer) gameItem);
                                }
                            }
                        }
                    }


                    //handle predators
                    for(Tiger t : tigerList) {
                        if (((Deer) gameItem).boxIntersection(((Deer) gameItem).getAwarenessBox(), t.getBoundingBox())) {
                            if(!((Deer) gameItem).getIntersectingPredatorObjects().contains(t)) {
                                ((Deer) gameItem).addIntersectingPredatorObject(t);
                            }
                        } else {
                            if (((Deer) gameItem).getIntersectingPredatorObjects() != null) {
                                if (((Deer) gameItem).getIntersectingPredatorObjects().contains(t)) {
                                    ((Deer) gameItem).removeIntersectingPredatorObject(t);
                                }
                            }
                        }
                    }


                    //handle deer eating
                    for(Grass gr : grassList) {
                        if (((Deer) gameItem).boxIntersection(((Deer) gameItem).getAwarenessBox(), gr.getBoundingBox())) {
                            if (!((Deer) gameItem).getIntersectingObjects().contains(gr)) {
                                ((Deer) gameItem).addIntersectingPlantObject(gr);
                            }
                        } else {
                            if (((Deer) gameItem).getIntersectingObjects().contains(gr)) {
                                ((Deer) gameItem).removeIntersectingObject(gr);
                            }
                        }
                    }
                }
            }
        }
    }


    public void cleanup() {
//        for (Mesh mesh : meshMap.keySet()) {
//            mesh.cleanUp();
//        }
    }

    public void addTiger(Vector3f spawnLocation, boolean newAdd, Tiger mother, Tiger father){
        Random random = new Random();
        float maxHunger = (random.nextFloat() * (200f - 100f) + 100f);
        float maxSize = random.nextFloat() * (0.0009f - 0.0004f) + 0.0004f;
        float awareness = random.nextFloat() * (5f - 3f) + 3f;
        float speed = random.nextFloat() * (0.013f - 0.009f) + 0.009f;
        float maxSpeed = random.nextFloat() * (0.04f - 0.02f) + 0.02f;
        float maxEnergy = random.nextFloat() * (70f - 30f) + 30f;
        Tiger tiger;
        if(spawnLocation == null) {
            tiger = new Tiger(game.getScene(), tigerMesh, randomSpawn(), terrain, awareness, maxHunger, 0, speed, maxSpeed, maxEnergy, maxEnergy, 0.0002f, maxSize);
        }else{
            tiger = new Tiger(game.getScene(), tigerMesh, spawnLocation, terrain, awareness, maxHunger, 0, speed, maxSpeed, maxEnergy, maxEnergy,0.0002f, maxSize);
        }

        if(newAdd){
            animalCrossOver(mother, father, tiger);
        }

        tiger.setHud(entityHud);
        tiger.setScale(0.0005f);
        tiger.setRotation(0, 180f, 0);
        tiger.getBoundingBox().setRotation(0, 180f, 0);
        tiger.getAwarenessBox().setRotation(0, 180f, 0);
        if(newAdd){
            tiger.setMother(mother);
            tiger.setFather(father);
            tiger.addGeneration(mother.getGeneration());
        }

        addDynamicGameItem(tiger);
//        addBoundingBox(tiger.getBoundingBox());
//        addBoundingBox(tiger.getAwarenessBox());
        addBoundingBox(tiger.getDestinationPoint());
    }

    public void addDeer(Vector3f spawnLocation, boolean newAdd, Deer mother, Deer father){
        Deer deer;
        Random random = new Random();
        float maxHunger = (random.nextFloat() * (200f - 100f) + 100f);
        float maxSize = random.nextFloat() * (0.00165f - 0.0015f) + 0.0015f;
        float awareness = random.nextFloat() * (4.5f - 2.5f) + 2.5f;
        float speed = random.nextFloat() * (0.02f - 0.01f) + 0.01f;
        float maxSpeed = random.nextFloat() * (0.04f - 0.02f) + 0.02f;
        float maxEnergy = random.nextFloat() * (100f - 30f) + 30f;
        Mesh dM;

        dM = deerMesh;

        if(spawnLocation == null){
                deer = new Deer(game.getScene(), dM, randomSpawn(), terrain, awareness, maxHunger, 0, speed, maxSpeed, maxEnergy, maxEnergy,0.0005f, maxSize);
            }else{
                deer = new Deer(game.getScene(), dM, spawnLocation, terrain, awareness, maxHunger, 0, speed, maxSpeed, maxEnergy, maxEnergy, 0.0005f, maxSize);
            }

        if(newAdd){
            animalCrossOver(mother, father, deer);
        }

            deer.setHud(entityHud);
            deer.setTerrain(terrain);
            deer.setScale(0.0005f);
            deer.setRotation(0, 180f, 0);
            deer.getBoundingBox().setRotation(0, 180f, 0);
            deer.getAwarenessBox().setRotation(0, 180f, 0);

            if(newAdd){
                deer.setMother(mother);
                deer.setFather(father);
                deer.addGeneration(mother.getGeneration());
            }

            addDynamicGameItem(deer);
//            addBoundingBox(deer.getBoundingBox());
//            addBoundingBox(deer.getAwarenessBox());
            addBoundingBox(deer.getDestinationPoint());
    }

    public void addGrass() throws Exception {
        Grass grass;
        grass = new Grass(game.getScene(), terrain, grassMesh, randomSpawn());
        grass.setMaxSize(0.1f);
        grass.setScale(0.001f);
        addStaticGameItem(grass);

        grass.setHud(entityHud);
    }

    public Vector3f randomSpawn(){
        Random r = new Random();
        float x = r.nextFloat() * 30f - 10f;
        float z = r.nextFloat() * 30f - 10f;
        float y = r.nextFloat();
        //10f for world offset
        Vector3f vec = new Vector3f(x, 1f , z);
        float pos = terrain.getHeight(vec);

        return new Vector3f(x, pos, z);
    }

//    public Map<Mesh, List<GameItem>> getBoundingMap() {
//        return boundingMap;
//    }


    public SceneLight getSceneLight() {
        return sceneLight;
    }

    public void setSceneLight(SceneLight sceneLight) {
        this.sceneLight = sceneLight;
    }

    public void removeItem(GameItem gameItem){
//        List list = meshMap.get(gameItem.getMesh());
//        if(list.contains(gameItem)){
//            list.remove(gameItem);
//        }


        if(meshMap.get(gameItem.getMesh()) != null) {
            meshMap.get(gameItem.getMesh()).remove(gameItem);
        }

        removedItems.add(gameItem);
    }


    public void countGenders(){
        int tempDM = 0, tempDF = 0, tempTM = 0, tempTF = 0;
        for(Deer d : deers){
            if(d.isMale()){
                tempDM++;
            }else{
                tempDF++;
            }
        }

        for(Tiger t : tigerList){
            if(t.isMale()){
                tempTM++;
            }else{
                tempTF++;
            }
        }

        deerMales = tempDM;
        deerFemales = tempDF;
        tigerMales = tempTM;
        tigerFemales = tempTF;

    }


    public float averageDeerAwareness(){
        float awareness = 0;
        for(int i = 0; i < deers.size() -1; i++){
            awareness += deers.get(i).getAwareness();
        }

        float avg = awareness / deers.size();
        return avg;
    }


    public float averageTigerAwareness(){
        float awareness = 0;
        for(int i = 0; i < tigerList.size() -1; i++){
            awareness += tigerList.get(i).getAwareness();
        }

        float avg = awareness / tigerList.size();
        return avg;
    }

    public float averageDeerMaxHunger(){
        float maxHunger = 0;
        for(int i = 0; i < deers.size() -1; i++){
            maxHunger += deers.get(i).getMaxHunger();
        }

        float avg = maxHunger / deers.size();
        return avg;
    }


    public float averageTigerMaxHunger(){
        float maxHunger = 0;
        for(int i = 0; i < tigerList.size() -1; i++){
            maxHunger += tigerList.get(i).getMaxHunger();
        }

        float avg = maxHunger / tigerList.size();
        return avg;
    }



    public float averageDeerMaxEnergy(){
        float maxEnergy = 0;
        for(int i = 0; i < deers.size() -1; i++){
            maxEnergy += deers.get(i).getEnergy();
        }

        float avg = maxEnergy / deers.size();
        return avg;
    }


    public float averageTigerMaxEnergy(){
        float maxEnergy = 0;
        for(int i = 0; i < tigerList.size() -1; i++){
            maxEnergy += tigerList.get(i).getEnergy();
        }

        float avg = maxEnergy / tigerList.size();
        return avg;
    }


    public int maxDeerGeneration(){
        int generation = 1;
        for(int i = 0; i < deers.size() -1; i++){
            if(deers.get(i).getGeneration() > generation){
                generation = deers.get(i).getGeneration();
            }
        }

        return generation;
    }

    public int maxTigerGeneration(){
        int generation = 1;
        for(int i = 0; i < tigerList.size() -1; i++){
            if(tigerList.get(i).getGeneration() > generation){
                generation = tigerList.get(i).getGeneration();
            }
        }

        return generation;
    }

    public void animalCrossOver(Animal a1, Animal a2, Animal offSpring){
        float awareness = Math.max(a1.getAwareness(), a2.getAwareness());
        float maxSize = Math.max(a1.getMaxSize(), a2.getMaxSize());
        float maxEnergy = Math.max(a1.getMaxEnergy(), a2.getMaxEnergy());
        float speed = Math.max(a1.getSpeed(), a2.getSpeed());
        float maxSpeed = Math.max(a1.getMaxSpeed(), a2.getMaxSpeed());
        float maxHunger = Math.max(a1.getMaxHunger(), a2.getMaxHunger());

        offSpring.setAwareness(awareness);
        offSpring.setMaxSize(maxSize);
        offSpring.setMaxEnergy(maxEnergy);
        offSpring.setSpeed(speed);
        offSpring.setMaxSpeed(maxSpeed);
        offSpring.setMaxHunger(maxHunger);
    }

    /**
     * @return the fog
     */
    public Fog getFog() {
        return fog;
    }

    /**
     * @param fog the fog to set
     */
    public void setFog(Fog fog) {
        this.fog = fog;
    }

    public List<GameItem> getGameItems() {
        return gameItemsIteration;
    }


    public EntityHud getEntityHud() {
        return entityHud;
    }


    public boolean isEntitySelected() {
        return entitySelected;
    }


    public Hud getHud() {
        return hud;
    }

    public List<Deer> getDeers() {
        return deers;
    }

    public List<Tiger> getTigerList() {
        return tigerList;
    }

    public List<Grass> getGrassList() {
        return grassList;
    }

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public int getDeerMales() {
        return deerMales;
    }

    public void setDeerMales(int deerMales) {
        this.deerMales = deerMales;
    }

    public int getDeerFemales() {
        return deerFemales;
    }

    public void setDeerFemales(int deerFemales) {
        this.deerFemales = deerFemales;
    }

    public int getTigerMales() {
        return tigerMales;
    }

    public void setTigerMales(int tigerMales) {
        this.tigerMales = tigerMales;
    }

    public int getTigerFemales() {
        return tigerFemales;
    }

    public void setTigerFemales(int tigerFemales) {
        this.tigerFemales = tigerFemales;
    }
}
