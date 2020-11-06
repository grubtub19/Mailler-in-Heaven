
// Worm.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* Contains the worm's internal data structure (a circular buffer)
   and code for deciding on the position and compass direction
   of the next worm move.
 */

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;


public class Player extends Entity
{
	private double movingTheta;

	private Sword sword;						//currently equipped sword
	private ArrayList<CrossHairs> cross = new ArrayList<CrossHairs>(3);
	private GunControllerC gunController;
	private int inFrames = 60;					//number of invincibility frames
	private int inTimer = 0;					//counter used to know when to stop being invinsible
	private double dashTheta;					//the direction of the next or current dash in radians
					//the direction of the sword's swing in degrees
	private int dashTimer = 0;					//counter used to know how far you are into the dash
	private int dashDistance = 140;				//how far each dash goes
	private int dashTime = 14;					//the timer to travel the full dashDistance
	private int dashPauseTime = 4;				//how long (frames) will the player pause between dashes
	private int dashSmoothL = 6;				//how many frames before the pause time the player can input a dash without it failing
	private int dashSmoothR = 6; 				//how many frames after the pause time the player can input a dash without it failing
	private boolean dashFail = false;			//keeps track of whether or not the next dash will fail
	private int dashRestartTimer = 0;			//counter used to know how far into the dashRestartTimer you are
	private int dashRestartTime = 10;			//how long after a failed dash you have until you can successfully input a new dash
	private boolean qDash = false;				//keeps track of if there was a valid dash input made within dashSmoothL so dash() will know to start a new dash once the pause time starts
	private int dashCountForSlide = 0;			//counter used to know how many dashes you have successfully chained (what number dash you are on). Used to determine how fast to launch your slide.
	private int maxDashCountForSlide = 12;		//the maximum value for dashCountForSlide (used so the slide does not infinitely get bigger)
	private int dashCountForSpeed = 0;			//counter used to know how many dashes you have successfully chained (what number dash you are on). Each successful dash results in a shorter effective dashTime when called in dash()
	private int maxDashCountForSpeed = 1;		//the maximum value for dashCountForSpeed
	private boolean sliding = false;			//whether or not you are still sliding
	private boolean sliding2 = false;
	private double slideResistance = .8;		//how much friction you have with the ground while sliding
	private double slideLaunchSpeed = 26;		//base slide launch speed out of your last dash
	private double inputSpeed = 0;				//magnitude of the input 0 - 1
	private double pointTheta = 0;				//direction the input is pointing
	private double inputDirection;				//another version for a slightly different purpose
	private double inputMagnitude;				//another version for a slightly different purpose
	private boolean aiming = false;				//aiming
	private boolean firstRight = true;			//used so you dont press a button 60 times in a second
	private boolean firstA = true;				//ditto
	private boolean firstB = true;				//ditto
	private boolean firstX = true;				//ditto
	private boolean sLiDe = false;				//ditto
	StripImageC mailler;
	StripImageC swoosh;
	private int maillerFacingDir;
	private int swooshTimer = 0;
	private int swooshTime = 10;				//timer for the sword animation
	private int swooshNum = -1;
	private Point swooshPos;
	private StripImageC crossImage = new StripImageC("cross.png", 2);
	private ArrayList<AudioClip> swish = new ArrayList<AudioClip>(9);
	private AudioClip oof = new AudioClip("oof.wav");
	private boolean justSwooshed = false;
	

	/**
	 * Creates a player character. Also constructs a sword linked to the character.
	 * @param xArr			double[] of x values for the character's polygon
	 * @param yArr			double[] of y values for the character's polygon
	 * @param x				x-coordinate of where to spawn the character
	 * @param y				y-coordinate of where to spawn the character
	 * @param sxArray		double[] of x values for the sword's polygon
	 * @param syArray		double[] of y values for the sword's polygon
	 * @param snArray		length of the sword's arrays
	 * @param sTime			time for a full sword swing
	 * @param sWidthTheta	radius of a full sword swing
	 * @param aD			attack damage of the sword
	 */
	public Player(NotHyrule nH, double[] xArr, double[] yArr, double x, double y, double[] sxArray, double[] syArray, int snArray, int length, double sTime, double sWidthTheta, int aD) {
		super(nH, xArr, yArr, x, y);
		sword = new Sword(this, sxArray, syArray, snArray, sTime, sWidthTheta, aD);
		cross.add(new CrossHairs(nH, this, length));
		cross.add(new CrossHairs(nH, this, length-6));
		//cross.add(new CrossHairs(nH, this, length-8));
		gunController = new GunControllerC(nH, this, 4, 6, 40, true, 10, false);
		currHP = 1600;
		maxHP = 1600;
		healthBar = new HealthBar(this, 70);
		//mailler = new StripImage("maillerstrip.png", 4);
		mailler = new StripImageC("maillerstrip2.png", 8);
		swoosh = new StripImageC("swoosh2.png", 32);
		swooshPos = new Point(0,0);
		for(int i = 1; i<10; i++) {
			swish.add(new AudioClip("swish" + i + ".wav"));
		}
		
	}

