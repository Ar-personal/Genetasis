package main;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.newdawn.slick.opengl.TextureLoader;
import org.w3c.dom.Text;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    private int id;
    private String path;
    private float width, height;

    private org.newdawn.slick.opengl.Texture texture;

    public Texture(String path, int id) {
        this.path = path;
        this.id = id;
    }

    public Texture(int id) {
        this.id = id;
    }

    public Texture(org.newdawn.slick.opengl.Texture texture, int id){
        this.texture = texture;
        this.id = id;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public int getId() {
        return id;
    }

    private static int loadTexture(String fileName) throws Exception {
        int width;
        int height;
        ByteBuffer buf;
        // Load Texture file
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            buf = stbi_load(fileName, w, h, channels, 4);
            if (buf == null) {
                throw new Exception("Image file [" + fileName  + "] not loaded: " + stbi_failure_reason());
            }

            /* Get width and height of image */
            width = w.get();
            height = h.get();
        }

        // Create a new OpenGL texture
        int textureId = glGenTextures();
        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte size
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Upload the texture data
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, buf);
        // Generate Mip Map
        glGenerateMipmap(GL_TEXTURE_2D);

        stbi_image_free(buf);

        return textureId;
    }

    public void create() {
        try {
            texture = TextureLoader.getTexture(path.split("[.]")[1], Texture.class.getResourceAsStream(path), GL11.GL_NEAREST);
            width = texture.getWidth();
            height = texture.getHeight();
            id = texture.getTextureID();
        } catch (IOException e) {
            System.err.println("Can't find texture at " + path);
        }
    }

    public void cleanup() {
        glDeleteTextures(id);
    }
}