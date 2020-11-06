import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Monster extends Entity{
	private int aD;								//attack damage
	private int yellDistance = 2000;
	private int inFrames = 12;					//invinsibility frames count
	private int inTimer = 0;					//invinsibility frames timer
	private double aggroDistance = 2000;			//how far away they will aggro from
	private boolean isAggroed = false;
	private double knockbackSpeed = 3;			//velocity to be knocked back at
	private boolean knockback = false;			//if in knockback
	private int dazeTime = 10;
	private int dazeCounter = 0;			//knockback counter
	private int knockbackTime = 20;				//how long knockback lasts
	private int jellyness;						//how strongly monsters do not want to overlap
	ArrayList<Player> playerList;					//all of the links
	Entity link;									//the aggroed player
	int gunTimer = 0;
	int fireTime = 60;
	double fireTheta = 40 + Math.random() * 220;
	int stripNum;
	private double imageTheta;
	Timer idleTimer = new RandomTimer(20, 100);
	Timer waitTimer = new RandomTimer(20, 100);
	int idleDirection = (int)(Math.random()*3);
	int idleX;
	int idleY;
	AudioClip oof = new AudioClip("oof2.wav");

	/**
	 *
	 * @param nH
	 * @param hitbox
	 * @param health
	 * @param healthBar
	 * @param gunController
	 * @param stripImage
	 */
	public Monster(NotHyrule nH, Hitbox hitbox, HealthC health, HealthBar healthBar, GunControllerC gunController, StripImageC stripImage, RigidBody rigidBody) {
		super(nH, hitbox, health, healthBar);
		//images = new StripImageC("rarityStrip.png", 8);

		this.topSpeed = 10;
		this.accelSpeed = 1;
		link = playerList.get(0);
		aD = 30;
		jellyness = 1;

		// TODO combine into Entity?
		addComponent(rigidBody, RigidBody.class);
		addComponent(gunController, GunControllerC.class);
	}
	
	/**
	 * how far these enemies will aggro other enemies when hurt
	 * @return distance
	 */
	public int getYellDistance() {
		return yellDistance;
	}
	
	/**
	 * the direction this enemy is shooting
	 */
	public double getShootingDirection() {
		//System.out.println("1: " + getTheta(player));
		return Math.toDegrees(Math.atan2(yAccel, xAccel))+90;
	}
	
	/**
	 * the direction the enemy is pointing
	 */
	public double getPointTheta() {
		return getTheta(link);
	}
	
	/**
	 * if the enemy is being knockedback
	 * @return boolean
	 */
	public boolean isKnockback() {
		return knockback;
	}
	
	/**
	 * how strongly monsters do not want to overlap
	 * @return int jellyness
	 */
	public int getJellyness() {
		return jellyness;
	}
	
	/**
	 * the monster's attack damage
	 * @return int AD
	 */
	public int getAD() {
		return aD;
	}
	
	/**
	 * the direction to the aggroed player
	 * @return	double theta in radians
	 */
	private double getTheta(Entity e) {
		return Math.atan2(e.getCenterPosY() - getCenterPosY(), e.getCenterPosX() - getCenterPosX());
	}
	
	/**
	 * checks if it is invinsible
	 */
	private void calcInvincibility() {
		if(inTimer > 0 && inTimer < inFrames) {
			inTimer++;
		} else if (inTimer >= inFrames) {
			inTimer = 0;
		}
	}
	
	/**
	 * updates velocity taking whether or not it is in knockback into consideration
	 */
	public void updateVel() {
		if (knockback) {
			resist(resistance);
			//dazeCounter++;
			if((xVel < .01 && xVel > -.01) && (yVel < .01 && yVel > -.01)) {
				dazeCounter++;
			}
			if(dazeCounter > dazeTime) {
				dazeCounter = 0;
				knockback = false;
			}
		} else {
			xVel += xAccel;
			yVel += yAccel;
			double distance = Math.sqrt(xVel*xVel+yVel*yVel);
			if (distance > topSpeed) {
			double directionTheta = Math.atan2(yVel, xVel);
			yVel = Math.sin(directionTheta)*topSpeed;
			xVel = Math.cos(directionTheta)*topSpeed;
			}
		}
	}
	/**
	 * checks if a player is within a distance of it
	 */
	private void checkAggro() {	
		for (int i = 0; i < playerList.size(); i++) {
			if (Math.pow(aggroDistance, 2) > Math.pow(playerList.get(i).getCenterPosX() - getCenterPosX(), 2) + Math.pow(playerList.get(i).getCenterPosY() - getCenterPosY(), 2)){
				isAggroed = true;
				link = playerList.get(i);
			}
		}
	}
	
	public void setAggro(Entity link) {
		isAggroed = true;
		this.link = link;
	}
	
	/**
	 * stands still until it aggros onto player and then moves to his current location directly
	 */
	public void updateAccel() {	
		if (!isAggroed)
			checkAggro();
		if (isAggroed && !knockback) {
			double xDist = link.getCenterPosX() - getCenterPosX();
			double yDist = link.getCenterPosY() - getCenterPosY();
			double theta = getTheta(link);
			xAccel = accelSpeed*Math.cos(theta);
			yAccel = accelSpeed*Math.sin(theta);
			if (Math.pow(xDist, 2) + Math.pow(yDist, 2) < 400)
				resist(resistance);
		} else {
			resist(resistance);
		}
	}
	
	/**
	 * updates the horse sprite
	 */
	public void updateImage() {

		if(nH.isHell()) {
			if((xVel * xVel + yVel * yVel) > 0.5)
				imageTheta = Math.toDegrees(Math.atan2(yAccel, xAccel));
		} else {
			if((xVel * xVel + yVel * yVel) > 0.5)
				imageTheta = Math.toDegrees(Math.atan2(yVel, xVel));
		}
			//System.out.println("theta = " + imageTheta);
		if(imageTheta > 135 || imageTheta < -135) {
			if(nH.isHell())
				stripNum = 6;
			else
				stripNum = 2;
		} else if(imageTheta > 45 && imageTheta < 135) {
			if(nH.isHell())
				stripNum = 5;
			else
				stripNum = 1;
		} else if(imageTheta > -45 && imageTheta < 45) {
			if(nH.isHell())
				stripNum = 4;
			else
				stripNum = 0;
		} else if(imageTheta > -135 && imageTheta <-45) {
			if(nH.isHell()) 
				stripNum = 7;
			else
				stripNum = 3;
		}
		//imageNum = 
	}
	
	/**
	 * this will move the horse around like it is grazing
	 */
	public void updateIdle() {
		if(idleTimer > 0) {
			//System.out.println("idleTimer = " + idleTimer);
			if(idleTimer > maxIdleTime) {
				//System.out.println("idleTimer = " + idleTimer);
				maxWaitTime = (int)(20 + Math.random()*100);
				idleTimer = 0;
				waitTimer = 1;
			}
			else
				idleTimer++;
		} else {
			if(waitTimer > maxWaitTime) {
				//System.out.println("RESET");
				idleDirection = (int)(Math.random()*4);
				maxIdleTime = (int)(20 + Math.random()*100);
				idleTimer = 1;
				waitTimer = 0;
			}
			else
				waitTimer++;
		}
	}
	
	/**
	 * this sets the speed and direction of the horse when grazing
	 */
	private void updateIdleSpeed() {
		if(idleTimer > 0) {
			//System.out.println("idleDirection = " + idleDirection);
			switch (idleDirection) {
			case 0:
				xVel = 1;
				yVel = 0;
				break;
			case 1:
				xVel = -1;
				yVel = 0;
				break;
			case 2:
				xVel = 0;
				yVel = 1;
				break;
			case 3:
				xVel = 0;
				yVel = -1;
				break;
			}
		} else {
			xVel = 0;
			yVel = 0;
		}
	}
	
	/**
	 * this checks when the enemy should fire its gunController
	 */
	public void updateGunTimer() {
		if(inTimer == 0) {
			if(gunTimer > fireTheta && gunTimer < fireTheta + fireTime) {
				xVel = 0;
				yVel = 0;
				//System.out.println(gunTimer);
				gunTimer++;
			}
			else if(gunTimer >= fireTheta + fireTime) {
				gunController.fire();
				gunTimer = 0;
				//fireTheta = 100 + Math.random() *128;
			}
			else
				gunTimer += 1;
		} 
		else
			gunTimer = 0;
	}
	/**
	 * updates everything about the monster
	 */
	public void update() {
		
		if(nH.isHell()) {
			updateAccel();
			updateVel();
			updateImage();
			calcInvincibility();
			gunController.update();
			updateGunTimer();
			translate(xVel, yVel);
			setPolygon(xArray, yArray);
			healthBar.update();
		} else {
			updateIdle();
			updateIdleSpeed();
			//System.out.println("xVel = " + xVel + ", yVel  = " + yVel);
			translate(xVel, yVel);
			setPolygon(xArray, yArray);
			updateImage();
		}
		images.setOffset((int)getCenterPosX(), (int)getCenterPosY());
	}
	
	/**
	 * 
	 * @return	true if the monster is invulnerable
	 */
	public boolean isInvulnerable() {
		if(!(inTimer == 0)) {
			return true;
		}
		else
			return false;
	}
	/**
	 * hurts the monster and knocks it back. Monster may die.
	 * @param aD	int attack damage
	 * @return		true if the monster dies
	 */
	public boolean hit(Entity e, int aD) {
		if(!nH.isHell())
			nH.setHell(true);
		double theta = getTheta(e);
		knockback = true;																//sets knockback velocity
		xVel = knockbackSpeed * Math.cos((getTheta(e))-Math.PI);
		yVel = knockbackSpeed * Math.sin((getTheta(e))-Math.PI);
		if(inTimer == 0) {
			
			if (currHP - aD <= 0) {
				
				return true;
			}
			else {
				oof.play(false);
				healthBar.show();
				currHP = currHP - aD;
				inTimer += 1;
				return false;
			}
		} 
		return false;
	}
	
	/**
	 * the enemy is struck with damage
	 * @param theta	direction to knockback the enemy
	 * @param aD	damage inflicted
	 * @param kBM	knockback multiplier
	 * @return		whether the enemy died or not, true == dead
	 */
	public boolean hit(double theta, int aD, int kBM) {
		if(!nH.isHell())
			nH.setHell(true);
		knockback = true;																//sets knockback velocity
		xVel = kBM/10.0 * knockbackSpeed*Math.cos(Math.toRadians(theta)-Math.PI);
		yVel = kBM/10.0 * knockbackSpeed*Math.sin(Math.toRadians(theta)-Math.PI);
		if(inTimer == 0) {
			if (currHP - aD <= 0) {
				return true;
			}
			else {
				healthBar.show();
				currHP = currHP - aD;
				inTimer += 1;
				return false;
			}
		} 
		return false;
	}
	
	/**
	 * draws the monster and its healthbar
	 */
	public void draw(Graphics g)
	{
		g.setColor(Color.red);
		if(inTimer/2%2 == 1) {			//flashes orange if invinsible
			g.setColor(Color.ORANGE);
		}
		
		g.fillPolygon(poly.xpoints, poly.ypoints, poly.xpoints.length);
		if(inTimer > 0 && nH.isHell()) {
			images.drawFadedImage(g, stripNum, (float)0.5);
		} else {
			images.draw(g, stripNum);
		}
		if(nH.isHell()) {
			healthBar.draw(g);
			gunController.draw(g);
		}
	}
}
