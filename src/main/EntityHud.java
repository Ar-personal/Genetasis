package main;

import engine.graphics.Window;
import engine.utils.Utils;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryUtil;

import static java.sql.Types.NULL;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVG.nvgFill;
import static org.lwjgl.nanovg.NanoVGGL2.*;

public class EntityHud extends Hud {
    protected float awareness = 1f;
    protected float hunger = 20f;
    protected float maxHunger;
    protected int thirst = 0;
    protected float size;
    protected float speed;
    protected float awarenessScale;
    protected int health = 0;
    protected int stamina;
    protected int energy;
    protected float marginLeft = 100f, marginTop = 200f, textPadding = 28f, windowHeight = 300f, windowWidth, barHeight = textPadding - 5f, barY = marginTop + textPadding + 10f;


    public void init(Window window) throws Exception {
        this.window = window;
        this.vg = window.getOpts().antialiasing ? nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES) : nvgCreate(NVG_STENCIL_STROKES);
        if (this.vg == NULL) {
            throw new Exception("Could not init nanovg");
        }

        fontBuffer = Utils.ioResourceToByteBuffer("C:\\Users\\Alex\\Dropbox\\Game Design\\Genetasis\\resources\\fonts\\OpenSans-Bold.ttf", 150 * 1024);
        int font = nvgCreateFontMem(vg, FONT_NAME, fontBuffer, 0);
        if (font == -1) {
            throw new Exception("Could not add font");
        }
        colour = NVGColor.create();

        posx = MemoryUtil.memAllocDouble(1);
        posy = MemoryUtil.memAllocDouble(1);

        windowWidth = window.getWidth() / 7;
    }

    public void render(){
        nvgBeginFrame(vg, window.getWidth(), window.getHeight(), 1);
        float hungerRatio = hunger / maxHunger;
        float maxHungerRatio = (windowWidth - (textPadding * 2)) / maxHunger;
        float newRatio = hunger / maxHungerRatio;


        nvgBeginPath(vg);
        nvgRect(vg, marginLeft, marginTop, windowWidth, windowHeight);
        nvgFill(vg);
        nvgFillColor(vg, rgba(83, 83, 83, 150, colour));

        //text colour
        nvgBeginPath(vg);
        nvgFillColor(vg, rgba(22, 25, 30, 155, colour));

        nvgFontSize(vg, 26f);
        nvgText(vg, marginLeft + textPadding, marginTop + textPadding, "Entity Type: Deer");

        //red bar
        nvgBeginPath(vg);
        nvgFillColor(vg, rgba(255, 43, 28, 150, colour));
        nvgRect(vg, marginLeft + textPadding, marginTop + (textPadding + 10f), windowWidth - (textPadding * 2), barHeight);
        nvgFill(vg);

        //green bar
        nvgBeginPath(vg);
        nvgFillColor(vg, rgba(121, 255, 43, 150, colour));
        nvgRect(vg, marginLeft + textPadding, marginTop + (textPadding + 10f), hunger, barHeight);
        nvgFill(vg);

        //text colour
        nvgBeginPath(vg);
        nvgFillColor(vg, rgba(22, 25, 30, 155, colour));

        //overlay
        nvgText(vg, marginLeft + textPadding, marginTop + textPadding * 2, "Hunger: " + String.format("%.2f", hunger) + " / " + (int) maxHunger);

        nvgText(vg, marginLeft + textPadding, marginTop + textPadding * 3, "thirst: " + thirst);
        nvgText(vg, marginLeft + textPadding, marginTop + textPadding * 4, "Size: " + size);
        nvgText(vg, marginLeft + textPadding, marginTop + textPadding * 5, "Energy: " + energy);
        nvgText(vg, marginLeft + textPadding, marginTop + textPadding * 6, "Stamina: " + stamina);
        nvgText(vg, marginLeft + textPadding, marginTop + textPadding * 7, "Speed: " + speed);
        nvgText(vg, marginLeft + textPadding, marginTop + textPadding * 8, "Health : " + health);
        nvgText(vg, marginLeft + textPadding, marginTop + textPadding * 9, "Awareness : " + awareness);




        nvgEndFrame(vg);

        // Restore state
        window.restoreState();
    }


    private NVGColor rgba(int r, int g, int b, int a, NVGColor colour) {
        colour.r(r / 255f);
        colour.g(g / 255f);
        colour.b(b / 255f);
        colour.a(a / 255f);

        return colour;
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

    public float getMaxHunger() {
        return maxHunger;
    }

    public void setMaxHunger(float maxHunger) {
        this.maxHunger = maxHunger;
    }

    public int getThirst() {
        return thirst;
    }

    public void setThirst(int thirst) {
        this.thirst = thirst;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getAwarenessScale() {
        return awarenessScale;
    }

    public void setAwarenessScale(float awarenessScale) {
        this.awarenessScale = awarenessScale;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }
}
