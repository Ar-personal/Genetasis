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
import engine.utils.Utils;
import org.joml.Vector4f;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.sql.Types.NULL;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL2.*;

public class Hud {

    protected static final String FONT_NAME = "BOLD";

    protected long vg;

    protected NVGColor colour;

    protected ByteBuffer fontBuffer;

    protected final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    protected DoubleBuffer posx;

    protected DoubleBuffer posy;

    protected int counter;

    protected List<EntityHud> entityHuds = new ArrayList<>();

    protected Window window;

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

        counter = 0;
    }

    public void render(Window window) {
        this.window = window;
        nvgBeginFrame(vg, window.getWidth(), window.getHeight(), 1);


        // Upper ribbon
        nvgBeginPath(vg);
        nvgRect(vg, 0, window.getHeight() - 100, window.getWidth(), 50);
        nvgFillColor(vg, rgba(0x23, 0xa1, 0xf1, 200, colour));
        nvgFill(vg);

        // Lower ribbon
        nvgBeginPath(vg);
        nvgRect(vg, 0, window.getHeight() - 50, window.getWidth(), 10);
        nvgFillColor(vg, rgba(0xc1, 0xe3, 0xf9, 200, colour));
        nvgFill(vg);

        glfwGetCursorPos(window.getWindowHandle(), posx, posy);
        int xcenter = 50;
        int ycenter = window.getHeight() - 75;
        int radius = 20;
        int x = (int) posx.get(0);
        int y = (int) posy.get(0);
        boolean hover = Math.pow(x - xcenter, 2) + Math.pow(y - ycenter, 2) < Math.pow(radius, 2);

        // Circle
        nvgBeginPath(vg);
        nvgCircle(vg, xcenter, ycenter, radius);
        nvgFillColor(vg, rgba(0xc1, 0xe3, 0xf9, 200, colour));
        nvgFill(vg);

        // Clicks Text
        nvgFontSize(vg, 25.0f);
        nvgFontFace(vg, FONT_NAME);
        nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_TOP);
        if (hover) {
            nvgFillColor(vg, rgba(0x00, 0x00, 0x00, 255, colour));
        } else {
            nvgFillColor(vg, rgba(0x23, 0xa1, 0xf1, 255, colour));

        }
        nvgText(vg, 50, window.getHeight() - 87, String.format("%02d", counter));

        // Render hour text
        nvgFontSize(vg, 40.0f);
        nvgFontFace(vg, FONT_NAME);
        nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        nvgFillColor(vg, rgba(0xe6, 0xea, 0xed, 255, colour));
        nvgText(vg, window.getWidth() - 150, window.getHeight() - 95, dateFormat.format(new Date()));

        nvgEndFrame(vg);

        // Restore state
        window.restoreState();
    }

    public void incCounter() {
        counter++;
        if (counter > 99) {
            counter = 0;
        }
    }

    private NVGColor rgba(int r, int g, int b, int a, NVGColor colour) {
        colour.r(r / 255.0f);
        colour.g(g / 255.0f);
        colour.b(b / 255.0f);
        colour.a(a / 255.0f);

        return colour;
    }

    public void cleanup() {
        nvgDelete(vg);
        if (posx != null) {
            MemoryUtil.memFree(posx);
        }
        if (posy != null) {
            MemoryUtil.memFree(posy);
        }
    }


}