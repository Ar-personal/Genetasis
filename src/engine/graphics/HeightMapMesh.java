package engine.graphics;

import de.matthiasmann.twl.utils.PNGDecoder;
import engine.utils.Maths;
import engine.utils.Utils;
import main.Texture;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.CallbackI;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.stb.STBImage.*;
import static org.lwjglx.opengl.Display.getWidth;

public class HeightMapMesh {

    private static final int MAX_COLOUR = 255 * 255 * 255;

    public static float STARTX = -0.5f;

    public static float STARTZ = -0.5f;

    private final float minY;

    private final float maxY;

//    private final float part;

    private Mesh mesh;

    private Vector3f[] biomeColours;

    private final float[][] heightArray;
    private List heightList;

    private List<Vector3f> colours;

    private float amplitude;

//    public HeightMapMesh(float minY, float maxY, ByteBuffer heightMapImage, int width, int height, String textureFile, int textInc) throws Exception {
//        this.minY = minY;
//        this.maxY = maxY;
//
//        heightArray = new float[height][width];
//        heightList = new float[height * width];
//
//        Texture texture = new Texture(textureFile);
//
//        float incx = getXLength() / (width - 1);
//        float incz = getZLength() / (height - 1);
//
//        List<Float> positions = new ArrayList<>();
//        List<Float> textCoords = new ArrayList<>();
//        List<Integer> indices = new ArrayList<>();
//
//        for (int row = 0; row < height; row++) {
//            for (int col = 0; col < width; col++) {
//                // Create vertex for current position
//                positions.add(STARTX + col * incx); // x
//                float currentHeight = getHeight(col, row, width, heightMapImage);
//                heightArray[row][col] = currentHeight;
//                positions.add(currentHeight); //y
//                positions.add(STARTZ + row * incz); //z
//
//                // Set texture coordinates
//                textCoords.add((float) textInc * (float) col / (float) width);
//                textCoords.add((float) textInc * (float) row / (float) height);
//
//                // Create indices
//                if (col < width - 1 && row < height - 1) {
//                    int leftTop = row * width + col;
//                    int leftBottom = (row + 1) * width + col;
//                    int rightBottom = (row + 1) * width + col + 1;
//                    int rightTop = row * width + col + 1;
//
//                    indices.add(rightTop);
//                    indices.add(leftBottom);
//                    indices.add(rightBottom);
//
//                    indices.add(leftTop);
//                    indices.add(leftBottom);
//                    indices.add(rightTop);
//                }
//            }
//        }
//        float[] posArr = Utils.listToArray(positions);
//        int[] indicesArr = indices.stream().mapToInt(i -> i).toArray();
//        float[] textCoordsArr = Utils.listToArray(textCoords);
//        float[] normalsArr = calcNormals(posArr, width, height);
//        this.mesh = new Mesh(posArr, textCoordsArr, null, normalsArr, indicesArr, true);
//        Material material = new Material(texture, 0.0f);
//        mesh.setMaterial(material);
//    }