	/**
	 * returns the direction of input
	 * @return theta
	 */
	public double getInputMagnitude() {
		return inputMagnitude;
	}
	/**
	 * direction on the controller
	 * @return	theta in degrees
	 */
	public double getInputDirection() {
		return inputDirection;
	}
	
	public double getShootingDirection() {
		return inputDirection;
	}
	
	/**
	 * returns the direction of input
	 * @return theta
	 */
	public double getPointTheta() {
		return pointTheta;
	}
	
	/**
	 * Sets the sword to the parameter
	 * @param sword	the new sword
	 */
	public void setSword(Sword sword) {
		this.sword = sword;
	}
	
	/**
	 * returns the sword from this character
	 * @return	sword
	 */
	public Sword getSword() {
		return sword;
	}
	
	public GunControllerC getCurrGun() {
		return gunController;
		
	}
	
	/**
	 * timer for calculating invincibility
	 */
	
	private void calcInvincibility() {
		if(inTimer > 0 && inTimer < inFrames) {
			inTimer++;
		} else if (inTimer >= inFrames) {
			inTimer = 0;
		}
	}
	
	/**
	 * update the crosshairs location
	 */
	private void updateCross() {
		for(int i = 0; i< cross.size(); i++) {
			cross.get(i).update();
		}
	}
	
	/**
	 * draws the crosshairs
	 * @param g
	 */
	private void drawCross(Graphics g) {
		for(int i = 0; i< cross.size(); i++) {
			cross.get(i).draw(g);
		}
	}
	
	/**
	 * returns whether or not the character is invulnerable
	 * @return boolean
	 */
	public boolean isInvulnerable() {
		return !(inTimer == 0);
	}
	
