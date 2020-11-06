import java.awt.*;
import java.awt.geom.AffineTransform;

public class Button {
	boolean left;
	boolean active = false;
	int indent = 120;
	int height = 60;
	int activeXDev = 80;
	int buzierX = 100;
	double t = 0;
	double tSpeedInc = 0.2;
	double tSpeedDec = 0.1;
	String text;
	int xPos;
	int yPos;
	int effectTimer;
	int effectTimeTotal = 120;
	double effectScale = 1.5;
	AffineTransform effectTransform = new AffineTransform();
	Polygon buttonPoly;
	Polygon effectPoly;
	String backgroundColor = "#009688";
	String backgroundActive = "#ffffff";
	String borderColor = "#000000";
	String borderActive = "#000000";
	String textColor = "#ffffff";
	
	/**
	 * creates the animated menu button
	 * @param text	what the button says
	 * @param xPos	the position of the pointy corner
	 * @param yPos	the position of the pointy corner
	 * @param left	whether it is on the left or right side of the screen
	 */
	public Button(String text, int xPos, int yPos, boolean left) {
		this.text = text;
		
		this.yPos = yPos;
		this.left = left;
		if(left) {
			this.xPos = xPos;
			int[] xArray = { -activeXDev*2, xPos, xPos-height, -activeXDev*2 };
			int[] yArray = { yPos - height/2, yPos - height/2, yPos + height/2, yPos + height/2};
			buttonPoly = new Polygon(xArray,yArray,xArray.length);
			effectPoly = new Polygon(xArray,yArray,xArray.length);
		} else {
			this.xPos = 1920-xPos;
			int[] xArray = { this.xPos, 1920 + activeXDev*2, 1920 + activeXDev*2, this.xPos+height };
			int[] yArray = { yPos - height/2, yPos - height/2, yPos + height/2, yPos + height/2};
			buttonPoly = new Polygon(xArray,yArray,xArray.length);
			effectPoly = new Polygon(xArray,yArray,xArray.length);
		}
	}
	
	/**
	 * centers text not used
	 * @param g
	 */
	private void drawTextCentered(Graphics g) {
		FontMetrics fm = g.getFontMetrics();
		int x = (xPos - fm.stringWidth(text) /2);
		int y = (yPos + fm.getHeight() / 4);
		g.drawString(text, x, y);
	}
	
	/**
	 * draws the text of the buttons on the right
	 * @param g Graphics
	 */
	private void drawTextRight(Graphics g) {
		FontMetrics fm = g.getFontMetrics();
		int x = (xPos - (int)getXDeviance()/2 - fm.stringWidth(text)) - indent;
		int y = (int)((yPos + fm.getAscent() / 2.0) - 3);
		g.drawString(text, x, y);
	}
	
	/**
	 * draws the text of the buttons on the left
	 * @param g Graphics
	 */
	private void drawTextLeft(Graphics g) {
		FontMetrics fm = g.getFontMetrics();
		int x = xPos + (int)getXDeviance()/2 + indent;
		int y = (int)((yPos + fm.getAscent() / 2.0) - 3);
		g.drawString(text, x, y);
	}
	
	/**
	 * how far left or right the text should sit
	 * @return
	 */
	private double getXDeviance() {
		//System.out.println("maxX(); = " + buttonPoly.getBounds().getMaxX() + ", x_offset = " + x_offset);
		if(left)
			return buttonPoly.getBounds().getMaxX() - xPos;
		else
			return -buttonPoly.getBounds().getMinX() + xPos;
	}

	/**
	 * sets the x position
	 * @param xVal
	 */
	public void setXPos(double xVal) {
		int x = (int)(xVal - getXDeviance());
		//
		if(left)
			buttonPoly.translate(x, 0);
		else
			buttonPoly.translate(-x, 0);
	}

	private void updateEffect(boolean active) {
		if(effectTimer > effectTimeTotal) {
			effectTimer = 0;
		} else if (effectTimer > 0) {
			effectTimer++;
		} else if(active) {
			effectTimer = 1;
		}
		effectTransform = new AffineTransform();
		effectTransform.scale(1 + ((effectScale - 1) * effectTimer / effectTimeTotal),1 + ((effectScale - 1) * effectTimer / effectTimeTotal));
	}

	public double getCenterPosX() {
		double small = effectPoly.xpoints[0];
		double big = effectPoly.xpoints[0];
		for(int i = 1; i < effectPoly.xpoints.length; i++) {
			if(effectPoly.xpoints[i] < small)
				small = effectPoly.xpoints[i];
			if(effectPoly.xpoints[i] > big)
				big = effectPoly.xpoints[i];
		}
		return(big + small)/2;
	}

	/**
	 * returns the y coordinate of the center position
	 * @return y coordinate of the center position
	 */
	public double getCenterPosY() {

		double small = effectPoly.ypoints[0];
		double big = effectPoly.ypoints[0];
		for(int i = 1; i < effectPoly.ypoints.length; i++) {

			if(effectPoly.ypoints[i] < small)
				small = effectPoly.ypoints[i];


			if(effectPoly.ypoints[i] > big)
				big = effectPoly.ypoints[i];
		}

		return(big + small)/2;
	}

	private void drawEffect(Graphics g) {
		AffineTransform originalTransform = ((Graphics2D)g).getTransform();
		((Graphics2D) g).translate(-getCenterPosX(),-getCenterPosY());
		((Graphics2D) g).scale(1.4,1.4);
		((Graphics2D) g).translate(getCenterPosX(),getCenterPosY());
		((Graphics2D) g).draw(effectPoly);
		((Graphics2D) g).setTransform(originalTransform);
	}

	/**
	 * updates everthing about the button. Also uses a bezier curve
	 * @param active if the menu is hovering this button
	 */
	public void update(boolean active) {
		if(active) {
			this.active = true;
			t+=tSpeedInc;
			if(t > 1)
				t = 1;

		} else {
			this.active = false;
			t-=tSpeedDec;
			if(t < 0)
				t = 0;
		}
		double x = Math.pow((1 - t), 2) * 0 + 2 * (1-t) * t * buzierX + t * t * activeXDev;
		setXPos(x);
		updateEffect(active);
	}

	public Color hex2Rgb(String colorStr) {
	    return new Color(
	    	Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
	        Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
	        Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
	}

	/**
	 * draws the button
	 * @param g Graphics
	 */
	public void draw(Graphics g) {
		if(active)
			g.setColor(hex2Rgb(backgroundActive));
		else
			g.setColor(hex2Rgb(backgroundColor));
		g.fillPolygon(buttonPoly);
		drawEffect(g);
		if(active)
			g.setColor(hex2Rgb(backgroundColor));
		else
			g.setColor(hex2Rgb(textColor));
		g.setFont(new Font("Roboto", Font.PLAIN, 24));
		if(left)
			drawTextRight(g);
		else
			drawTextLeft(g);
	}
}
