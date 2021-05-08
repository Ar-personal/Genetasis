package main;

import engine.entities.Deer;
import engine.entities.GameItem;
import engine.entities.TerrainItem;
import engine.objects.Camera;
import engine.objects.Terrain;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.primitives.Intersectionf;

import java.util.List;

public class CameraBoxSelectionDetector {

    private final Vector3f max;

    private final Vector3f min;

    private final Vector2f nearFar;

    private Vector3f dir;

    public CameraBoxSelectionDetector() {
        dir = new Vector3f();
        min = new Vector3f();
        max = new Vector3f();
        nearFar = new Vector2f();
    }

    public void selectGameItem(List<GameItem> gameItems, Camera camera) {
        dir = camera.getViewMatrix().positiveZ(dir).negate();
        selectGameItem(gameItems, camera.getPosition(), dir);
    }

    protected void selectGameItem(List<GameItem> gameItems, Vector3f center, Vector3f dir) {
        GameItem selectedGameItem = null;
        float closestDistance = Float.POSITIVE_INFINITY;

        for (GameItem gameItem : gameItems) {
            if(!(gameItem instanceof TerrainItem)) {
                if(!(gameItem instanceof Deer)){
                    gameItem.setSelected(false);
                    min.set(gameItem.getPosition());
                    max.set(gameItem.getPosition());
                    min.add(-gameItem.getScale(), -gameItem.getScale(), -gameItem.getScale());
                    max.add(gameItem.getScale(), gameItem.getScale(), gameItem.getScale());
                    if (Intersectionf.intersectRayAab(center, dir, min, max, nearFar) && nearFar.x < closestDistance) {
                        closestDistance = nearFar.x;
                        selectedGameItem = gameItem;
                    }
                }else{
                    gameItem.setSelected(false);
                    min.set(gameItem.getPosition());
                    max.set(gameItem.getPosition());
                    min.add(-0.1f, -0.1f, -0.1f);
                    max.add(0.1f, 0.1f, 0.1f);
                    if (Intersectionf.intersectRayAab(center, dir, min, max, nearFar) && nearFar.x < closestDistance) {
                        closestDistance = nearFar.x;
                        selectedGameItem = gameItem;
                    }
                }
            }
        }

        if (selectedGameItem != null) {
            selectedGameItem.setSelected(true);
        }
    }
}
