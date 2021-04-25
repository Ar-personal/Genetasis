package engine.utils;

import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utils {

    public static String loadResource(String fileName) throws Exception {
        String result;
        try (InputStream in = Utils.class.getResourceAsStream(fileName);
             Scanner scanner = new Scanner(in, java.nio.charset.StandardCharsets.UTF_8.name())) {
            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }


    public static List<String> readAllLines(String fileName) throws Exception {
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Class.forName(Utils.class.getName()).getResourceAsStream(fileName)))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        }
        return list;
    }

    public static float[] listToArray(List<Float> list) {
        int size = list != null ? list.size() : 0;
        float[] floatArr = new float[size];
        for (int i = 0; i < size; i++) {
            floatArr[i] = list.get(i);
        }
        return floatArr;
    }

    public static float[] float2Dto1D(float[][] array) {
        int size = array.length;
        float[] floatArr = new float[size];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                floatArr[i] = array[i][j];
            }
        }
        return floatArr;
    }


    public static float[] vector3ToArray(Vector3f[] array) {
        int size = array.length;
        float[] floatArr = new float[size * 3];
        for(int o = 0; o < size; o += 3) {
            if(o + 1 >= size){
                continue;
            }


            floatArr[o] = array[o].x();
            floatArr[o + 1] = array[o].y();
            floatArr[o + 2] = array[o].z();

        }
        return floatArr;
    }
}
