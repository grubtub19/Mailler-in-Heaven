import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;

//TODO Convert more and maybe move stuff to rigidbody
public abstract class Entity extends GameObject {

	/**
	 * stores array of points and uses them to draw a polygon. These include playable characters and enemies. Anything that moves
	 * @param xArray	array of x coordinates of the polygon
	 * @param yArray	array of y coordinates of the polygon
	 * @param x			x coordinate of the location
	 * @param y			y coordinate of the location
	 */
	public Entity(NotHyrule nH, Hitbox hitbox, HealthC health, HealthBar healthBar) {
		super();

		addComponent(hitbox, Hitbox.class);
		addComponent(health, HealthC.class);
		addComponent(healthBar, HealthBar.class);

		this.nH = nH;
	}
	
	//------GameObject-------
	NotHyrule nH;
	
	//------get/set------
	
	protected double facingTheta;
	protected double xVel;
	protected double yVel;
	protected double xAccel;
	protected double yAccel;
	protected double topSpeed = 5;
	protected double accelSpeed = 6;
	protected double resistance = .4;
	HealthC health;
	HealthBar healthBar;

	/**
	 * returns the current HP of the Entity
	 * @return the current HP of the Entity
	 */
	public int getCurrHP() {
		return currHP;
	}
	
	/**
	 * returns the max HP of the Entity
	 * @return the max HP of the Entity
	 */
	public int getMaxHP() {
		return maxHP;
	}	
	
	/**
	 * returns direction the of the character based off the inputs. Not the movement and not the direction of the cursor
	 * @return theta
	 */
	public double getFacingTheta() {
		return facingTheta;
	}
	
	/**
	 * returns the direction the character is currently moving
	 * @return the direction the character is currently moving
	 */
	public double getMovingTheta() {
		return Math.toDegrees(Math.atan2(xVel,yVel));
	}
	
	/**
	 * returns the x value of the velocity
	 * @return	the x value of the velocity
	 */
	public double getXVel() {
		return xVel;
	}
	/**
	 * returns the y value of the velocity
	 * @return the y value of the velocity
	 */
	public double getYVel() {
		return yVel;
	}
	
	/**
	 * returns the x value of the acceleration
	 * @return	the x value of the acceleration
	 */
	public double getXAccel() {
		return xAccel;
	}
	
	/**
	 * returns the y value of the acceleration
	 * @return the y value of the acceleration
	 */
	public double getYAccel() {
		return yAccel;
	}
	
	/**
	 * sets the values of the direction the character is facing
	 * @param theta
	 */
	public void setFacingTheta(double theta) {
		facingTheta = theta;
	}
	
	public abstract double getShootingDirection();
	
	public abstract double getPointTheta();
	
	/**
	 * sets the x value of acceleration
	 * @param a x value of the acceleration
	 */
	public void setXAccel(double a) {
		xAccel = a;
	}
	
	/**
	 * sets the y value of the acceleration
	 * @param a the y value of acceleration
	 */
	public void setYAccel(double a) {
		yAccel = a;
	}
	
	public void setVel(double x, double y) {
		xVel = x;
		yVel = y;
	}
	
	/**
	 * sets the x value of the velocity
	 * @param a the x value of the velocity
	 */
	public void setXVel(double a) {
		xVel = a;
	}
	
	/**
	 * sets the y value of the velocity
	 * @param a the y value of the velocity
	 */
	public void setYVel(double a) {
		yVel = a;
	}
	
	public void setPoly(Polygon polygon) {
		for(int i=0; i<polygon.xpoints.length; i++) {
			this.xArray[i] = polygon.xpoints[i];
			this.yArray[i] = polygon.ypoints[i];
		}
	}
	
	//------functions-------
	
	/**
	 * moves the character a set distance for 1 frame
	 * @param x x value of the translation
	 * @param y y value of the translation
	 */
	public void translate(double x, double y) {
		for(int i=0; i<xArray.length; i++) {
			xArray[i] += x;
			yArray[i] += y;
		}
	}
	
	/**
	 * rotates the polygon around its center
	 * @param shape to rotate
	 * @param theta the theta value to rotate it in degrees
	 * @return the new polygon
	 */
	public Polygon rotate(Shape shape, double theta) {
		Point2D.Double center = getCenterPos();
		ArrayList<Point> points = new ArrayList<Point>();
		
		AffineTransform at = new AffineTransform();
		at.rotate(Math.toRadians(theta), center.x, center.y);
		
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
	 * rotates the polygon around a specified point
	 * @param shape to rotate
	 * @param theta to rotate in degrees
	 * @param x the x coordinate of the point to rotate the polygon around
	 * @param y the y coordinate of the point to rotate the polygon around
	 * @return the new polygon
	 */
	public Polygon rotate(Shape shape, double theta, double x, double y) {
		ArrayList<Point> points = new ArrayList<Point>();
		
		AffineTransform at = new AffineTransform();
		at.rotate(Math.toRadians(theta), x, y);
		
		for (PathIterator pathI = shape.getPathIterator(at); !pathI.isDone(); pathI.next()) {
			double [] values = new double[6];								//iterates through all the points and rotates them using AffineTransform
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
	 * calculates what the next velocity should be
	 */
	protected void updateVel() {
		xVel += xAccel;
		yVel += yAccel;
		double distance = Math.sqrt(xVel*xVel+yVel*yVel);
		if(distance > topSpeed) {
			double directionTheta = Math.atan2(yVel, xVel);
			yVel = Math.sin(directionTheta)*topSpeed;
			xVel = Math.cos(directionTheta)*topSpeed;
		}
	}
	
	/**
	 * slows the x velocity of the entity toward 0
	 * @param resist the amount to decrease the velocity each frame
	 */
	protected void resistX(double resist, double theta) {
		double xM = Math.abs(Math.cos(theta));
		
		//System.out.println(xM);
		if (xVel < 0) {
			xVel += xM*resist;
			if (xVel >= 0)
				xVel = 0;
		} else if (xVel > 0) {
			xVel -= xM*resist;
			if (xVel <= 0)
				xVel = 0;
		}
	}
	/**
	 * slows the y velocity of the entity toward 0
	 * @param resist the amount to decrease the velocity each frame
	 */
	protected void resistY(double resist, double theta) {
		double yM = Math.abs(Math.sin(theta));
		//System.out.println(yM);
		if (yVel < 0) {
			yVel += yM*resist;
			if (yVel >= 0)
				yVel = 0;
		} else if (yVel > 0) {
			yVel -= yM*resist;
			if (yVel <= 0)
				yVel = 0;
		}
	}
	/**
	 * slows the velocity of the entity toward 0
	 * @param resist the amount to decrease the velocity each frame
	 */
	protected void resist(double resist) {
		double theta = Math.atan2(yVel, xVel);
		resistX(resist, theta);
		resistY(resist, theta);
	}
	
	protected void updateAccel() {
		
	}
	
	/**
	 * updates the entity
	 */
	public void update() {
		updateVel();
		updateAccel();
	}
	
	/**
	 * draws the entity
	 * @param g the graphics from NotHyrule
	 */
	public void draw(Graphics g) {
		g.setColor(Color.white);
		g.fillPolygon(poly);
	}
}
