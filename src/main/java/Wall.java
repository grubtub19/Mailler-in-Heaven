import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

public class Wall {					//One directional hitbox. If anything intersects() the buttonPoly, the Entity is pushed to the edge of the wall. Can only be non-rotated rectangles
	private Polygon poly;			//the hitbox area, should probably be a Rectangle in the future
	private Point2D.Double pointA;	//if you want a wall that pushed Entities up, pointA is on the top left corner of the polygon
	private Point2D.Double pointB;	//"" pointB is on the top right corner of the polygon
	
	/**
	 * creates a one directional wall that pushes Entities to the edge
	 * @param xArray int[] of x-coordinates
	 * @param yArray int[] of y-coordinates
	 * @param pointA Point2D.Double
	 * @param pointB Point2D.Double
	 */
	public Wall(int[] xArray, int[] yArray, Point2D.Double pointA, Point2D.Double pointB) {
		poly = new Polygon(xArray, yArray, xArray.length);
		this.pointA = pointA;
		this.pointB = pointB;
	}
	
	/**
	 * gets the Area
	 * @return Area
	 */
	public Area getArea() {
		return new Area(poly);			
	}
	
	public boolean isX() {
		if(pointA.y != pointB.y)
			return true;
		else 
			return false;
	}
	
	/**
	 * pushes Entity to edge of the wall and allows sliding
	 * @param e Entity
	 */
	public void resolveCollision(Entity e) {		
		double dx = 0;
		Point2D.Double pos = e.getCenterPos();
		if(pointA.getX() - pointB.getX() == 0) {
			if(pointA.getY() < pointB.getY())
				dx = pointA.getX() - (pos.getX() - (e.getPoly().getBounds2D().getHeight())/2);
			else {
				dx = pointA.getX() - (pos.getX() + (e.getPoly().getBounds2D().getWidth())/2);			
			}
		}
		
		double dy = 0;
		if(pointA.getY() - pointB.getY() == 0) {
			if(pointA.getX() < pointB.getX()) 
				dy = pointA.getY() - (pos.getY() + (e.getPoly().getBounds2D().getHeight())/2);
			else
				dy = pointA.getY() - (pos.getY() - (e.getPoly().getBounds2D().getHeight())/2);
		}		
		e.translate(dx, dy);
		e.setPolygon();
		
	}
	
	/**
	 * Draws the Wall
	 * @param g Graphics
	 */
	public void draw(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillPolygon(poly);
	}
	
}
