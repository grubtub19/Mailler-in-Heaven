//TODO Convert

public class CrossHairs extends Entity {
	int length;
	Player player;
	double lastDir;
	static double[] xA = { 100,110,110,100 };
	static double[] yA = { 100,100,110,110 };
	

	public CrossHairs(NotHyrule nH, Player player, int length) {
		super(nH, xA, yA, player.getCenterPosX(), player.getCenterPosY());
		this.length = length;
		this.player = player;
	}
	
	public double getPointTheta() {
		return lastDir;
	}
	
	public double getShootingDirection() {
		return lastDir;
	}
	
	private void calcPos() {

		double mag = player.getInputMagnitude()  * 60;
		if(mag < 30) {
			mag = 30;
		}
		translate(player.getCenterPosX() - getCenterPosX() + (mag * length * Math.cos(Math.toRadians(player.getInputDirection()-90))),
				  player.getCenterPosY() - getCenterPosY() + (mag * length * Math.sin(Math.toRadians(player.getInputDirection()-90))));
		setPolygon(xArray,yArray);
	}
	
	public void update() {
		calcPos();
	}
}
