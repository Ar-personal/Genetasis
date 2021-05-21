package engine.terrain;

import engine.entities.BoundingBox;
import engine.entities.TerrainItem;
import engine.entities.StaticGameItem;
import engine.graphics.Mesh;
import org.joml.Vector3f;

public class Terrain {

    private final TerrainItem[] terrainItems;

    private final int terrainSize;

    private final int verticesPerCol;

    private final int verticesPerRow;

    private final HeightMapMesh heightMapMesh;
    private BoundingBox boundingBox;
    private Mesh boundingMesh;

    private float minY;

    private float maxY;

    private float worldOffset = 10f;

    private float topLeftX;
    private float topLeftZ;
    private float boundingWidth;
    private float boundingLength;
    /**
     * It will hold the bounding box for each terrain block
     */
    private final Box2D[][] boundingBoxes;

    public Terrain(int terrainSize, float scale, float minY, float maxY, int textInc) throws Exception {
        this.minY = minY;
        this.maxY = maxY;
        this.terrainSize = terrainSize;
        terrainItems = new TerrainItem[terrainSize * terrainSize];

        int width = 256;
        int height = 256;

        verticesPerCol = width - 1;
        verticesPerRow = height - 1;
        float amplitude = 0.3f;
        float[][] heights = generateHeights(width, new PerlinNoise(6, 0.40f, 0.55f));
        heightMapMesh = new HeightMapMesh(minY, maxY, heights, width, height, textInc, amplitude);
        Mesh mesh = heightMapMesh.getMesh();
        float[] positions = new float[]{

                mesh.getMinX(), mesh.getMaxY(), mesh.getMaxZ(),
                // V1
                mesh.getMinX(), mesh.getMinY(), mesh.getMaxZ(),
                // V2
                mesh.getMaxX(), mesh.getMinY(), mesh.getMaxZ(),
                // V3
                mesh.getMaxX(), mesh.getMaxY(), mesh.getMaxZ(),
                // V4
                mesh.getMinX(), mesh.getMaxY(), mesh.getMinZ(),
                // V5
                mesh.getMaxX(), mesh.getMaxY(), mesh.getMinZ(),
                // V6
                mesh.getMinX(), mesh.getMinY(), mesh.getMinZ(),
                // V7
                mesh.getMaxX(), mesh.getMinY(), mesh.getMinZ(),
        };



        float[] colours = new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
        };

        int[] indices = new int[]{
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                4, 0, 3, 5, 4, 3,
                // Right face
                3, 2, 7, 5, 3, 7,
                // Left face
                6, 1, 0, 6, 0, 4,
                // Bottom face
                2, 1, 6, 2, 6, 7,
                // Back face
                7, 6, 4, 7, 4, 5,
        };

        boundingMesh = new Mesh(positions, null, colours, null, indices);
        boundingBox = new BoundingBox(boundingMesh, new Vector3f(-10, 0, -10));
        boundingBox.setScale(scale);