    public HeightMapMesh(float minY, float maxY, float[][] heights, int width, int height, int textInc, float amplitude) throws Exception {
        this.heightArray = heights;
        this.minY = minY;
        this.maxY = maxY;
        this.amplitude = amplitude;

        float incx = getXLength() / (width - 1);
        float incz = getZLength() / (height - 1);

        List positions = new ArrayList();
        Vector3f[][] positionsVec = new Vector3f[height][width];
        List<Float> texCoords = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                // Create vertex for current position
                positions.add(STARTX + col * incx); // x
                float currentHeight = heights[row][col];
                positions.add(currentHeight); //y
                positions.add(STARTZ + row * incz); //z
                positionsVec[row][col] = new Vector3f(STARTX + col * incx, currentHeight, STARTZ + row * incz);


                texCoords.add((float) textInc * (float) col / (float) width);
                texCoords.add((float) textInc * (float) row / (float) height);


                // Create indices
                if (col < width - 1 && row < height - 1) {
                    int leftTop = row * width + col;
                    int leftBottom = (row + 1) * width + col;
                    int rightBottom = (row + 1) * width + col + 1;
                    int rightTop = row * width + col + 1;

                    indices.add(rightTop);
                    indices.add(leftBottom);
                    indices.add(rightBottom);

                    indices.add(leftTop);
                    indices.add(leftBottom);
                    indices.add(rightTop);
                }
            }
        }


        float[] posArr = Utils.listToArray(positions);
        int[] indicesArr = indices.stream().mapToInt(i -> i).toArray();
        float[] texCoordsArr = Utils.listToArray(texCoords);
        float[] normalsArr = calcNormals(posArr, width, height);

        biomeColours = new Vector3f[]{new Vector3f((float) 201 / 255, (float) 178 / 255, (float) 99 / 255), new Vector3f((float) 135 /255, (float) 184 / 255, (float) 82 / 255), new Vector3f( (float) 80 / 255, (float) 171 /255, (float) 93 / 255),
                new Vector3f((float) 120 / 255, (float) 120 / 255, (float) 120 / 255), new Vector3f((float) 200 / 256, (float) 200 / 255, (float)210 / 255), new Vector3f((float)177 / 255, (float)214 /255, (float)224 / 255)};

