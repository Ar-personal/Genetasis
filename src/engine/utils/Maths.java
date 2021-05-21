package engine.utils;

public class Maths {

    public static float clamp(float value, float min, float max) {
        return Math.max(Math.min(value, max), min);
    }
}

