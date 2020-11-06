import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Polymorph {
	
	/**
	 * returns the center of the shape
	 * @param shape
	 * @return	Point2D.Double center
	 */
	public static Point2D.Double getCenter(Shape shape) {
		Rectangle bounds = shape.getBounds();
		double x = bounds.getX() + bounds.getWidth()/2;
		double y = bounds.getY() + bounds.getHeight()/2;
		return new Point2D.Double(x,y);
	}
	
	/**
	 * moves the first Polygon to be centered on the second Polygon
	 * @param one Polygon
	 * @param two Polygon
	 */
	public static void centerPolyOnPoly(Polygon one, Polygon two) {
		Point2D centerOne = getCenter(one);
		Point2D centerTwo = getCenter(two);
		int deltaX = (int)(centerTwo.getX() - centerOne.getX());
		int deltaY = (int)(centerTwo.getY() - centerOne.getY());
		one.translate(deltaX, deltaY);
	}
	
	/**
	 * rotates the shape around itself
	 * @param shape Shape
	 * @param theta double in degrees
	 * @return Polygon
	 */
	public static Polygon rotate(Shape shape, double theta) {
		Point2D.Double center = getCenter(shape);
		ArrayList<Point> points = new ArrayList<Point>();
		
		AffineTransform at = new AffineTransform();
		at.rotate(theta, center.x, center.y);
		
		for (PathIterator pathI = shape.getPathIterator(at); !pathI.isDone(); pathI.next()) {
			double [] values = new double[6];
			pathI.currentSegment(values);
			points.add(new Point((int)values[0],(int)values[1]));
		}
		
		int [] xArray = new int[points.size()-1];

		int [] yArray = new int[points.size()-1];
		
		for (int i = 0; i < points.size()-1; i +=1 ) {
			xArray[i] = points.get(i).x;
			yArray[i] = points.get(i).y;
		}
		Polygon rshape = new Polygon(xArray,yArray,xArray.length);
		return rshape;
	}
	/**
	 * rotates the shape around (x,y)
	 * @param shape Shape
	 * @param theta double in degrees
	 * @param x double x-coordinate
	 * @param y double y-coordinate
	 * @return Polygon
	 */
	public static Polygon rotate(Shape shape, double theta, double x, double y) {
		ArrayList<Point> points = new ArrayList<Point>();
		
		AffineTransform at = new AffineTransform();
		at.rotate(theta, x, y);
		
		for (PathIterator pathI = shape.getPathIterator(at); !pathI.isDone(); pathI.next()) {
			double [] values = new double[6];
			pathI.currentSegment(values);
			points.add(new Point((int)values[0],(int)values[1]));
		}
		
		int [] xArray = new int[points.size()-1];

		int [] yArray = new int[points.size()-1];
		
		for (int i = 0; i < points.size()-1; i +=1 ) {
			xArray[i] = points.get(i).x;
			yArray[i] = points.get(i).y;
		}
		Polygon rshape = new Polygon(xArray,yArray,xArray.length);
		return rshape;
	}
	public static double[][] rotateArray(Shape shape, double theta) {
		ArrayList<Point> points = new ArrayList<Point>();
		Point2D.Double center = getCenter(shape);
		AffineTransform at = new AffineTransform();
		at.rotate(theta, center.x, center.y);
		
		for (PathIterator pathI = shape.getPathIterator(at); !pathI.isDone(); pathI.next()) {
			double [] values = new double[6];
			pathI.currentSegment(values);
			points.add(new Point((int)values[0],(int)values[1]));
		}
		double[][] d = new double[points.size()][points.size()];
		
		for(int i = 0; i < points.size(); i++) {
			d[0][i] = points.get(i).getX();
			d[1][i] = points.get(i).getY();
		}

		return d;
	}
	
}