//        float amplitude = 10f;

        colours = new ArrayList<>();
        for(int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                float h = positionsVec[i][j].y;
                float spread = 0.6f;
                float halfSpread = spread / 2;
                float value = (h + amplitude) / (amplitude * 2);
                value = Maths.clamp((value - halfSpread) * (1f / spread), 0f, 0.9999f);
                float part = 1f / (biomeColours.length -1);
                int firstBiome = (int) Math.floor(value / part);
                float blend = (value - (firstBiome * part)) / part;
//                float blend = 1f - h;
//                float blend = amplitude * positionsVec[i][j].y;
                    colours.add(Colour.interpolateColours(biomeColours[firstBiome], biomeColours[firstBiome + 1], blend, null));
//                if (positionsVec[i][j].y > 0.1f) {
////                    colours.add(Colour.interpolateColours(biomeColours[5], biomeColours[4], blend, null));
//                    colours.add(biomeColours[3]);
//                }
//
//                if (positionsVec[i][j].y < 0.1f) {
////                    colours.add(Colour.interpolateColours(biomeColours[5], biomeColours[4], blend, null));
//                    colours.add(biomeColours[0]);
//                }

//                if (positionsVec[i][j].y < -0.06f) {
//                    colours.add(biomeColours[0]);
//                }
//
//                if (positionsVec[i][j].y > -0.06f && positionsVec[i][j].y < -0.03f) {
//
//                    colours.add(Colour.interpolateColours(biomeColours[1], biomeColours[2], blend, null));
//                }
//
//                if (positionsVec[i][j].y > -0.03f && positionsVec[i][j].y < 0.02) {
//
//                    colours.add(Colour.interpolateColours(biomeColours[2], biomeColours[3], blend, null));
//                }
//                Random rand = new Random();
//                if (positionsVec[i][j].y > 0.02f && positionsVec[i][j].y < 0.07) {
//                    colours.add(biomeColours[rand.nextInt(biomeColours.length -1)]);
////                    colours.add(Colour.interpolateColours(biomeColours[3], biomeColours[4], blend, null));
//                }
//
//                if (positionsVec[i][j].y > 0.07f && positionsVec[i][j].y < 0.1) {
//
//                    colours.add(Colour.interpolateColours(biomeColours[4], biomeColours[5], blend, null));
//                }
//
//                if (positionsVec[i][j].y > 0.1f) {
//                    colours.add(biomeColours[6]);
//                }

//
//                if (positionsVec[i][j].y > 0.7f) {
////                    colours.add(Colour.interpolateColours(biomeColours[5], biomeColours[4], blend, null));
//                    colours.add(biomeColours[5]);
//                }else
//
//                if (positionsVec[i][j].y > 0.06f && positionsVec[i][j].y < 0.07f) {
////                    colours.add(Colour.interpolateColours(biomeColours[4], biomeColours[3], blend, null));
//                    colours.add(biomeColours[4]);
//                }else
//
//                if (positionsVec[i][j].y > 0.03f && positionsVec[i][j].y < 0.04f) {
////                    colours.add(Colour.interpolateColours(biomeColours[3], biomeColours[2], blend, null));
//                    colours.add(biomeColours[3]);
//                }else
//
//                if (positionsVec[i][j].y > 0.02f && positionsVec[i][j].y < 0.03f) {
////                  colours.add(Colour.interpolateColours(biomeColours[2], biomeColours[1], blend, null));
//                    colours.add(biomeColours[2]);
//                }else
//
//                if (positionsVec[i][j].y > 0.01f && positionsVec[i][j].y < 0.02f) {
////                    colours.add(Colour.interpolateColours(biomeColours[1], biomeColours[0], blend, null));
//                    colours.add(biomeColours[1]);
//                }


            }
        }

        float[] floatColour = new float[(width * height) * 3];
        int index = 0;
        for (int o = 0; o < colours.size(); o++){
            floatColour[index] = colours.get(o).x;
            floatColour[index + 1] = colours.get(o).y;
            floatColour[index + 2] = colours.get(o).z;
            if(index + 3 >= floatColour.length){
                continue;
            }else {
                index += 3;
            }
        }

        System.out.println("indices length: " + indicesArr.length + " colors length: " + floatColour.length + " heightlist length: " + " positions length: " + posArr.length + " height array length: ");


        this.mesh = new Mesh(posArr, texCoordsArr, floatColour, normalsArr, indicesArr);
        this.mesh.setMaterial(null);
    }

    //textureless terrain with heightmap file
    public HeightMapMesh(float minY, float maxY, ByteBuffer heightMapImage, int width, int height, int textInc) throws Exception {
        this.minY = minY;
        this.maxY = maxY;


        heightArray = new float[height][width];
        System.out.println("HeightMapMesh width: " + width + " heightMapMesh height: " + height);
        System.out.println("Total Size: " + width * height);

        float incx = getXLength() / (width - 1);
        float incz = getZLength() / (height - 1);

        List positions = new ArrayList();
        Vector3f[][] positionsVec = new Vector3f[height][width];
        List<Float> texCoords = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        heightList = new ArrayList();


        int Idx = 0;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                // Create vertex for current position
                positions.add(STARTX + col * incx); // x
                float currentHeight = getHeight(col, row, width, heightMapImage);
                heightArray[row][col] = currentHeight;
                heightList.add(currentHeight);
                positions.add(currentHeight); //y
                positions.add(STARTZ + row * incz); //z
                positionsVec[row][col] = new Vector3f(STARTX + col * incx, currentHeight, STARTZ + row * incz);

                Idx++;
                // Set texture coordinates
                texCoords.add((float) textInc * (float) col / (float) width);
                texCoords.add((float) textInc * (float) row / (float) height);

                // Create indices
                if (col < width - 1 && row < height - 1) {
                    int leftTop = row * width + col;
                    int leftBottom = (row + 1) * width + col;
                    int rightBottom = (row + 1) * width + col + 1;
                    int rightTop = row * width + col + 1;

                    indices.add(rightTop);
                    indices.add(leftBottom);
                    indices.add(rightBottom);

                    indices.add(leftTop);
                    indices.add(leftBottom);
                    indices.add(rightTop);
                }
            }
        }

        float[] posArr = Utils.listToArray(positions);
        int[] indicesArr = indices.stream().mapToInt(i -> i).toArray();
        float[] texCoordsArr = Utils.listToArray(texCoords);
        float[] normalsArr = calcNormals(posArr, width, height);

        biomeColours = new Vector3f[]{new Vector3f((float) 201 / 255, (float) 178 / 255, (float) 99 / 255), new Vector3f((float) 135 /255, (float) 184 / 255, (float) 82 / 255), new Vector3f( (float) 80 / 255, (float) 171 /255, (float) 93 / 255),
                new Vector3f((float) 120 / 255, (float) 120 / 255, (float) 120 / 255), new Vector3f((float) 200 / 256, (float) 200 / 255, (float)210 / 255), new Vector3f(0, 0, 0)};