        boundingBoxes = new Box2D[terrainSize][terrainSize];
        for (int row = 0; row < terrainSize; row++) {
            for (int col = 0; col < terrainSize; col++) {
                float xDisplacement = (col - ((float) terrainSize - 1) / (float) 2) * scale * HeightMapMesh.getXLength();
                float zDisplacement = (row - ((float) terrainSize - 1) / (float) 2) * scale * HeightMapMesh.getZLength();

                TerrainItem terrainBlock = new TerrainItem(heightMapMesh.getMesh(), new Vector3f(xDisplacement, 0, zDisplacement));
                terrainBlock.setScale(scale);
                terrainItems[row * terrainSize + col] = terrainBlock;

                boundingBoxes[row][col] = getBoundingBox(terrainBlock);
            }
        }


    }

    private float[][] generateHeights(int gridSize, PerlinNoise perlinNoise) {
        float heights[][] = new float[gridSize][gridSize];
        for (int z = 0; z < heights.length; z++) {
            for (int x = 0; x < heights[z].length; x++) {
//    //                heights[z][x] = perlinNoise.getPerlinNoise(x, z);
//                float o = perlinNoise.getPerlinNoise(x, z);
//                if(o < 0){
//                heights[z][x] = x - minY / (maxY - minY);
//                }else{
                    heights[z][x] = perlinNoise.getPerlinNoise(x, z);
//                }
//                heights[z][x] = perlinNoise.getPerlinNoise(x, z);

            }
        }

        return heights;
    }

    public float getHeight(Vector3f position) {
        float result = Float.MIN_VALUE;
        // For each terrain block we get the bounding box, translate it to view coodinates
        // and check if the position is contained in that bounding box
        Box2D boundingBox = null;
        boolean found = false;
        StaticGameItem terrainBlock = null;
        for (int row = 0; row < terrainSize && !found; row++) {
            for (int col = 0; col < terrainSize && !found; col++) {
                terrainBlock = terrainItems[row * terrainSize + col];
                boundingBox = boundingBoxes[row][col];
                found = boundingBox.contains(position.x, position.z);
            }
        }

        // If we have found a terrain block that contains the position we need
        // to calculate the height of the terrain on that position
        if (found) {
            Vector3f[] triangle = getTriangle(position, boundingBox, terrainBlock);
            result = interpolateHeight(triangle[0], triangle[1], triangle[2], position.x, position.z);
        }

        return result;
    }

    protected Vector3f[] getTriangle(Vector3f position, Box2D boundingBox, StaticGameItem terrainBlock) {
        // Get the column and row of the heightmap associated to the current position
        float cellWidth = boundingBox.width / (float) verticesPerCol;
        float cellHeight = boundingBox.height / (float) verticesPerRow;
        int col = (int) ((position.x - boundingBox.x) / cellWidth);
        int row = (int) ((position.z - boundingBox.y) / cellHeight);

        Vector3f[] triangle = new Vector3f[3];
        triangle[1] = new Vector3f(
                boundingBox.x + col * cellWidth,
                getWorldHeight(row + 1, col, terrainBlock),
                boundingBox.y + (row + 1) * cellHeight);
        triangle[2] = new Vector3f(
                boundingBox.x + (col + 1) * cellWidth,
                getWorldHeight(row, col + 1, terrainBlock),
                boundingBox.y + row * cellHeight);
        if (position.z < getDiagonalZCoord(triangle[1].x, triangle[1].z, triangle[2].x, triangle[2].z, position.x)) {
            triangle[0] = new Vector3f(
                    boundingBox.x + col * cellWidth,
                    getWorldHeight(row, col, terrainBlock),
                    boundingBox.y + row * cellHeight);
        } else {
            triangle[0] = new Vector3f(
                    boundingBox.x + (col + 1) * cellWidth,
                    getWorldHeight(row + 2, col + 1, terrainBlock),
                    boundingBox.y + (row + 1) * cellHeight);
        }

        return triangle;
    }

    protected float getDiagonalZCoord(float x1, float z1, float x2, float z2, float x) {
        float z = ((z1 - z2) / (x1 - x2)) * (x - x1) + z1;
        return z;
    }

    protected float getWorldHeight(int row, int col, StaticGameItem staticGameItem) {
        float y = heightMapMesh.getHeight(row, col);
        return y * staticGameItem.getScale() + staticGameItem.getPosition().y;
    }

    protected float interpolateHeight(Vector3f pA, Vector3f pB, Vector3f pC, float x, float z) {
        // Plane equation ax+by+cz+d=0
        float a = (pB.y - pA.y) * (pC.z - pA.z) - (pC.y - pA.y) * (pB.z - pA.z);
        float b = (pB.z - pA.z) * (pC.x - pA.x) - (pC.z - pA.z) * (pB.x - pA.x);
        float c = (pB.x - pA.x) * (pC.y - pA.y) - (pC.x - pA.x) * (pB.y - pA.y);
        float d = -(a * pA.x + b * pA.y + c * pA.z);
        // y = (-d -ax -cz) / b
        float y = (-d - a * x - c * z) / b;
        return y;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    /**
     * Gets the bounding box of a terrain block
     *
     * @param terrainBlock A StaticGameItem instance that defines the terrain block
     * @return The boundingg box of the terrain block
     */
    private Box2D getBoundingBox(TerrainItem terrainBlock) {
        float scale = terrainBlock.getScale();
        Vector3f position = terrainBlock.getPosition();

        topLeftX = (HeightMapMesh.STARTX * scale + position.x) - worldOffset;
        topLeftZ = (HeightMapMesh.STARTZ * scale + position.z) - worldOffset;
        boundingWidth = Math.abs(HeightMapMesh.STARTX * 8) * scale;
        boundingLength = Math.abs(HeightMapMesh.STARTZ * 8) * scale;
        Box2D boundingBox = new Box2D(topLeftX, topLeftZ, boundingWidth, boundingLength);
        return boundingBox;
    }

    public Box2D[][] getBoundingBoxes() {
        return boundingBoxes;
    }

    public float getTopLeftX() {
        return topLeftX;
    }

    public void setTopLeftX(float topLeftX) {
        this.topLeftX = topLeftX;
    }

    public float getTopLeftZ() {
        return topLeftZ;
    }

    public void setTopLeftZ(float topLeftZ) {
        this.topLeftZ = topLeftZ;
    }

    public float getBoundingWidth() {
        return boundingWidth;
    }

    public void setBoundingWidth(float boundingWidth) {
        this.boundingWidth = boundingWidth;
    }

    public float getBoundingLength() {
        return boundingLength;
    }

    public void setBoundingLength(float boundingLength) {
        this.boundingLength = boundingLength;
    }

    public TerrainItem[] getGameItems() {
        return terrainItems;
    }

    static class Box2D {

        public float x;

        public float y;

        public float width;

        public float height;

        //set size of 2d box
        public Box2D(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public boolean contains(float x2, float y2) {
            return x2 >= x
                    && y2 >= y
                    && x2 < x + width
                    && y2 < y + height;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }
}
