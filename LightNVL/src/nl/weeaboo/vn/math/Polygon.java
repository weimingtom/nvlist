package nl.weeaboo.vn.math;

import java.io.Serializable;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.lua2.io.LuaSerializable;

@LuaSerializable
public class Polygon implements IPolygon, Serializable {

	private static final long serialVersionUID = 1L;
	
	private final int points;
	private final double[] pointsX;
	private final double[] pointsY;
	private final Rect2D bounds;
	
	public Polygon(Matrix transform, double x, double y, double w, double h) {
		this(coords(transform, x, y, w, h));
	}
	public Polygon(double... coords) {
		this(xcoords(coords), ycoords(coords));
	}
	public Polygon(double[] x, double[] y) {
		points = Math.min(x.length, y.length);
		
		pointsX = new double[points];
		System.arraycopy(x, 0, pointsX, 0, points);
		
		pointsY = new double[points];
		System.arraycopy(y, 0, pointsY, 0, points);

		double x0 = Double.POSITIVE_INFINITY;
		double y0 = Double.POSITIVE_INFINITY;
		double x1 = Double.NEGATIVE_INFINITY;
		double y1 = Double.NEGATIVE_INFINITY;
		
		for (int n = 0; n < points; n++) {
			x0 = Math.min(x0, pointsX[n]);
			y0 = Math.min(y0, pointsY[n]);
			x1 = Math.max(x1, pointsX[n]);
			y1 = Math.max(y1, pointsY[n]);
		}
		
		if (Double.isNaN(x0) || Double.isNaN(x1) || Double.isNaN(y0) || Double.isNaN(y1)) {
			bounds = new Rect2D(0, 0, 0, 0);
		} else {
			bounds = new Rect2D(x0, y0, x1-x0, y1-y0);
		}
	}
	
	//Functions
	private static double[] coords(Matrix transform, double x, double y, double w, double h) {
		Vec2 p0 = transform.transform(x,   y  );
		Vec2 p1 = transform.transform(x+w, y  );
		Vec2 p2 = transform.transform(x+w, y+h);
		Vec2 p3 = transform.transform(x,   y+h);
		
		return new double[] { p0.x, p0.y, p1.x, p1.y, p2.x, p2.y, p3.x, p3.y };
	}
	private static double[] xcoords(double[] coords) {
		double[] result = new double[coords.length/2];				
		for (int d = 0, s = 0; d < result.length; d++, s+=2) {
			result[d] = coords[s];
		}
		return result;
	}
	private static double[] ycoords(double[] coords) {
		double[] result = new double[coords.length/2];				
		for (int d = 0, s = 1; d < result.length; d++, s+=2) {
			result[d] = coords[s];
		}
		return result;
	}

	@Override
	public boolean contains(double x, double y) {
		if (points <= 2 || !bounds.contains(x, y)) {
			return false; //Polygon is a point, line, or bounding rect doesn't intersect
		}
		
		//Raycasting algorithm
		int hits = 0;
		for (int n = 0; n < points; n++) {
			double lx, ly;
			if (n > 0) {
				lx = pointsX[n-1];
				ly = pointsY[n-1];
			} else {
				lx = pointsX[points-1];
				ly = pointsY[points-1];
			}
			
			double cx = pointsX[n];
			double cy = pointsY[n];

			if (cy == ly) {
				continue;
			}

			double leftX;
			if (cx < lx) {
				if (x >= lx) continue;
				leftX = cx;
			} else {
				if (x >= cx) continue;
				leftX = lx;
			}

			double test1, test2;
			if (cy < ly) {
				if (y < cy || y >= ly) {
					continue;
				}
				if (x < leftX) {
					hits++;
					continue;
				}
				test1 = x - cx;
				test2 = y - cy;
			} else {
				if (y < ly || y >= cy) {
					continue;
				}
				if (x < leftX) {
					hits++;
					continue;
				}
				test1 = x - lx;
				test2 = y - ly;
			}

			if (test1 < (test2 / (ly - cy) * (lx - cx))) {
				hits++;
			}
		}

		return (hits & 1) != 0; //Number of hits is odd
	}

	//Getters
	@Override
	public Rect2D getBoundingRect() {
		return bounds;
	}
	
	//Setters
	
}