	/**
	 * Handles the character taking damage. Shows the healthbar. Subtracts the character's HP. 
	 * @param aD	the attack damage of whatever hit this character
	 * @return		true if the player died
	 */
	public boolean hit(int aD) {
		if(!isInvulnerable()) {

		}
		if(inTimer == 0 & !isDashing()) {
			if (currHP - aD <= 0) {
				return true;
			} else {
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
	 * checks if it is in the middle of a dash
	 * @return
	 */
	public boolean isDashing() {
		if(dashTimer > 0)
			return true;
		else
			return false;
	}

	public boolean willSlide() {
		return dashCountForSlide > 3;
	}
	
	/**
	 * checks if you are sliding
	 * @return
	 */
	public boolean isSliding() {
		if(sliding || sliding2) 
			return true;
		else
			return false;
	}
	
	/**
	 * sets the momentum to the max
	 */
	public void setDCFS() {
		dashCountForSlide = maxDashCountForSlide;
	}

	/**
	 * called when you input a dash command, then makes a decision of what to do at different times in the dash
	 */
	public void startDash() {
		sliding2 = false;
		//System.out.println("dashTimer = " + dashTimer);
		//System.out.println("dashFail = " + dashFail);
		//System.out.println("dashResetTimer = " + dashRestartTimer);
		if(dashRestartTimer == 0 && !dashFail) {
			if(dashTimer == 0) { 									//if (not in a dash && previous dash wasn't a failure && dash isn't on cooldown)
				//dashTheta = pointTheta;												//save the dash angle on the same frame just before we call dash()
				dashTimer = 1;//start the dash
				//System.out.println("starting dashTimer");
			} else if ((dashTimer < dashTime - dashCountForSpeed - dashSmoothL && dashTimer > 0) || qDash) {		//if inside of a dash
				//System.out.println("dashFailed");
				qDash = false;
				dashFail = true;																		//the next dash will fail
			} else if (dashTimer >= dashTime - dashCountForSpeed - dashSmoothL && dashTimer < dashTime - dashCountForSpeed + dashPauseTime + dashSmoothR) {		//if in the correct window																		//queue next dash
				//dashTheta = Math.toRadians(inputDirection - 90);
				qDash = true;																			//queue the next dash
				if(dashCountForSlide < maxDashCountForSlide)											
					dashCountForSlide++;																//increment slide counter
				if(dashCountForSpeed < maxDashCountForSpeed)
					dashCountForSpeed++;
			} else if (dashTimer >= dashTime - dashCountForSpeed + dashPauseTime) {		//if after the pause time
				//dashTheta = Math.toRadians(inputDirection - 90);												//save the dash angle 
				dashTimer = 1;																			//start the next dash
				qDash = false;																			//reset qDash															//increment speed counter
			}
		}
	}
	
	/**
	 * This calculates the translation of the dash
	 */
	private void dash() {
		//System.out.print(dashTimer + " ");
		//System.out.println("dashTimer: " + dashTimer + ", dashTime - dashCountForSpeed + dashPauseTime + dashSmoothR: " + (dashTime - dashCountForSpeed + dashPauseTime + dashSmoothR));
		if(dashTimer < dashTime - dashCountForSpeed) {	
			double x;
			double y;
			if(dashTimer < dashTime-dashCountForSpeed*0.5) {
				
				if((dashTime-dashCountForSpeed)%2 != 0) {
					double small = ((dashTime-dashCountForSpeed)/2);
					double change = ((dashTime-dashCountForSpeed) - ((small+1)*0.7))/small;
					x = change * dashDistance/(dashTime - dashCountForSpeed) * Math.cos(dashTheta);		//measure x and y distance the same distance every time
					y = change * dashDistance/(dashTime - dashCountForSpeed) * Math.sin(dashTheta);
				} else {
				x = 1.2 * dashDistance/(dashTime - dashCountForSpeed) * Math.cos(dashTheta);		//measure x and y distance the same distance every time
				y = 1.2 * dashDistance/(dashTime - dashCountForSpeed) * Math.sin(dashTheta);
				}//if in dash movement
			} else {
				x = 0.8 * dashDistance/(dashTime - dashCountForSpeed) * Math.cos(dashTheta);		//measure x and y distance the same distance every time
				y = 0.8 * dashDistance/(dashTime - dashCountForSpeed) * Math.sin(dashTheta);
			}
			translate(x,y);																//move in dashTheta direction
			dashTimer++;
		} else if (dashTimer < dashTime - dashCountForSpeed + dashPauseTime) {					//if in pause
			dashTimer++;																		//do nothing
		} else if (dashTimer >= dashTime - dashCountForSpeed + dashPauseTime && qDash && !dashFail) {		//if in the window of time after the pause and the next dash is queued
			//System.out.println("---__---");
			qDash = false;
			dashTimer = 0;
			startDash();																		//start the next dash
		} else if (dashTimer >= dashTime - dashCountForSpeed + dashPauseTime && dashTimer <= dashTime - dashCountForSpeed + dashPauseTime + dashSmoothR && !qDash) {							//if in the window of time after the pause and no dash has been queued yet
			dashTimer++;
			super.updateVel();																	//let the player move around normally
			if(sword.isSwinging())
				dashFail = true;
		} else if (dashTimer > dashTime - dashCountForSpeed + dashPauseTime + dashSmoothR) {	//if time is up and you stopped dashing
			qDash = false;
			dashRestartTimer = 1;
			dashFail = false;																	//reset variables
			sliding = true;																		//start sliding
			dashTimer = 0;
		}
	}
	
	/**
	 * a short function you can call to stop in the middle of a dash and start sliding.
	 */
	public void startSlide() {
		//System.out.println("dashTimer == " + dashTimer);
		if(dashTimer > 0) {
			qDash = false;
			sLiDe = true;
			dashRestartTimer = 0;
			dashFail = false;
			sliding2 = true;
			//System.out.println("Sliding2");
			dashTimer = 0;
		}
	}

	public void stopDash() {
		if(dashTimer > 0) {
			qDash = false;
			sLiDe = false;
			dashRestartTimer = 0;
			dashFail = false;
			sliding2 = false;
			dashTimer = 0;
		}
	}
	/**
	 * sets the sliding to true
	 */
	public void setSliding() {
		sliding = true;
	}
	
	/**
	 * this type of sliding you can aim in
	 */
	public void setSliding2() {
		sliding2 = true;
	}
	
	/**
	 * starts the sword animation
	 */
	public void startSwoosh() {
		boolean active = false;
		for(AudioClip currSwoosh: swish) {
			if(currSwoosh.isActive()) {
				active = true;
			}
		}
		if(swooshNum == -1) {
			justSwooshed = false;
		}
		//System.out.println(swooshNum + " --- " + justSwooshed);
		if(swooshNum >= 0 && !active && !justSwooshed) {
			int num = (int)Math.floor(Math.random() * 9);
			//System.out.println("playing : " + num );
			swish.get(num).play(false);
			justSwooshed = true;
		}
		
	}
	
	private void calcInputs(LinkedList<Integer> justPressed, LinkedList<Integer> pressed, LinkedList<Integer> justReleased, double direction, double magnitude, int butA, int butB, int butX, boolean leftTrigger, boolean rightTrigger) {
		boolean yInput = false;								//tracks if a y-input was made on that frame
		boolean xInput = false;		
		/*//tracks if an x-input was made on that frame
		for (int j = 0; j < justReleased.size(); j++) {
			switch (justReleased.get(j)) {					//handles what happens when a key is released
				case KeyEvent.VK_W:
					yAccel = 0;
					break;
				case KeyEvent.VK_D:
					xAccel = 0;
					break;									
				case KeyEvent.VK_S:
					yAccel = 0;
					break;
				case KeyEvent.VK_A:
					xAccel = 0;
					break;
				case KeyEvent.VK_SPACE: 					//this was supposed to stop you from swinging the sword while you dash, but looking back at it, It really doesn't work
					sword.swingable = true;
			}
		}
		*/
	//System.out.println(direction);
	//System.out.println(magnitude);
	inputDirection = direction;	
	inputMagnitude = magnitude;
	//System.out.println(inputMagnitude);
	inputSpeed = magnitude * topSpeed;
	pointTheta = Math.toRadians(direction -90);
	xAccel = 2*magnitude*Math.cos(Math.toRadians(direction -90))/4;
	yAccel = 2*magnitude*Math.sin(Math.toRadians(direction -90))/4;
	//System.out.println(butX);
	
	if(butA == 1 && firstA) {
		//System.out.println("1");
		if(!sword.isSwinging() && !sliding) {
			dashTheta = pointTheta;
			//System.out.println("startDash()");
			startDash();
		}
		firstA = false;
	}//tries to start a dash
	if(butA == 0 && !firstA) {
		firstA = true;
	}
	
	if(butB == 1 && firstB) {
		gunController.changeType();
		gunController.setBarTimer(1);
		firstB = false;
	}
	if(butB == 0 && !firstB) {
		firstB = true;
	}
	
	if(butX == 1 && firstX && !sliding) {
		if(dashTimer == 0 || (dashTimer >= dashTime - dashCountForSpeed + dashPauseTime && dashTimer <= dashTime - dashCountForSpeed + dashPauseTime + dashSmoothR) && dashCountForSlide <= 2 ) {
		sword.swing(this);
		firstX = false;
		}
	}
	if(butX == 0 && !firstX) {
		firstX = true;
	}
	
	if(leftTrigger && !sword.isSwinging() && dashTimer == 0) {
		aim();
	}
	if(!leftTrigger) {
		aiming = false;
	}
	if(!rightTrigger && !firstRight) {
		firstRight = true;
	}
	if(rightTrigger && firstRight) {
		gunController.fire();
		sliding2 = true;
		firstRight = false;
	}
	
	
		
	
		/*
		for (int i = 0; i < pressed.size(); i++) {			//handles currently pressed buttons
			switch (pressed.get(i)) { 
				case KeyEvent.VK_W:
					setYAccel(-accelSpeed);
					yInput = true;
					break;
				case KeyEvent.VK_D:
					setXAccel(accelSpeed);
					xInput = true;
					break;									//sets the acceleration in the 4 directions of WASD
				case KeyEvent.VK_S:							//in the future, I would like to have controller inputs here too.
					setYAccel(accelSpeed);
					yInput = true;
					break;
				case KeyEvent.VK_A:
					setXAccel(-accelSpeed);
					xInput = true;
					break;
			}	
		}

		
		if(!yInput && !sliding) {							//if no input in y and isn't sliding
			resistY(resistance);							//slowly sets the y velocity to 0
		}
		if(!xInput && !sliding) {							//if no input in x and isn't sliding
			resistX(resistance);							//slowly sets the x velocity to 0
		}*/
	}
	
	/**
	 * aims and stands still
	 */
	private void aim() {
		aiming = true;
		gunController.setBarTimer(1);
	}
	
	/*
	 * updates the velocity. Does checks for whether it is swinging or if it is dashing and calculates the velocity at every frame
	 */
	public void updateVel() {
		calcInvincibility();														
		
		/*
		if(dashTimer == 0 && !sliding) {											//not in the middle of a dash or a slide
			xVel += xAccel;
			yVel += yAccel;
			double distance = Math.sqrt(xVel*xVel+yVel*yVel);
			if(distance > topSpeed) {
				double directionTheta = Math.atan2(yVel, xVel);
				yVel = Math.sin(directionTheta)*topSpeed;
				xVel = Math.cos(directionTheta)*topSpeed;					//update the x and y velocities
			}
			if(distance > inputSpeed) {
				resist(resistance);
			}
			if(sword.isSwinging()) {												
				xVel = 0;
				yVel = 0;																//no moving while the sword is swinging
				xAccel = 0;
				yAccel = 0;
			}
			if (!(xAccel == 0 && yAccel == 0)) {										//if moving direction is changing
				facingTheta = Math.toDegrees(Math.atan2(yAccel, xAccel));			//update the facing direction
				
			}
		} else if (dashTimer > 0) {													//if in the middle of a dash or a slide and 
			if (!(xAccel == 0 && yAccel == 0)) {										//calculate the facing theta
				facingTheta = Math.toDegrees(Math.atan2(yAccel, xAccel));
				}
			xVel = 0;
			yVel = 0;																	//no normal movement while in a dash or a slide
			dash();																		//continue dash
		}
		if(dashRestartTimer > 0) {													//if in dash cooldown
			if(dashRestartTimer == 1 && dashCountForSlide > 2) {					//if first frame of slide (note, you only slide after 4 consecutive dashes, dashCountForSlide increments 2 behind the actual dash count)
				xVel = slideLaunchSpeed * (1 + dashCountForSlide/10) * Math.cos(dashTheta);		//calculate the launch speed taking dash count into consideration
				yVel = slideLaunchSpeed * (1 + dashCountForSlide/10) * Math.sin(dashTheta);
			}
			if(dashRestartTimer > dashRestartTime) {									//if the cooldown is over
				dashRestartTimer = 0;
				dashCountForSlide = 0;													//reset variables
				dashCountForSpeed = 0;
			} else {
				dashRestartTimer++;														//else increment the cooldown timer
			}
		}
		if(sliding) {																	//if the player is sliding
			if((xVel < .01 && xVel > -.01) && (yVel < .01 && yVel > -.01)) {			//stop sliding if you as going slow enough
				sliding = false;
			}																			
			if(xVel > 5 || xVel < -5 || yVel > 5 || yVel < -5) {						//if you are going faster than 5, slow down rapidly
				resist(slideResistance);
			} else {
				resist(slideResistance/4);												//if you are going slower than 5, slow down slowly
			}
		}*/
		
		//System.out.println(sliding);
		
		if(aiming && !sliding) {
			resist(resistance/2);
		}
		else if(!sliding && !sliding2) {
			if (dashTimer > 0) {
				yVel = 0;
				xVel = 0;
				dash();
			}
			else if (sword.isSwinging()) {
				
			}
			else {
				xVel += xAccel;
				yVel += yAccel;
				double distance = Math.sqrt(xVel*xVel+yVel*yVel);
				if(distance > topSpeed) {
					double directionTheta = Math.atan2(yVel, xVel);
					yVel = Math.sin(directionTheta)*topSpeed;
					xVel = Math.cos(directionTheta)*topSpeed;					//update the x and y velocities
				}
				if(distance > inputSpeed) {
					resist(resistance);
				}
			}
		}
		if (sliding || sliding2 || dashRestartTimer > 0) {
			//System.out.println("Sliding");
			if(dashRestartTimer > 0 || sLiDe) {													//if in dash cooldown
				if((dashRestartTimer == 1 && dashCountForSlide > 2) || sLiDe) {					//if first frame of slide (note, you only slide after 4 consecutive dashes, dashCountForSlide increments 2 behind the actual dash count)
					//System.out.println("1!");
					if(dashCountForSlide <= 2 && sLiDe) {
						xVel = slideLaunchSpeed * ( .2) * Math.cos(dashTheta);		//calculate the launch speed taking dash count into consideration
						yVel = slideLaunchSpeed * ( .2) * Math.sin(dashTheta);
					} else {
					
					xVel = slideLaunchSpeed * ( dashCountForSlide/12.0) * Math.cos(dashTheta);		//calculate the launch speed taking dash count into consideration
					yVel = slideLaunchSpeed * ( dashCountForSlide/12.0) * Math.sin(dashTheta);
					}
					sLiDe = false;
				}
				if(dashRestartTimer > dashRestartTime) {									//if the cooldown is over
					//System.out.println("2!");
					dashRestartTimer = 0;
					dashCountForSlide = 0;													//reset variables
					dashCountForSpeed = 0;
				} else {
					//System.out.println("3!");
					dashRestartTimer++;														//else increment the cooldown timer
				}
			}
			
			if((xVel < .01 && xVel > -.01) && (yVel < .01 && yVel > -.01)) {			//stop sliding if you as going slow enough
				sliding = false;
				sliding2 = false;
			}																			
			if(xVel > 5 || xVel < -5 || yVel > 5 || yVel < -5) {						//if you are going faster than 5, slow down rapidly
				resist(slideResistance);
			} else {
				resist(slideResistance/4);												//if you are going slower than 5, slow down slowly
			}
		}
		
	}
	
	private void updateImage() {
		//if((xVel * xVel + yVel * yVel) > 0.5)
		//	facingTheta = Math.toDegrees(Math.atan2(yVel, xVel));
		
		double degTheta = Math.toDegrees(pointTheta);
		//System.out.println("theta = " + degTheta);
		if(swooshTimer == 0) {
			if(degTheta > 135 && degTheta < 255) {
				maillerFacingDir = 2;
			} else if(degTheta > 45 && degTheta < 135) {
				maillerFacingDir = 1;
			} else if(degTheta > -45 && degTheta < 45) {
				maillerFacingDir = 0;
			} else if(degTheta > 255 || degTheta <-45) {
				maillerFacingDir = 3;
			}
		}
		if(sword.isSwinging() || swooshTimer > 0) {
			if(swooshTimer < 2) {
				swooshNum = 0;
				swooshTimer++;
			} else if (swooshTimer < 5) {
				swooshNum = 1;
				swooshTimer++;
			} else if (swooshTimer < 8) {
				swooshNum = 2;
				swooshTimer++;
			} else if (swooshTimer < 12) {
				swooshNum = 3;
				swooshTimer++;
			} else {
				swooshNum = -1;
				swooshTimer = 0;
			}
			if(maillerFacingDir == 0) {
				swoosh.setTopLeftOffset((int)(getCenterPosX()+20-swoosh.getWidth()/2.0), (int)(getCenterPosY()-20-swoosh.getHeight()/2.0));
			} else if (maillerFacingDir == 1) {
				swoosh.setTopLeftOffset((int)(getCenterPosX()-swoosh.getWidth()/2.0), (int)(getCenterPosY()-20+20-swoosh.getHeight()/2.0));
			} else if (maillerFacingDir == 2) {
				swoosh.setTopLeftOffset((int)(getCenterPosX()-20-swoosh.getWidth()/2.0), (int)(getCenterPosY()-20-swoosh.getHeight()/2.0));
			} else if (maillerFacingDir == 3) {
				swoosh.setTopLeftOffset((int)(getCenterPosX()-swoosh.getWidth()/2.0), (int)(getCenterPosY()-20-20-swoosh.getHeight()/2.0));
			}
		}
	}
	
	/*
	 * updates everything about the character every frame
	 */
	public void update(LinkedList<Integer> justPressed, LinkedList<Integer> pressed, LinkedList<Integer> justReleased, double direction, double magnitude, int butA, int butB, int butX, boolean leftTrigger, boolean rightTrigger) {
		//System.out.println("dashTimer = " + dashTimer + ", dashFail = " + dashFail + ", DashRestartTimer = " + dashRestartTimer + ", qDash = " + qDash + ", dCFS = " + dashCountForSlide + ", sliding = " + sliding + ", sliding2 = " + sliding2 + ", sLiDe = " + sLiDe);
		calcInputs(justPressed, pressed,  justReleased, direction, magnitude, butA, butB, butX, leftTrigger, rightTrigger);									//calculate the acceleration from the inputs
		updateVel();																		//update the velocity based on those inputs. Also takes care of dashing/sliding/sword swinging movement
		//System.out.println("xVel = " + xVel + ", yVel = " + yVel);
		translate(xVel, yVel);																//move xArray and yArray
		setPolygon(xArray, yArray);															//make a new polygon at those points (i dont think this is the best way to do this)
		sword.calcSword(this);																//calculate the sword's position
		updateCross();
		gunController.update();
		healthBar.update();																	//update the healthbar
		updateImage();
		startSwoosh();
	}
	
	/**
	 * 
	 * draws the character, his sword, and his health bar
	 */
	public void draw(Graphics g)
	{
		g.setColor(Color.black);
		if(inTimer/2%2 == 1) {
			g.setColor(Color.BLUE);															//if in invinsibile, flash blue
		}
		g.fillPolygon(poly);
		g.setColor(Color.gray);
		if(sword.isSwinging()) {
			g.fillPolygon(sword.getXPoints(), sword.getYPoints(), sword.getXPoints().length);
		}
		if(aiming || gunController.isVisible()) {
			System.out.println("showed the gunController");
			for(CrossHairs crossHair: cross) {
				crossImage.setOffset((int)crossHair.getCenterPosX(), (int)crossHair.getCenterPosY());
				if(gunController.getState() > 0) {
					crossImage.drawFadedImage(g, 0, (float)0.6);
				} else {
					crossImage.drawFadedImage(g, 1, (float)0.6);
				}
			}
			/*for(int i = 0; i < cross.size(); i++) {
				crossImage.setOffset((int)cross.get(i).getCenterPosX(), (int)cross.get(i).getCenterPosY());
				if(gunController.getState() > 0) {
					crossImage.drawFadedImage(g, 0, (float)0.6);
				} else {
					crossImage.drawFadedImage(g, 1, (float)0.6);
				}
			}*/
			//drawCross(g);
			gunController.draw(g);
		}  else {
			System.out.println("gunController wasn't visible");
			System.out.println("gunController.showTimer = " + gunController.showTimer);
		}
		
		gunController.drawBar(g);
		healthBar.draw(g);
		if(swooshNum >= 0) {
			
			if(sword.isRight()) {
				//System.out.println("swooshNum = " + swooshNum + ", maillerFacingDir = " + maillerFacingDir + " = " + (swooshNum+4*maillerFacingDir));
				swoosh.draw(g, swooshNum+4* maillerFacingDir);
			} else {
				//System.out.println("swooshNum = " + swooshNum + ", maillerFacingDir = " + maillerFacingDir + " = " + (swooshNum+4*maillerFacingDir+16));
				swoosh.draw(g, swooshNum+4* maillerFacingDir +16);
			}
		}
		
		mailler.setTopLeftOffset((int)(getCenterPosX()-mailler.getWidth()/2.0), (int)(getCenterPosY()-mailler.getHeight()+getHeight()/2));
		
		if(inTimer > 0 && nH.isHell()) {
			if(sword.isSwinging()) {
				mailler.drawFadedImage(g, maillerFacingDir + 4, (float)0.5);
			} else {
				mailler.drawFadedImage(g, maillerFacingDir, (float)0.5);
			}

		} else {
			if(sword.isSwinging()) {
				mailler.draw(g, maillerFacingDir + 4);
			} else {
				mailler.draw(g, maillerFacingDir);
			}
		}
		if(sword.isSwinging() && maillerFacingDir == 1) {
			//g.fillPolygon(sword.getXPoints(), sword.getYPoints(), sword.getXPoints().length);
		}
	}  // end of draw()

}  // end of Worm class

