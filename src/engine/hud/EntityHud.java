package engine.hud;

import engine.entities.GameItem;
import engine.graphics.Window;
import engine.utils.Utils;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryUtil;

import static java.sql.Types.NULL;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVG.nvgFill;
import static org.lwjgl.nanovg.NanoVGGL2.*;

public class EntityHud extends Hud {

    protected String gameItemType;

    protected float awareness = 1f;
    protected float hunger = 20f;
    protected float maxHunger;
    protected int thirst = 0, generation;
    protected float size;
    protected float speed, maxSpeed;
    protected float awarenessScale;
    protected float maxSize;
    protected int health = 0;
    protected int stamina;
    protected float energy, maxEnergy;
    protected float marginLeft = 100f, marginTop = 200f, textPadding = 28f, windowHeight = 350f, windowWidth, barHeight = textPadding - 5f, barY = marginTop + textPadding + 10f;
    protected boolean male;


    public void init(Window window) throws Exception {
        this.window = window;
        this.vg = window.getOpts().antialiasing ? nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES) : nvgCreate(NVG_STENCIL_STROKES);
        if (this.vg == NULL) {
            throw new Exception("Could not init nanovg");
        }

        fontBuffer = Utils.ioResourceToByteBuffer("/fonts/OpenSans-Bold.ttf", 150 * 1024);
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
        float f1 = size / maxSize * 100;
//        float sizePercentage =  (1f - f1) * 100f;


        nvgBeginPath(vg);
        nvgRect(vg, marginLeft, marginTop, windowWidth, windowHeight);
        nvgFill(vg);
        nvgFillColor(vg, rgba(83, 83, 83, 150, colour));

        //text colour
        nvgBeginPath(vg);
        nvgFillColor(vg, rgba(22, 25, 30, 155, colour));

        nvgFontSize(vg, 26f);
        nvgText(vg, marginLeft + textPadding, marginTop + textPadding, "Entity Type: " + gameItemType);

        //red hunger bar
        nvgBeginPath(vg);
        nvgFillColor(vg, rgba(255, 43, 28, 150, colour));
        nvgRect(vg, marginLeft + textPadding, marginTop + (textPadding + 10f), windowWidth - (textPadding * 2), barHeight);
        nvgFill(vg);

        //green hunger bar
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
        nvgText(vg, marginLeft + textPadding, marginTop + textPadding * 4, "Growth: " + String.format("%.2f", f1) + "%");

        //red energy bar
        nvgBeginPath(vg);
        nvgFillColor(vg, rgba(255, 43, 28, 150, colour));
        nvgRect(vg, marginLeft + textPadding, marginTop + (textPadding + 93f), windowWidth - (textPadding * 2), barHeight);
        nvgFill(vg);

        //green energy bar
        nvgBeginPath(vg);
        nvgFillColor(vg, rgba(245, 215, 66, 150, colour));
        nvgRect(vg, marginLeft + textPadding, marginTop + (textPadding + 93f), energy, barHeight);
        nvgFill(vg);

        nvgBeginPath(vg);
        nvgFillColor(vg, rgba(22, 25, 30, 155, colour));

        //overlay
        nvgText(vg, marginLeft + textPadding, marginTop + textPadding * 5, "Energy: " + String.format("%.2f", energy) + " / " + String.format("%.2f", maxEnergy));



        nvgText(vg, marginLeft + textPadding, marginTop + textPadding * 6, "Stamina: " + stamina);
        nvgText(vg, marginLeft + textPadding, marginTop + textPadding * 7, "Max Speed: " + String.format("%.3f", maxSpeed));
        nvgText(vg, marginLeft + textPadding, marginTop + textPadding * 8, "Health : " + health);
        nvgText(vg, marginLeft + textPadding, marginTop + textPadding * 9, "Awareness : " + awareness);
        String sex = "";
        if(male){
            sex = "male";
        }else{
            sex = "female";
        }
        nvgText(vg, marginLeft + textPadding, marginTop + textPadding * 10, "Sex : " + sex);
        nvgText(vg, marginLeft + textPadding, marginTop + textPadding * 11, "Size : " + String.format("%.6f", size));
        nvgText(vg, marginLeft + textPadding, marginTop + textPadding * 12, "Generation : " + generation);


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

    public float getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(float maxSize) {
        this.maxSize = maxSize;
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

    public boolean isMale() {
        return male;
    }

    public void setMale(boolean male) {
        this.male = male;
    }


    public void setGameItemType(GameItem gameItemType) {
        this.gameItemType = gameItemType.getClass().getSimpleName();
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
}