//        float amplitude = 10f;
//        float spread = 0.45f;
//        float halfSpread = spread / 2;
//        float value = (height + amplitude) / (amplitude * 2);
//        value = Maths.clamp((value - halfSpread) * (1f / spread), 0f, 0.9999f);
//        int firstBiome = (int) Math.floor(value / part);
//        float blend = (value - (firstBiome * part)) / part;
        colours = new ArrayList<>();
        for(int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                //positions vec not bigger than 65536  but why does it need to be when positions is 3 times that with x y and z coords
                //there should be as many colours as there are positions, positions length is 196608 and so is colours length, but that means for positions x, y, z there is and r g b,
                //so do colours need to be one third of positions? but then items in float buffer are mismatched?? fully coloured map with texture errors when i changed positions to be arrays and to take
                // the y value directly
                float h = positionsVec[i][j].y;
                float hscaled = (float) (h*2.0 - 1e-05); // hscaled should range in [0,2)
                int hi = (int) hscaled;
                float blend =  1 + h;

                if (positionsVec[i][j].y < -0.12f && positionsVec[i][j].y > -0.14f) {
                    blend = positionsVec[i][j].y / -0.14f;
                    colours.add(Colour.interpolateColours(biomeColours[4], biomeColours[3], blend, null));
                }

                if (positionsVec[i][j].y < -0.1f && positionsVec[i][j].y > -0.12f) {
                    blend = positionsVec[i][j].y / -0.14f;


                    colours.add(Colour.interpolateColours(biomeColours[5], biomeColours[4], blend, null));
                }

                if (positionsVec[i][j].y < -0.14f && positionsVec[i][j].y > -0.19f) {
                    blend = positionsVec[i][j].y / -0.19f;
                    colours.add(Colour.interpolateColours(biomeColours[3], biomeColours[2], blend, null));
                }

                if (positionsVec[i][j].y < -0.19f && positionsVec[i][j].y > -0.32f) {
                    blend = positionsVec[i][j].y / -0.22f;
                    colours.add(Colour.interpolateColours(biomeColours[2], biomeColours[1], blend, null));
                }

                if (positionsVec[i][j].y < -0.32f && positionsVec[i][j].y > -0.50f) {
                    blend = positionsVec[i][j].y / -0.30f;
                    colours.add(Colour.interpolateColours(biomeColours[1], biomeColours[0], blend, null));
                }

                if(positionsVec[i][j].y < -0.40f){
                    colours.add(biomeColours[0]);
                }

            }
//            colours[i] = calculateColour((float) heightList.get(i), amplitude, spread);

        }

        float[] floatColour = new float[(width * height) * 3];
        int index = 0;
        for (int o = 0; o < colours.size(); o++){
            floatColour[index] = colours.get(o).x;
            floatColour[index + 1] = colours.get(o).y;
            floatColour[index + 2] = colours.get(o).z;
            if(index + 3 >= floatColour.length){
                continue;
            }else {
                index += 3;
            }
        }

        //add array of floats to mesh class by converting an array of vec3
