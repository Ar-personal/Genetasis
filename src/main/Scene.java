package main;

import engine.entities.*;
import engine.graphics.Fog;
import engine.graphics.Mesh;
import engine.graphics.SceneLight;
import engine.objects.SkyBox;
import engine.objects.Terrain;
import engine.utils.OBJLoader;
import org.joml.Vector3f;

import java.util.*;

public class Scene {

    private Map<Mesh, List<GameItem>> meshMap;
//    private Map<Mesh, List<GameItem>> boundingMap;
    private List<GameItem> gameItems = new ArrayList<>();
    private List<GameItem> removedItems;
    private List<Deer> deers = new ArrayList<>();

    private Game game;
    private SkyBox skyBox;
    private Terrain terrain;
    private SceneLight sceneLight;

    private Fog fog;

    private int numGameItems;

    public Scene(Game game, Terrain terrain) {
        this.game = game;
        meshMap = new HashMap();
        this.terrain = terrain;
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
            for (GameItem gameItem : gameItems) {
                gameItem.update();

                if(gameItem instanceof Deer){
                    List<Grass> grasses = new ArrayList<>();
                    for (GameItem g : gameItems) {
                        if(g instanceof Grass){
                            grasses.add((Grass) g);
                        }
                    }
                    for(Deer d : deers) {
                        for (Grass gr : grasses) {
                            if (d.boxIntersection(d.getAwarenessBox(), gr.getBoundingBox())) {
                                d.addIntersectingPlantObject(gr);
                            } else {
                                if (d.getIntersectingObjects() != null) {
                                    if (d.getIntersectingObjects().contains(gr)) {
                                        d.removeIntersectingObject(gr);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(removedItems !=null) {
                gameItems.removeAll(removedItems);
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
            deer.setTerrain(terrain);
            deer.setScale(0.001f);
            deer.setRotation(0, 180f, 0);
            deer.getBoundingBox().setRotation(0, 180f, 0);
            deer.getAwarenessBox().setRotation(0, 180f, 0);
            deers.add(deer);
            addDynamicGameItem(deer);
//            addBoundingBox(deer.getBoundingBox());
//            addBoundingBox(deer.getAwarenessBox());
//            addBoundingBox(deer.getDestinationPoint());

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
        removedItems = new ArrayList<>();
        removedItems.add(gameItem);

        if(gameItem instanceof Deer){
            deers.remove(deers);
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
}
