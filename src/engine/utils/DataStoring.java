package engine.utils;

import engine.graphics.Colour;
import org.joml.Vector3f;

import java.nio.ByteBuffer;

public class DataStoring {

    public static void packVertexData(Vector3f position, Vector3f normal, Colour colour, ByteBuffer buffer) {
        packVertexData(position.x, position.y, position.z, normal, colour, buffer);
    }

    public static void packVertexData(float x, float y, float z, Vector3f normal, Colour colour, ByteBuffer buffer) {
        storePosition(buffer, x, y, z);
        storeNormal(buffer, normal);
        storeColour(buffer, colour);
    }

    public static void packVertexData(float x, float y, float z, Colour colour, ByteBuffer buffer) {
        storePosition(buffer, x, y, z);
        storeColour(buffer, colour);
    }

    private static void storePosition(ByteBuffer buffer, float x, float y, float z) {
        buffer.putFloat(x);
        buffer.putFloat(y);
        buffer.putFloat(z);
    }

    private static void storeNormal(ByteBuffer buffer, Vector3f normal) {
        int packedInt = DataUtils.pack_2_10_10_10_REV_int(normal.x, normal.y, normal.z, 0);
        buffer.putInt(packedInt);
    }

    private static void storeColour(ByteBuffer buffer, Colour colour) {
        byte[] colourBytes = colour.getAsBytes();
        buffer.put(colourBytes);
    }

}