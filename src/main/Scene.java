package main;

import engine.entities.*;
import engine.graphics.Fog;
import engine.graphics.Mesh;
import engine.hud.EntityHud;
import engine.hud.Hud;
import engine.lights.SceneLight;
import engine.graphics.Window;

import engine.objects.Terrain;
import engine.utils.OBJLoader;
import org.joml.Vector3f;

import java.util.*;

public class Scene {

    private Map<Mesh, List<GameItem>> meshMap;
    private Map<Mesh, List<GameItem>> tempMap;
    private List<GameItem> gameItems = new ArrayList<>();
    private List<GameItem> itemsToBeAdded = new ArrayList<>();
    private List<GameItem> removedItems = new ArrayList<>();
    private List<Deer> deers = new ArrayList<>();
    private List<Grass> grassList = new ArrayList<>();

    private Mesh deerMesh = new OBJLoader().loadMesh("/models/Deer.obj", new Vector3f(60, 15, 30), false);
    private Mesh deerChildMesh = new OBJLoader().loadMesh("/models/Deer.obj", new Vector3f(60, 255, 30), false);

    private Hud hud;
    private EntityHud entityHud;

    private Game game;
    private Window window;
    private Terrain terrain;
    private SceneLight sceneLight;

    private Fog fog;

    private int numGameItems;
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
        tempMap = new HashMap<Mesh, List<GameItem>>();
    }




    public void update(){
        //add every game item in hashmap to gameitem list
        for(Mesh m : meshMap.keySet()){
            List list = meshMap.get(m);
            for(Object g : list){
                if(!gameItems.contains(g)){
                    gameItems.add((GameItem) g);
                }
            }
        }

        if(gameItems.size() > 1) {
            entitySelected = false;
            for (GameItem gameItem : gameItems) {
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
                            ((Grass) gameItem).addIntersectingGrassObject(g);
                        } else {
                            if (((Grass) gameItem).getIntersectingGrassObjects() != null) {
                                if (((Grass) gameItem).getIntersectingGrassObjects().contains(g)) {
                                    ((Grass) gameItem).removeIntersectingObject(g);
                                }
                            }
                        }
                    }
                }

                if(gameItem instanceof Deer){
                    //check for mates
                    if(!deers.contains(gameItem)){
                        deers.add((Deer) gameItem);
                    }
                    for(Deer d : deers) {
                        //dont check itself
                        if (d == gameItem) {
                            continue;
                        }

                        //add eachother to intersecting obj list
                        if (((Deer) gameItem).boxIntersection(((Deer) gameItem).getAwarenessBox(), d.getBoundingBox())) {
                            ((Deer) gameItem).addIntersectingDeerObject(d);
                        } else {
                            if (((Deer) gameItem).getIntersectingDeerObjects() != null) {
                                if (((Deer) gameItem).getIntersectingObjects().contains(d)) {
                                    ((Deer) gameItem).removeIntersectingObject(d);
                                }

                                if (d.getIntersectingDeerObjects().contains(gameItem)) {
                                    d.removeIntersectingDeerObject((Deer) gameItem);
                                }
                            }
                        }
                    }


                    //check for edible plants
                    for (Grass gr : grassList) {
                        if (((Deer) gameItem).boxIntersection(((Deer) gameItem).getAwarenessBox(), gr.getBoundingBox())) {
                                if (!((Deer) gameItem).getIntersectingObjects().contains(gr)) {
                                    ((Deer) gameItem).addIntersectingPlantObject(gr);
                                }
                        } else {
                            if (((Deer) gameItem).getIntersectingObjects() != null) {
                                if (((Deer) gameItem).getIntersectingObjects().contains(gr)) {
                                    ((Deer) gameItem).removeIntersectingObject(gr);
                                }
                            }
                        }
                    }
                }
            }

            if(removedItems.size() > 0){
                for(GameItem g : removedItems){
                    if(gameItems.contains(g)){
                        gameItems.remove(g);
                    }
                }
            }
            //remove items
        }
    }


    public void cleanup() {
        for (Mesh mesh : meshMap.keySet()) {
            mesh.cleanUp();
        }
    }



    public void addDeer(Vector3f spawnLocation, boolean newAdd){
        Deer deer;
        Random random = new Random();
            float maxHunger = (random.nextFloat() * (200f - 100f) + 100f);
            float maxSize = random.nextFloat() * (0.002f - 0.0015f) + 0.0015f;
        Mesh dM;

        if(!newAdd){
            dM = deerMesh;
        }else {
            dM = deerChildMesh;
        }

        if(spawnLocation == null){
                deer = new Deer(game.getScene(), dM, randomSpawn(), 3f, maxHunger, 0, 0.01f, 10, 10, 50, maxSize);
            }else{
                deer = new Deer(game.getScene(), dM, spawnLocation, 3f, maxHunger, 0, 0.01f, 10, 10, 50, maxSize);
            }

            deer.setHud(entityHud);
            deer.setTerrain(terrain);
            deer.setScale(0.0005f);
            deer.setSize(0.0005f);
            deer.setRotation(0, 180f, 0);
            deer.getBoundingBox().setRotation(0, 180f, 0);
            deer.getAwarenessBox().setRotation(0, 180f, 0);
            deer.addGeneration();


            addDynamicGameItem(deer);
//            addBoundingBox(deer.getBoundingBox());
//            addBoundingBox(deer.getAwarenessBox());
            addBoundingBox(deer.getDestinationPoint());




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
        if(meshMap.get(gameItem.getMesh()) != null) {
            meshMap.get(gameItem.getMesh()).remove(gameItem);
        }
        removedItems.add(gameItem);

        if(gameItem instanceof Deer){
            deers.remove(gameItem);
        }

        if(gameItem instanceof Grass){
            grassList.remove(gameItem);
        }
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
        return gameItems;
    }

    public List<GameItem> getRemovedItems() {
        return removedItems;
    }

    public EntityHud getEntityHud() {
        return entityHud;
    }

    public void setEntityHud(EntityHud entityHud) {
        this.entityHud = entityHud;
    }

    public boolean isEntitySelected() {
        return entitySelected;
    }

    public void setEntitySelected(boolean entitySelected) {
        this.entitySelected = entitySelected;
    }

    public List<Grass> getGrassList() {
        return grassList;
    }

    public void addGrassItem(Grass g){
        grassList.add(g);
    }

    public void removeGrassItem(Grass g){
        grassList.remove(g);
    }

    public List<GameItem> getItemsToBeAdded() {
        return itemsToBeAdded;
    }

    public void resetItemsToBeAdded() {
        this.itemsToBeAdded = new ArrayList<>();
    }

    public void setGrassList(List<Grass> grassList) {
        this.grassList = grassList;
    }

    public Hud getHud() {
        return hud;
    }

    public void setHud(Hud hud) {
        this.hud = hud;
    }
}
