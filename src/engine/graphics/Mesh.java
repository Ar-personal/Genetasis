package engine.graphics;

import engine.entities.GameItem;
import main.Texture;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {

    private final int vaoId;

    private final List<Integer> vboIdList;

    private int colourVboId;

    private final int vertexCount;

    private Material material;

    private Texture texture;

    private float width, height, length, minX, minY, minZ, maxX, maxY, maxZ;

    public Mesh(float[] positions, float[] textCoords, float[] colours, float[] normals, int[] indices, boolean wireFrame) {
        FloatBuffer posBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        FloatBuffer colourBuffer = null;
        FloatBuffer vecNormalsBuffer = null;
        IntBuffer indicesBuffer = null;
        try {
            vertexCount = indices.length;
            vboIdList = new ArrayList<>();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            // Position VBO
            int vboId = glGenBuffers();
            vboIdList.add(vboId);
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
//            if(wireFrame) {
//                glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
//            }
//
//            if(!wireFrame){
//                glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
//            }
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);


                // Texture coordinates VBO
            if(textCoords != null) {
                vboId = glGenBuffers();
                vboIdList.add(vboId);
                textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
                textCoordsBuffer.put(textCoords).flip();
                glBindBuffer(GL_ARRAY_BUFFER, vboId);
                glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
                glEnableVertexAttribArray(1);
                glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
            }


            // Vertex normals VBO
            if(normals != null) {
                vboId = glGenBuffers();
                vboIdList.add(vboId);
                vecNormalsBuffer = MemoryUtil.memAllocFloat(normals.length);
                if (vecNormalsBuffer.capacity() > 0) {
                    vecNormalsBuffer.put(normals).flip();
                } else {
                    // Create empty structure
                    vecNormalsBuffer = MemoryUtil.memAllocFloat(positions.length);
                }
                glBindBuffer(GL_ARRAY_BUFFER, vboId);
                glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL_STATIC_DRAW);
                glEnableVertexAttribArray(2);
                glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
            }

            //colours
            if(colours != null) {
                colourVboId = glGenBuffers();
                vboId = glGenBuffers();
                vboIdList.add(vboId);
                colourBuffer = MemoryUtil.memAllocFloat(colours.length);
                colourBuffer.put(colours).flip();
                glBindBuffer(GL_ARRAY_BUFFER, colourVboId);
                glBufferData(GL_ARRAY_BUFFER, colourBuffer, GL_STATIC_DRAW);
                glEnableVertexAttribArray(3);
                glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0);
            }

            // Index VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if (posBuffer != null) {
                MemoryUtil.memFree(posBuffer);
            }
            if (textCoordsBuffer != null) {
                MemoryUtil.memFree(textCoordsBuffer);
            }
            if (vecNormalsBuffer != null) {
                MemoryUtil.memFree(vecNormalsBuffer);
            }
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
            if (colourBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }

        findDimensions(positions);
    }

    public void findDimensions(float[] dimensions){
        minX = dimensions[0]; minY = dimensions[1]; minZ = dimensions[2]; maxX = dimensions[0]; maxY = dimensions[1]; maxZ = dimensions[2];

        for(int i = 0; i < dimensions.length - 3; i+=3){
            if(dimensions[i] < minX){
                minX = dimensions[i];
            }
            if(dimensions[i + 1] < minY){
                minY = dimensions[i + 1];
            }
            if(dimensions[i + 2] < minZ){
                minZ = dimensions[i + 2];
            }
            if(dimensions[i] > maxX){
                maxX = dimensions[i];
            }
            if(dimensions[i + 1] > maxY){
                maxY = dimensions[i + 1];
            }
            if(dimensions[i + 2] > maxZ){
                maxZ = dimensions[i + 2];
            }

        }

        width = maxX - minX;
        height = maxY - minY;
        length = maxZ - minZ;

    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    private void initRender() {
        if(material != null) {
            texture = material.getTexture();
        }
        if (texture != null) {
            // Activate firs texture bank
            glActiveTexture(GL_TEXTURE0);
            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, texture.getId());
        }

        // Draw the mesh
        glBindVertexArray(getVaoId());
    }

    private void endRender() {
        // Restore state
        glBindVertexArray(0);

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void render() {
        initRender();

        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
//        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        endRender();
    }

    public void renderList(List<GameItem> gameItems, Consumer<GameItem> consumer) {
        initRender();

        for (GameItem staticGameItem : gameItems) {
            // Set up data required by StaticGameItem
            consumer.accept(staticGameItem);
            // Render this game item
            glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
        }

        endRender();
    }

    public void cleanUp() {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList) {
            glDeleteBuffers(vboId);
        }

        // Delete the texture
        Texture texture = material.getTexture();
        if (texture != null) {
            texture.cleanup();
        }

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

    public void deleteBuffers() {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList) {
            glDeleteBuffers(vboId);
        }

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getMinX() {
        return minX;
    }

    public void setMinX(float minX) {
        this.minX = minX;
    }

    public float getMinY() {
        return minY;
    }

    public void setMinY(float minY) {
        this.minY = minY;
    }

    public float getMinZ() {
        return minZ;
    }

    public void setMinZ(float minZ) {
        this.minZ = minZ;
    }

    public float getMaxX() {
        return maxX;
    }

    public void setMaxX(float maxX) {
        this.maxX = maxX;
    }

    public float getMaxY() {
        return maxY;
    }

    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }

    public float getMaxZ() {
        return maxZ;
    }

    public void setMaxZ(float maxZ) {
        this.maxZ = maxZ;
    }
}


