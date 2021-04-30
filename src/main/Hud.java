package main;

import engine.entities.GameItem;
import engine.entities.HudItem;
import engine.entities.StaticGameItem;
import engine.graphics.Material;
import engine.graphics.Mesh;
import engine.graphics.Window;
import engine.utils.FontTexture;
import engine.utils.IHud;
import engine.utils.OBJLoader;
import engine.entities.TextItemStatic;
import org.joml.Vector4f;

import java.awt.*;

public class Hud implements IHud{

    private static final Font FONT = new Font("Arial", Font.PLAIN, 20);

    private static final String CHARSET = "ISO-8859-1";

    private final GameItem[] hudItems;

    private final TextItemStatic statusTextItem;

    private final GameItem compassItem;

    public Hud(String statusText) throws Exception {
        FontTexture fontTexture = new FontTexture(FONT, CHARSET);
        this.statusTextItem = new TextItemStatic(statusText, fontTexture);
//        this.statusTextItem.getMesh().getMaterial().setAmbientColour(new Vector4f(1, 1, 1, 1));

        // Create compass
        Mesh mesh = new OBJLoader().loadMesh("/models/compass.obj", null, false);
        Material material = new Material();
        material.setAmbientColour(new Vector4f(1, 0, 0, 1));
        mesh.setMaterial(material);
        compassItem = new HudItem(mesh) {
        };
        compassItem.setScale(40.0f);
        // Rotate to transform it to screen coordinates
        compassItem.setRotation(0f, 0f, 180f);

        // Create list that holds the items that compose the HUD
        hudItems = new GameItem[]{statusTextItem, compassItem};
    }

    public void setStatusText(String statusText) {
        this.statusTextItem.setText(statusText);
    }

    public void rotateCompass(float angle) {
        this.compassItem.setRotation(0, 0, 180 + angle);
    }

    @Override
    public GameItem[] getGameItems() {
        return hudItems;
    }

    public void updateSize(Window window) {
        this.statusTextItem.setPosition(10f, window.getHeight() - 50f, 0);
        this.compassItem.setPosition(window.getWidth() - 40f, 50f, 0);
    }
}