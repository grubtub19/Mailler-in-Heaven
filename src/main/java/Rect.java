import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;

public class Rect {
	Point center;	
	int top;		
	int topBug;
	int bottom;
	int bottomBug;
	int left;
	int leftBug;
	int right;
	int rightBug;
	int nobug = 10;
	int wallWidth = 10;
	Wall wallN;			//4 Walls make up a Rect. Rect is used to make it easy to create 4 hitboxes in one.
	Wall wallE;
	Wall wallS;
	Wall wallW;
	Polygon poly;
	ArrayList<Image> images;
	ArrayList<Image> ceiling;
	ArrayList<Image> bush;
	NotHyrule nH;
	
	
	/**
	 * Creates a rectangle with 4 collision walls on each side
	 * @param wallList 	the wallList from NotHyrule 
	 * @param center	the center of the rectangle
	 * @param width		the width of the rectangle
	 * @param height	the y_offset of the rectangle
	 */
	public Rect(NotHyrule nH, ArrayList<Wall> wallList, Point topLeft, int width, int height) {
		this.nH = nH;
		top = topLeft.y;
		topBug = top + nobug;
		bottom = topLeft.y + height;
		bottomBug = bottom - nobug;
		left = topLeft.x;
		leftBug = left + nobug;
		right = topLeft.x + width;
		rightBug = right - nobug;
		
		int[] xArray1 = { leftBug, rightBug, rightBug, leftBug };
		int[] yArray1 = { top, top, top + wallWidth, top + wallWidth};
		Point2D.Double point1a = new Point2D.Double(left, top);
		Point2D.Double point1b = new Point2D.Double(right, top);
		wallN = new Wall(xArray1, yArray1, point1a, point1b);
		wallList.add(wallN);
		
		int[] xArray2 = { left, left + wallWidth, left + wallWidth, left };
		int[] yArray2 = { topBug, topBug, bottomBug, bottomBug };
		Point2D.Double point2a = new Point2D.Double( left, bottom );
		Point2D.Double point2b = new Point2D.Double( left, top );
		wallW = new Wall(xArray2, yArray2, point2a, point2b);
		wallList.add(wallW);
		
		int[] xArray3 = { leftBug, rightBug, rightBug, leftBug };
		int[] yArray3 = { bottom - wallWidth, bottom - wallWidth, bottom, bottom };
		Point2D.Double point3a = new Point2D.Double( right, bottom );
		Point2D.Double point3b = new Point2D.Double( left, bottom );
		wallS = new Wall(xArray3, yArray3, point3a, point3b);
		wallList.add(wallS);
		
		int[] xArray4 = { right - wallWidth, right, right, right - wallWidth };
		int[] yArray4 = { topBug, topBug, bottomBug, bottomBug };
		Point2D.Double point4a = new Point2D.Double( right, top );
		Point2D.Double point4b = new Point2D.Double( right,  bottom );
		wallE = new Wall( xArray4, yArray4, point4a, point4b);
		wallList.add(wallE);
		
		int[] xArrayP = { left, right, right, left };
		int[] yArrayP = { top, top, bottom , bottom };
		poly = new Polygon(xArrayP, yArrayP, xArrayP.length);
		
		images = new ArrayList<Image>();
		ceiling = new ArrayList<Image>();
		bush = new ArrayList<Image>();
		int high = (bottom - top)/102;
		int wide = (right - left)/102;
		//System.out.println("wide = " + wide);
		
		for(int i = 0; i < wide; i++) {
			for(int j = 0; j < high; j++) {
				bush.add(new Image("plant.png"));
				bush.get(i*high + j).setPos(left + i*102, top + j*102);
			}
		}
		
		for(int j = 0; j < high; j++) {
			ceiling.add(new Image("wall1.png"));
			ceiling.get(j*2).setPos(left, top + j*102);
			ceiling.add(new Image("wall3.png"));
			ceiling.get(j*2+1).setPos(right-15, top + j*102);
		}
		
		for (int i = 0; i < wide; i++ ) {
			ceiling.add(new Image("wall2.png"));
			ceiling.get(high*2 + i).setPos(left+i*102, top);	
		}
		
		int size = right - left;
		
		if(size <= 102) {
			images.add(new Image("wallSmall.png"));
		} else {
			images.add(new Image("wallStart.png"));
		}
		size -= 102;
		images.get(0).setPos(left, bottom - 80);
		int i = 1;
		while(size >= 204) {
			images.add(new Image("wall.png"));
			images.get(i).setPos(left + i*102, bottom - 80);
			size -= 102;
			i++;
		}
		if(size >= 102) {
			images.add(new Image("wallEnd.png"));
			images.get(i).setPos(left + i*102, bottom - 80);
		}
	}
	
	/**
	 * Draws the rectangle. Does not draw the individual walls that make up the hitboxes.
	 * @param dbg 	Graphics from NotHyrule
	 */
	public void draw(Graphics2D dbg) {
		if(nH.isHell()) {
			dbg.setColor(Color.decode("#131317"));
			dbg.fillPolygon(poly);
			
			dbg.setStroke(new BasicStroke(10));
			dbg.drawPolygon(poly);
			wallN.draw(dbg);
			wallS.draw(dbg);
			wallE.draw(dbg);
			wallW.draw(dbg);
			
			for(int i = 0; i < ceiling.size(); i++) {
				ceiling.get(i).draw(dbg);
			}
			for(int i = 0; i < images.size(); i++) {
				images.get(i).draw(dbg);
			}
			
		} else {
			for(int i = 0; i < bush.size(); i++) {
				bush.get(i).draw(dbg);
			}
		}
	}
	
	
}
