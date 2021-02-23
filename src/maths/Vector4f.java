package maths;

import org.lwjglx.util.vector.ReadableVector4f;
import org.lwjglx.util.vector.WritableVector4f;

import java.io.Serializable;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Vector;

import static com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver.length;
import static maths.Matrix4f.SIZE;

public class Vector4f{

    private static final long serialVersionUID = 1L;
    private float[] elements = new float[SIZE * SIZE];
    public float x, y, z, w;


    public Vector4f(float x, float y, float z, float w) {
        set(x, y, z, w);
    }

    public Vector4f(Vector3f vector3f, int i) {
        set(vector3f.getX(), vector3f.getY(), vector3f.getZ(), i);
    }


    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }


    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public void set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * Load from another Vector4f
     * @param src The source vector
     * @return this
     */
    public Vector4f set(ReadableVector4f src) {
        x = src.getX();
        y = src.getY();
        z = src.getZ();
        w = src.getW();
        return this;
    }

    /**
     * @return the length squared of the vector
     */
    public float lengthSquared() {
        return x * x + y * y + z * z + w * w;
    }

    /**
     * Translate a vector
     * @param x The translation in x
     * @param y the translation in y
     * @return this
     */
    public Vector4f translate(float x, float y, float z, float w) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
        return this;
    }

    public static Matrix4f multiply(Vector4f vector4f, Matrix4f matrix4f) {
        Matrix4f result = Matrix4f.identity();

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                result.set(i, j, vector4f.get(i, 0) * matrix4f.get(0, j) +
                        vector4f.get(i, 1) * matrix4f.get(1, j) +
                        vector4f.get(i, 2) * matrix4f.get(2, j) +
                        vector4f.get(i, 3) * matrix4f.get(3, j));
            }
        }

        return result;
    }

    /**
     * Add a vector to another vector and place the result in a destination
     * vector.
     * @param left The LHS vector
     * @param right The RHS vector
     * @param dest The destination vector, or null if a new vector is to be created
     * @return the sum of left and right in dest
     */
    public static Vector4f add(Vector4f left, Vector4f right, Vector4f dest) {
        if (dest == null)
            return new Vector4f(left.x + right.x, left.y + right.y, left.z + right.z, left.w + right.w);
        else {
            dest.set(left.x + right.x, left.y + right.y, left.z + right.z, left.w + right.w);
            return dest;
        }
    }

    /**
     * Subtract a vector from another vector and place the result in a destination
     * vector.
     * @param left The LHS vector
     * @param right The RHS vector
     * @param dest The destination vector, or null if a new vector is to be created
     * @return left minus right in dest
     */
    public static Vector4f sub(Vector4f left, Vector4f right, Vector4f dest) {
        if (dest == null)
            return new Vector4f(left.x - right.x, left.y - right.y, left.z - right.z, left.w - right.w);
        else {
            dest.set(left.x - right.x, left.y - right.y, left.z - right.z, left.w - right.w);
            return dest;
        }
    }




    /**
     * Negate a vector and place the result in a destination vector.
     * @param dest The destination vector or null if a new vector is to be created
     * @return the negated vector
     */



    /**
     * Normalise this vector and place the result in another vector.
     * @param dest The destination vector, or null if a new vector is to be created
     * @return the normalised vector
     */
    public Vector4f normalise(Vector4f dest) {
        float l = length();

        if (dest == null)
            dest = new Vector4f(x / l, y / l, z / l, w / l);
        else
            dest.set(x / l, y / l, z / l, w / l);

        return dest;
    }

    /**
     * The dot product of two vectors is calculated as
     * v1.x * v2.x + v1.y * v2.y + v1.z * v2.z + v1.w * v2.w
     * @param left The LHS vector
     * @param right The RHS vector
     * @return left dot right
     */
    public static float dot(Vector4f left, Vector4f right) {
        return left.x * right.x + left.y * right.y + left.z * right.z + left.w * right.w;
    }


    public Vector4f scale(float scale) {
        x *= scale;
        y *= scale;
        z *= scale;
        w *= scale;
        return new Vector4f(x, y, z, w);
    }

    public Vector4f store(FloatBuffer buf) {

        buf.put(x);
        buf.put(y);
        buf.put(z);
        buf.put(w);

        return this;
    }

    public String toString() {
        return "Vector4f: " + x + " " + y + " " + z + " " + w;
    }

    public float get(int x, int y) {
        return elements[y * SIZE + x];
    }

    /**
     * @return x
     */
    public final float getX() {
        return x;
    }

    /**
     * @return y
     */
    public final float getY() {
        return y;
    }

    /**
     * Set X
     * @param x
     */
    public final void setX(float x) {
        this.x = x;
    }

    /**
     * Set Y
     * @param y
     */
    public final void setY(float y) {
        this.y = y;
    }

    /**
     * Set Z
     * @param z
     */
    public void setZ(float z) {
        this.z = z;
    }


    /* (Overrides)
     * @see org.lwjgl.vector.ReadableVector3f#getZ()
     */
    public float getZ() {
        return z;
    }

    /**
     * Set W
     * @param w
     */
    public void setW(float w) {
        this.w = w;
    }

    /* (Overrides)
     * @see org.lwjgl.vector.ReadableVector3f#getZ()
     */
    public float getW() {
        return w;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector4f vector4f = (Vector4f) o;
        return Arrays.equals(elements, vector4f.elements);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(elements);
    }
}
