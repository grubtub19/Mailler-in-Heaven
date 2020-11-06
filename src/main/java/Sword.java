import java.awt.Polygon;
import java.awt.geom.Area;

public class Sword {
	private Polygon basePoly;				//polygon that is always centered on player
	private Polygon poly;					//polygon that is moved, rotated, and displayed
	private double swingTheta = 0;			//how far into the swing the sword is in degrees
	private boolean swinging = false;
	private double swingWidthTheta = Math.toRadians(360);	//the full width of a swing
	private double swingTime;				//how long the swing will take
	private int distance = 20;				//how far away the center of the player is from the center of the sword when it swings
	private int aD;							//the sword's attack damage
	boolean swingable = true;				//if the sword can be swung at this point in time
	private int pauseTimer = 0;
	private int maxPauseTime = 12;
	public double swingDirection;
	private boolean qSword = false;
	private boolean right = true;
	private int standY = 20;
	private boolean getRight = true;
	/**
	 * initializes a new sword linked to a player character
	 * @param player			the player character who is holding the sword
	 * @param xArray		the array of x-coordinates that make up the sword
	 * @param yArray		the array of y-coordinates that make up the sword
	 * @param nArray		the length of one of these arrays
	 * @param sTime			the swing time
	 * @param sWidthTheta	the swing theta
	 * @param aD			the attack damage
	 */
	public Sword(Player player, double[] xArray, double[] yArray, int nArray, double sTime, double sWidthTheta, int aD) {
		int[] iyArray = new int[yArray.length];
		int[] ixArray = new int[xArray.length];
		for(int i=0; i < xArray.length; i++) {
			ixArray[i] = (int)xArray[i];
			iyArray[i] = (int)yArray[i];
		}
		basePoly = new Polygon(ixArray, iyArray, nArray);																	//creates basePoly using coordinates within the arrays															//centers basePoly on the center of player
		setPoly(basePoly);																									//sets swordPoly as copy of basePoly																										//assigns a player to hold the sword
		swingTime = sTime;																											//copies swingTime
		swingWidthTheta = Math.toRadians(sWidthTheta);																						//copies swingWidthTheta
		this.aD = aD;
	}
	
	/**
	 * creates a copy of another sword and gives it to a new player character
	 * @param sword Sword
	 * @param player Player
	 */
	public Sword(Sword sword, Player player) {
		this.basePoly = new Polygon(sword.getBaseXPoints(),sword.getBaseYPoints(),sword.getBasePoly().npoints);
		Polymorph.centerPolyOnPoly(basePoly, player.getPoly());
		setPoly(basePoly);
		swingTime = sword.getSwingTime();
		swingWidthTheta = sword.getSwingWidthTheta();
		aD = sword.getAD();
	}
	
	public boolean isRight() {
		return getRight;
	}
	
	/**
	 * creates a new polygon that is a copy of the parameter polygon
	 * @param poly Polygon you copy from
	 */
	public void setPoly(Polygon poly) {
		this.poly = new Polygon(poly.xpoints, poly.ypoints, poly.npoints);
	}
	
	/**
	 * 
	 * @return the Area of the sword Polygon
	 */
	public Area getArea() {
		return new Area(poly);
	}
	
	public double getSwingTime() {
		return swingTime;
	}
	
	/**
	 * get the angle of a full swing
	 * @return theta in degrees
	 */
	public double getSwingWidthTheta() {
		return swingWidthTheta;
	}
	
	/**
	 * swing the sword
	 */
	public void swing(Player player) {
		if(pauseTimer == 0 || pauseTimer > maxPauseTime/2) {
			swingDirection = Math.toRadians(player.getInputDirection() - 90);
			qSword = true;
			getRight = !getRight;
			
		}
	}
	
	/**
	 * 
	 * @return true if the sword is swinging
	 */
	public boolean isSwinging() {
		return swinging;
	}
	
	/**
	 * 
	 * @return return int attack damage
	 */
	public int getAD() {
		return aD;
	}

	/**
	 * moves the sword to the right location depending on where you are in the swing
	 * @param player
	 */
	public void calcSword(Player player) {
		if(pauseTimer == 0 && qSword) {
			swinging = true;
			qSword = false; 
			player.setSliding2();
			player.setXVel( 3* Math.cos(player.getPointTheta()));
			player.setYVel( 3* Math.sin(player.getPointTheta()));
		}
		//getRight = right;
		
		int reach = distance+basePoly.getBounds().height/2; 										
		Polymorph.centerPolyOnPoly(basePoly, player.getPoly());							//update the basePoly to be centered on the player character
		double x;
		double y;
		if(right) {
			x = reach*(Math.cos(swingDirection-swingWidthTheta/2));				
			y = reach*(Math.sin(swingDirection-swingWidthTheta/2))-standY;		//calculate the location for the sword
		} else {
			x = reach*(Math.cos(swingDirection+swingWidthTheta/2));				
			y = reach*(Math.sin(swingDirection+swingWidthTheta/2))-standY;
		}
		if(swinging && pauseTimer == 0 && swingTheta < swingWidthTheta) {												//if swinging
			setPoly(basePoly);
			poly.translate((int)x, (int)y);
			if(right) {
				setPoly(Polymorph.rotate(poly, swingDirection + Math.PI/2 -swingWidthTheta/2));
				setPoly(Polymorph.rotate(poly, swingTheta, player.getCenterPosX(), player.getCenterPosY()-standY));
			} else {
				setPoly(Polymorph.rotate(poly, swingDirection + Math.PI/2 + swingWidthTheta/2));
				setPoly(Polymorph.rotate(poly, -swingTheta, player.getCenterPosX(), player.getCenterPosY()-standY));
			}
			
			swingTheta += swingWidthTheta/swingTime;
		} else if(swinging && pauseTimer == 0 && swingTheta >= swingWidthTheta) {
			swinging = false;
			pauseTimer = 1;
			swingTheta = 0;
			right = !right;
		}
		if(pauseTimer >= maxPauseTime)
			pauseTimer = 0;
		if(pauseTimer > 0)
			pauseTimer++;
	}
	/**
	 * 
	 * @return Polygon sword
	 */
	public Polygon getPoly() {
		return poly;
	}
	
	/**
	 * 
	 * @return Polygon basePoly centered on player
	 */
	public Polygon getBasePoly() {
		return basePoly;
	}

	public int getDistance() {
		return distance;
	}
	
	/**
	 * returns x-coordinate array
	 * @return int[]
	 */
	public int[] getXPoints() {
		return poly.xpoints;
	}

	/**
	 * return y-coordinate array
	 * @return int[]
	 */
	public int[] getYPoints() {
		return poly.ypoints;
	}
	
	/**
	 * return x-coordinate array of basePoly
	 * @return
	 */
	public int[] getBaseXPoints() {
		return basePoly.xpoints;
	}
	
	/**
	 * return y-coordinate array of basePoly
	 * @return
	 */
	public int[] getBaseYPoints() {
		return basePoly.ypoints;
	}
}
