package engine.graphics;

import engine.utils.Maths;

import java.util.ArrayList;
import java.util.List;

public class ColourGenerator {

    private final float spread;
    private final float halfSpread;

    private Colour[] biomeColours;
    private Colour[][] biomeColours2D;
    private final float part;
    private List list;

    public ColourGenerator(Colour[] biomeColours, float spread) {
        this.biomeColours = biomeColours;
        this.spread = spread;
        this.halfSpread = spread / 2f;
        this.part = 1f / (biomeColours.length - 1);
    }

    public ColourGenerator(Colour[][] biomeColours2D, float spread) {
        this.biomeColours2D = biomeColours2D;
        this.spread = spread;
        this.halfSpread = spread / 2f;

        list = new ArrayList();
        for (int i = 0; i < biomeColours2D.length; i++){
            for (int j = 0; j < biomeColours2D[i].length; j++){
                list.add(biomeColours2D[i][j]);
            }
        }

        this.part = 1f / (biomeColours2D.length - 1);
    }

    /**
     * Calculates the colour for every vertex of the terrain, by linearly
     * interpolating between the biome colours depending on the vertex's height.
     *
     * @param heights
     *            -The heights of all the vertices in the terrain.
     * @param amplitude
     *            - The amplitude range of the terrain that was used in the
     *            heights generation. Maximum possible height is
     *            {@code altitude} and minimum possible is {@code -altitude}.
     * @return The colours of all the vertices in the terrain, in a grid.
     */
//    public Colour[][] generateColours(float[][] heights, float amplitude) {
//        Colour[][] colours = new Colour[heights.length][heights.length];
//        for (int z = 0; z < heights.length; z++) {
//            for (int x = 0; x < heights[z].length; x++) {
//                colours[z][x] = calculateColour(heights[z][x], amplitude);
//            }
//        }
//        return colours;
//    }

    /**Determines the colour of the vertex based on the provided height.
     * @param height - Height of the vertex.
     * @param amplitude - The maximum height that a vertex can be (
     * @return
     */
//    private Colour calculateColour(float height, float amplitude) {
//        float value = (height + amplitude) / (amplitude * 2);
//        value = Maths.clamp((value - halfSpread) * (1f / spread), 0f, 0.9999f);
//        int firstBiome = (int) Math.floor(value / part);
//        float blend = (value - (firstBiome * part)) / part;
//        return Colour.interpolateColours(biomeColours[firstBiome], biomeColours[firstBiome + 1], blend, null);
//    }

}