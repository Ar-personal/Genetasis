package main;

import engine.entities.*;
import engine.graphics.Fog;
import engine.graphics.Mesh;
import engine.graphics.SceneLight;
import engine.graphics.Window;
import engine.objects.SkyBox;
import engine.objects.Terrain;
import engine.utils.OBJLoader;
import org.joml.Vector3f;

import java.util.*;

public class Scene {

    private Map<Mesh, List<GameItem>> meshMap;
//    private Map<Mesh, List<GameItem>> boundingMap;
    private List<GameItem> gameItems = new ArrayList<>();
    private List<GameItem> removedItems = new ArrayList<>();
    private List<Deer> deers = new ArrayList<>();
    private List<Grass> grassList = new ArrayList<>();

    private EntityHud entityHud;

    private Game game;
    private Window window;
    private SkyBox skyBox;
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
        entityHud = new EntityHud();
        entityHud.init(window);

//        boundingMap = new HashMap<>();
        fog = Fog.NOFOG;
    }

    public Map<Mesh, List<GameItem>> getGameMeshes() {
        return meshMap;
    }

    public void setGameItems(StaticGameItem[] staticGameItems) {
        numGameItems = staticGameItems != null ? staticGameItems.length : 0;
        for (int i=0; i<numGameItems; i++) {
            StaticGameItem staticGameItem = staticGameItems[i];
            Mesh mesh = staticGameItem.getMesh();
            List<GameItem> list = meshMap.get(mesh);
            if ( list == null ) {
                list = new ArrayList<>();
                meshMap.put(mesh, list);
            }
            list.add(staticGameItem);
            gameItems.add(staticGameItem);
        }
    }

    public void addStaticGameItem(StaticGameItem staticGameItem) {
        numGameItems++;
        List<GameItem> list = meshMap.get(staticGameItem.getMesh());
        if ( list == null ) {
            list = new ArrayList<>();
            meshMap.put(staticGameItem.getMesh(), list);
        }
        list.add(staticGameItem);
        gameItems.add(staticGameItem);
    }

        public void addDynamicGameItem(DynamicGameItem dynamicGameItem) {
        numGameItems++;
            List<GameItem> list = meshMap.get(dynamicGameItem.getMesh());
            if ( list == null ) {
                list = new ArrayList<>();
                meshMap.put(dynamicGameItem.getMesh(), list);
            }
            list.add(dynamicGameItem);
            gameItems.add(dynamicGameItem);
    }


    public void addBoundingBox(GameItem boundingBoxItem) {
        numGameItems++;
        List<GameItem> list = meshMap.get(boundingBoxItem.getMesh());
        if ( list == null ) {
            list = new ArrayList<>();
            meshMap.put(boundingBoxItem.getMesh(), list);
        }
        list.add(boundingBoxItem);
        gameItems.add(boundingBoxItem);
    }

    public void update(){
        if(gameItems != null) {
            entitySelected = false;
            for (GameItem gameItem : gameItems) {
                //while looping through entities check if any are selected, if not then don't render any hud's for entities
                if(gameItem.isSelected()){
                    entitySelected = true;
                }
                gameItem.update();

                if(gameItem instanceof Deer){
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
            if(removedItems !=null) {
                gameItems.removeAll(removedItems);
                removedItems = new ArrayList<>();
            }
        }
    }


    public void cleanup() {
        for (Mesh mesh : meshMap.keySet()) {
            mesh.cleanUp();
        }
    }


    public void addDeers(int amt) throws Exception {
        Deer deer;
        Random random = new Random();
        for(int i = 0; i < amt; i++) {
            float maxHunger = (random.nextFloat() * (200f - 100f) + 100f);
            Mesh deerMesh = new OBJLoader().loadMesh("/models/Deer.obj", new Vector3f(60, 15, 30), false);
            deer = new Deer(game.getScene(), deerMesh, randomSpawn(), 3f, maxHunger, 0, 0.01f, 10, 10, 50, 0.001f);
            deer.setHud(entityHud);
            deer.setTerrain(terrain);
            deer.setScale(0.001f);
            deer.setRotation(0, 180f, 0);
            deer.getBoundingBox().setRotation(0, 180f, 0);
            deer.getAwarenessBox().setRotation(0, 180f, 0);
            deers.add(deer);

            addDynamicGameItem(deer);
//            addBoundingBox(deer.getBoundingBox());
//            addBoundingBox(deer.getAwarenessBox());
            addBoundingBox(deer.getDestinationPoint());

        }
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

    public SkyBox getSkyBox() {
        return skyBox;
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }

    public SceneLight getSceneLight() {
        return sceneLight;
    }

    public void setSceneLight(SceneLight sceneLight) {
        this.sceneLight = sceneLight;
    }

    public void removeItem(GameItem gameItem){
//        gameItems.remove(gameItem);
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



    public void setGrassList(List<Grass> grassList) {
        this.grassList = grassList;
    }
}
