package nl.weeaboo.vn.math;

import static nl.weeaboo.lua2.FastMath.fastCos;
import static nl.weeaboo.lua2.FastMath.fastSin;
import nl.weeaboo.lua2.io.LuaSerializable;

@LuaSerializable
public final class Matrix extends AbstractMatrix {

	private static final long serialVersionUID = 1L;

	private static final Matrix IDENTITY_MATRIX = new Matrix(1, 0, 0, 0, 1, 0);
	
	private transient float[] glMatrix;
	
	public Matrix(double m00, double m01, double m02, double m10, double m11, double m12) {
		super(m00, m01, m02, m10, m11, m12);
	}
	
	//Functions		
	public MutableMatrix mutableCopy() {
		return new MutableMatrix(m00, m01, m02, m10, m11, m12);
	}
	
	public Matrix plus(AbstractMatrix m) {
		return new Matrix(
			m00 + m.m00, m01 + m.m01, m02 + m.m02,
			m10 + m.m10, m11 + m.m11, m12 + m.m12);
	}
	public Matrix minus(AbstractMatrix m) {
		return new Matrix(
			m00 - m.m00, m01 - m.m01, m02 - m.m02,
			m10 - m.m10, m11 - m.m11, m12 - m.m12);
	}
	public Matrix multiply(double s) {
		return new Matrix(
			m00 * s, m01 * s, m02 * s,
			m10 * s, m11 * s, m12 * s);
	}
	public Matrix multiply(AbstractMatrix m) {
		return new Matrix(
			m00 * m.m00 + m01 * m.m10,
			m00 * m.m01 + m01 * m.m11,
			m00 * m.m02 + m01 * m.m12 + m02,

			m10 * m.m00 + m11 * m.m10,
			m10 * m.m01 + m11 * m.m11,
			m10 * m.m02 + m11 * m.m12 + m12);
	}
	
	public Matrix translatedCopy(double x, double y) {
		return new Matrix(
			m00, m01, x * m00 + y * m01 + m02,
			m10, m11, x * m10 + y * m11 + m12);
	}
    public Matrix rotatedCopy(double angle) {
		double cos = fastCos((float)angle);
		double sin = fastSin((float)angle);
		
    	return new Matrix(
    		cos * m00 + sin * m01, -sin * m00 + cos * m01, m02,
    		cos * m10 + sin * m11, -sin * m10 + cos * m11, m12);
    }
    public Matrix scaledCopy(double sx, double sy) {
    	return new Matrix(
    		m00 * sx, m01, m02,
    		m10, m11 * sy, m12);
    }
    
    @Override
    public float[] toGLMatrix() {
    	if (glMatrix == null) {
    		glMatrix = super.toGLMatrix();
    	}
    	return glMatrix;
    }
    
	//Getters
	public static Matrix identityMatrix() {
		return IDENTITY_MATRIX;
	}
	public static Matrix translationMatrix(double x, double y) {
		if (x == 0 && y == 0) {
			return IDENTITY_MATRIX;
		}
		return new Matrix(1, 0, x, 0, 1, y);
	}
	public static Matrix scaleMatrix(double x, double y) {
		if (x == 0 && y == 0) {
			return IDENTITY_MATRIX;
		}
		return new Matrix(1, 0, x, 0, 1, y);
	}
	public static Matrix rotationMatrix(double angle) {
		if (angle == 0) {
			return IDENTITY_MATRIX;
		}

		double cos = fastCos((float)angle);
		double sin = fastSin((float)angle);
		
		return new Matrix(cos, -sin, 0, sin, cos, 0);
	}
	
	//Setters
	
}
