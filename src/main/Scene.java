package main;

import engine.entities.DynamicGameItem;
import engine.entities.GameItem;
import engine.entities.StaticGameItem;
import engine.graphics.Fog;
import engine.graphics.Mesh;
import engine.graphics.SceneLight;
import engine.objects.SkyBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {

    private Map<Mesh, List<GameItem>> meshMap;
//    private Map<Mesh, List<GameItem>> boundingMap;
    private List<GameItem> gameItems = new ArrayList<>();

    private SkyBox skyBox;

    private SceneLight sceneLight;

    private Fog fog;

    private int numGameItems;

    public Scene() {
        meshMap = new HashMap();
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
        List<GameItem> list = meshMap.get(boundingBoxItem.getMesh());
        if ( list == null ) {
            list = new ArrayList<>();
            meshMap.put(boundingBoxItem.getMesh(), list);
        }
        list.add(boundingBoxItem);
        gameItems.add(boundingBoxItem);
    }


    public void cleanup() {
        for (Mesh mesh : meshMap.keySet()) {
            mesh.cleanUp();
        }
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
}