//        int i = 0;
//        for(int o = 0; o < floatColours.length; o += 3) {
//
//                floatColours[o] = colours[i].x;
//                floatColours[o + 1] = colours[i].y;
//                floatColours[o + 2] = colours[i].z;
//
//                if (i + 1 >= colours.length) {
//                    i = 0;
//                }
//                i++;
//            }

        System.out.println("indices length: " + indicesArr.length + " colors length: " + floatColour.length + " heightlist length: " + heightList.size() + " positions length: " + posArr.length + " height array length: ");
        this.mesh = new Mesh(posArr, null, floatColour, normalsArr, indicesArr);
        mesh.setMaterial(new Material());
    }

    public Mesh getMesh() {
        return mesh;
    }

    public float getHeight(int row, int col) {
        float result = 0;
        if ( row >= 0 && row < heightArray.length ) {
            if ( col >= 0 && col < heightArray[row].length ) {
                result = heightArray[row][col];
            }
        }
        return result;
    }

    public static float getXLength() {
        return Math.abs(-STARTX*8);
    }

    public static float getZLength() {
        return Math.abs(-STARTZ*8);
    }

    private float[] calcNormals(float[] posArr, int width, int height) {
        Vector3f v0 = new Vector3f();
        Vector3f v1 = new Vector3f();
        Vector3f v2 = new Vector3f();
        Vector3f v3 = new Vector3f();
        Vector3f v4 = new Vector3f();
        Vector3f v12 = new Vector3f();
        Vector3f v23 = new Vector3f();
        Vector3f v34 = new Vector3f();
        Vector3f v41 = new Vector3f();
        List<Float> normals = new ArrayList<>();
        Vector3f normal = new Vector3f();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (row > 0 && row < height -1 && col > 0 && col < width -1) {
                    int i0 = row*width*3 + col*3;
                    v0.x = posArr[i0];
                    v0.y = posArr[i0 + 1];
                    v0.z = posArr[i0 + 2];

                    int i1 = row*width*3 + (col-1)*3;
                    v1.x = posArr[i1];
                    v1.y = posArr[i1 + 1];
                    v1.z = posArr[i1 + 2];
                    v1 = v1.sub(v0);

                    int i2 = (row+1)*width*3 + col*3;
                    v2.x = posArr[i2];
                    v2.y = posArr[i2 + 1];
                    v2.z = posArr[i2 + 2];
                    v2 = v2.sub(v0);

                    int i3 = (row)*width*3 + (col+1)*3;
                    v3.x = posArr[i3];
                    v3.y = posArr[i3 + 1];
                    v3.z = posArr[i3 + 2];
                    v3 = v3.sub(v0);

                    int i4 = (row-1)*width*3 + col*3;
                    v4.x = posArr[i4];
                    v4.y = posArr[i4 + 1];
                    v4.z = posArr[i4 + 2];
                    v4 = v4.sub(v0);

                    v1.cross(v2, v12);
                    v12.normalize();

                    v2.cross(v3, v23);
                    v23.normalize();

                    v3.cross(v4, v34);
                    v34.normalize();

                    v4.cross(v1, v41);
                    v41.normalize();

                    normal = v12.add(v23).add(v34).add(v41);
                    normal.normalize();
                } else {
                    normal.x = 0;
                    normal.y = 1;
                    normal.z = 0;
                }
                normal.normalize();
                normals.add(normal.x);
                normals.add(normal.y);
                normals.add(normal.z);
            }
        }
        return Utils.listToArray(normals);
    }

    private float getHeight(int x, int z, int width, ByteBuffer buffer) {
        byte r = buffer.get(x * 4 + 0 + z * 4 * width);
        byte g = buffer.get(x * 4 + 1 + z * 4 * width);
        byte b = buffer.get(x * 4 + 2 + z * 4 * width);
        byte a = buffer.get(x * 4 + 3 + z * 4 * width);
        int argb = ((0xFF & a) << 24) | ((0xFF & r) << 16)
                | ((0xFF & g) << 8) | (0xFF & b);
        return this.minY + Math.abs(this.maxY - this.minY) * ((float) argb / (float) MAX_COLOUR);
    }

//    private Vector3f calculateColour(float height, float amplitude, float spread) {
//        float halfSpread = spread / 2;
//        float value = (height + amplitude) / (amplitude * 2);
//        value = Maths.clamp((value - halfSpread) * (1f / spread), -0.1f, 0.1f);
//        int firstBiome = (int) Math.floor(value / part);
//        float blend = (value - (firstBiome * part)) / part;
//        return Colour.interpolateColours(biomeColours[firstBiome], biomeColours[firstBiome + 1], blend, null);
//    }

}
