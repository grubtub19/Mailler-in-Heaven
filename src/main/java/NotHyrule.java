// WormChase.java
// Roger Mailler, January 2009, adapted from
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* A worm moves around the screen and the user must
 click (press) on its head to complete the game.

 If the user misses the worm's head then a blue box
 will be added to the screen (if the worm's body was
 not clicked upon).

 A worm cannot move over a box, so the added obstacles
 *may* make it easier to catch the worm.

 A worm starts at 0 length and increases to a maximum
 length which it keeps from then on.

 A score is displayed on screen at the end, calculated
 from the number of boxes used and the time taken. Less
 boxes and less time mean a higher score.

 -------------

 Uses full-screen exclusive mode, active rendering,
 and double buffering/page flipping.

 On-screen pause and quit buttons.

 Using Java 3D's timer: J3DTimer.getValue()
 *  nanosecs rather than millisecs for the period

 Average FPS / UPS
 20			50			80			100
 Win 98:         20/20       50/50       81/83       84/100
 Win 2000:       20/20       50/50       60/83       60/100
 Win XP (1):     20/20       50/50       74/83       76/100
 Win XP (2):     20/20       50/50       83/83       85/100

 Located in /WormFSEM

 Updated: 12th Feb 2004
 * added extra sleep to the end of our setDisplayMode();

 * moved createBufferStrategy() call to a separate
 setBufferStrategy() method, with added extras
 ----
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;

public class NotHyrule extends GameFrame {

	public ArrayList<Monster> monsterList; 	//list of all Monsters in the scene
	public ArrayList<Player> playerList;			//list of all playable characters in the scene
	public ArrayList<Wall> wallList;			//list of all Walls in the scene
	public ArrayList<Rect> rectList;			//list of all Rects in the scene
	public ArrayList<BulletFactory> bulletList;		//list of all Bullets in the scene
	private Menu mainMenu;							//main menu
	private Menu subMenu;							//level fail menu
	private static int state = 0;				//menu vs level
	private static final long serialVersionUID = -2450477630768116721L;

	private static int DEFAULT_FPS = 60;

	private int score = 0;
	private Font font;
	private FontMetrics metrics;
	
    private DecimalFormat df = new DecimalFormat("0.##");  // 2 dp
    
    private boolean hell = false;				//changes atmosphere
    private Image back;							//the background of the menu

	private MusicManager music;
    private int levelNum = 0;					//current level you are on
    private int maxLevel = 2;					//so it can tell when you finished the last level

	public NotHyrule(long period) {
		super(period);
	}

	@Override
	protected void simpleInitialize() {
		//pauseArea = new Rectangle(1800, 1000, 40, 40);
		//quitArea = new Rectangle(1860, 1000, 40, 40);
		font = new Font("SansSerif", Font.BOLD, 24);
		metrics = this.getFontMetrics(font);

		try {
			device = XInputDevice.getAllDevices()[0];
		} catch(XInputNotLoadedException  e) {
			System.out.println("error: " + e);
		}
		components = device.getComponents();
		buttons = components.getButtons();
		axes = components.getAxes();
		music = new MusicManager();
		playerList = new ArrayList<Player>(1);
		monsterList = new ArrayList<Monster>();					//initialize all variables
		wallList = new ArrayList<Wall>();
		rectList = new ArrayList<Rect>();
		bulletList = new ArrayList<BulletFactory>();

		loadMenu();
	}
	/**
	 * These are all self explanatory
	 */
	protected void unloadMainMenu() {
		mainMenu = null;
	}

	protected void unloadSubMenu() {
		subMenu = null;
	}
	
	protected void unloadLevel() {
		playerList.clear();
		monsterList.clear();
		wallList.clear();
		rectList.clear();
		bulletList.clear();
	}

	/**
	 * returns hell
	 * @return hell
	 */
	public boolean isHell() {
		return hell;
	}
	
	/**
	 * sets hell
	 * @param isHell if true, it will be hell
	 */
	public void setHell(boolean isHell) {
		if(isHell) {
			music.changeSong(SongTitle.HELL, true,false);
			back = new Image("hellFloor.png");
			hell = true;
		} else {
			music.changeSong(SongTitle.HAPPY, true, true);
			back = new Image("grass.png");
			hell = false;
		}
	}
	
	/**
	 * loads the main menu
	 */
	protected void loadMenu() {
		setHell(false);
		levelNum = 0;			//main menu
		unloadLevel();
		unloadSubMenu();
		mainMenu = new Menu(this, 0);
		state = 0;
	}
	
	/**
	 * loads the lose menu
	 */
	protected void loadMenu2(int menuNum) {
		switch(menuNum) {
			case 1:
				music.changeSong(SongTitle.LOSER,false,false);
				subMenu = new Menu(this, 1);
				break;
			case 2:
				subMenu = new Menu(this,2);
				break;
			case 3:
				subMenu = new Menu(this, 3);
		}
												//lose level menu
	}
	
	/**
	 * loads the "to next level" menu
	 */
	//protected void loadMenu3() {
	//	menu3 = new Menu(this, 2);										//win level menu
	//}
	
	/**
	 * loads the winning screen
	 */
	//protected void loadWin() {
	//	menu4 = new Menu(this, 3);
	//}
	
	/**
	 * changes between level vs menu
	 * @param num 0 == menu, 1 == level
	 */
	public void changeState(int num) {
		state = num;
		if(num == 1) {
			loadLevel1();
		}
	}
	
	/**
	 * places collision walls on the screen edges
	 */
	private void loadBorderWalls() {
		int[] xWArray = { 0, 1920, 1920, 0 };
		int[] yWArray = { 0, 0, -40, -40 };
		Point2D.Double pointA = new Point2D.Double(800, 1);
		Point2D.Double pointB = new Point2D.Double(200, 1);
		wallList.add(new Wall(xWArray, yWArray, pointA, pointB));

		int[] xWArray2 = { -40, 0, 0, -40 };
		int[] yWArray2 = { 0, 0, 1080, 1080 };
		Point2D.Double pointA2 = new Point2D.Double(1, 0);
		Point2D.Double pointB2 = new Point2D.Double(1, 1000);
		wallList.add(new Wall(xWArray2, yWArray2, pointA2, pointB2));					//these are Walls that border the screen to keep you in view
		
		int[] xWArray3 = { 0, 1920, 1920, 0 };
		int[] yWArray3 = { 1080, 1080, 1120, 1120 };
		Point2D.Double pointA3 = new Point2D.Double(0, 1080);
		Point2D.Double pointB3 = new Point2D.Double(1920, 1080);
		wallList.add(new Wall(xWArray3, yWArray3, pointA3, pointB3));
		
		int[] xWArray4 = { 1920, 1960, 1960, 1920 };
		int[] yWArray4 = { 0, 0, 1080, 1080 };
		Point2D.Double pointA4 = new Point2D.Double(1920, 1080);
		Point2D.Double pointB4 = new Point2D.Double(1920, 0);
		wallList.add(new Wall(xWArray4, yWArray4, pointA4, pointB4));
	}
	
	/**
	 * loads the first level
	 */
	protected void loadLevel1() {
		unloadLevel();
		unloadMainMenu();
		unloadSubMenu();
		levelNum = 1;
		back = new Image("grass.png");
		
		double[] xArray = { 0, 30, 30, 0 };
		double[] yArray = { 0, 0, 30, 30 };											//these make a square
		double[] sxArray = { -40,0,40,80,40,0 };				//these make a sword
		double[] syArray = { 30,-0,0,30,90,90 };
		double swingTime = 6;														//I actually don't know what this is
		double iterTheta = 100;														//Same

		playerList.add(new Player(this, xArray, yArray, 700, 400, sxArray, syArray, sxArray.length, 10, swingTime, iterTheta, 5)); //you can actually make as many playable characters as you want
		
		//double[] xMArray = { 20,50,70,60,50,40,50,50,40,40,30,30,20,20,30,20,10,0 };	//these make a more complex scary monster shape that is cool, but does not work well with collisions
		//double[] yMArray = { 0,0,20,30,20,30,40,60,70,40,40,70,60,40,30,20,30,20 };
		double[] xMArray2 = { 0, 60, 60, 0 };											//these make a square
		double[] yMArray2 = { 0, 0, 60, 60 };
		double[] ex = { 0, 10, 10, 0 };													//these make a smaller square
		double[] ey = {0, 0, 10, 10 };
		/*
		monsterList.add(new Monster(this,xMArray2, yMArray2, 100, 600, 40, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 100, 610, 20, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 100, 620, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 100, 630, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 110, 600, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 110, 610, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 110, 620, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 110, 630, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 400, 100, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 400, 110, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 400, 120, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 400, 130, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 410, 100, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 410, 110, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 410, 120, 30, 3,playerList));					//this many monster lags the system pretty hard
		monsterList.add(new Monster(this,xMArray2, yMArray2, 410, 130, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1200, 200, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1200, 210, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1200, 220, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1200, 230, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1210, 200, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1210, 210, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1210, 220, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1210, 230, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1200, 600, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1200, 610, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1200, 620, 40, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1200, 630, 20, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1210, 600, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1210, 610, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1210, 620, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1210, 630, 30, 3,playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1220, 600, 30, 3,playerList));*/
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1220, 610, 40, 3, playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1220, 620, 40, 3, playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1220, 630, 40, 3, playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1200, 640, 40, 3, playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 200, 340, 40, 3, playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 230, 440, 40, 3, playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 260, 240, 40, 3, playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 290, 340, 40, 3, playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1520, 640, 40, 3, playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1550, 740, 40, 3, playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1580, 640, 40, 3, playerList));
		monsterList.add(new Monster(this, xMArray2, yMArray2, 1510, 640, 40, 3, playerList));
		//monsterList.add(new Monster(this, xMArray2, yMArray2, 940, 40, 40, 2,playerList));
		//monsterList.add(new Monster(this, xMArray2, yMArray2, 970, 40, 40, 3, playerList));
		//monsterList.add(new Monster(this, xMArray2, yMArray2, 1000, 900, 40,2, playerList));
		//monsterList.add(new Monster(this, xMArray2, yMArray2, 1030, 900, 40,3, playerList));
		//monsterList.add(new Monster(this,xMArray2, yMArray2, 700, 80, 30, playerList));
		
		Rect rect1 = new Rect(this, wallList, new Point(000, 000), 102, 816 );							//these create some squares 
		rectList.add(rect1);
		
		Rect rect2 = new Rect(this, wallList, new Point(400, 250), 204, 102 );
		rectList.add(rect2);
		
		Rect rect3 = new Rect(this, wallList, new Point(400, 575), 102, 510 );
		rectList.add(rect3);
		
		Rect rect4 = new Rect(this, wallList, new Point(1600, 100), 408, 102 );
		rectList.add(rect4);
		
		Rect rect5 = new Rect(this, wallList, new Point(1400, 800), 204, 204 );
		rectList.add(rect5);
		
		Rect rect6 = new Rect(this, wallList, new Point(800, 400), 408, 408 );
		rectList.add(rect6);
		
		//Rect rect7 = new Rect( wallList, new Point(1550, 700), 300, 500 );
		//rectList.add(rect7);
		
		//Rect rect8 = new Rect( wallList, new Point(1600, 600), 204, 700 );
		//rectList.add(rect8);
		
		loadBorderWalls();
		state = 1;
	}
	
	/**
	 * loads the second level
	 */
	protected void loadLevel2() {
		unloadLevel();
		unloadMainMenu();
		unloadSubMenu();
		levelNum = 2;
		
		double[] xArray = { 0, 30, 30, 0 };
		double[] yArray = { 0, 0, 30, 30 };											//these make a square
		double[] sxArray = { 0,10,10,15,20,20,30,26,20,20,10,10,4 };				//these make a sword
		double[] syArray = { 20,20,-20,-25,-20,20,20,28,30,40,40,30,28 };
		double iterTime = 6;														//I actually don't know what this is
		double iterTheta = 100;														//Same

		playerList.add(new Player(this, xArray, yArray, 700, 400, sxArray, syArray, sxArray.length, 10, iterTime, iterTheta, 5)); //you can actually make as many playable characters as you want
		
		double[] xMArray = { 20,50,70,60,50,40,50,50,40,40,30,30,20,20,30,20,10,0 };	//these make a more complex scary monster shape that is cool, but does not work well with collisions
		double[] yMArray = { 0,0,20,30,20,30,40,60,70,40,40,70,60,40,30,20,30,20 };
		double[] xMArray2 = { 0, 60, 60, 0 };											//these make a square
		double[] yMArray2 = { 0, 0, 60, 60 };
		double[] ex = { 0, 10, 10, 0 };													//these make a smaller square
		double[] ey = {0, 0, 10, 10 };
		
		monsterList.add(new Monster(this,xMArray2, yMArray2, 400, 840, 40, 4, playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 430, 940, 40, 4, playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 460, 1040, 40, 4, playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1490, 840, 40, 4, playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1420, 940, 40, 4, playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1450, 940, 40, 4, playerList));
		monsterList.add(new Monster(this,xMArray2, yMArray2, 1480, 840, 40, 4, playerList));
		monsterList.add(new Monster(this, xMArray2, yMArray2, 910, 40, 40, 4, playerList));
		monsterList.add(new Monster(this, xMArray2, yMArray2, 940, 40, 40, 4, playerList));
		monsterList.add(new Monster(this, xMArray2, yMArray2, 940, 40, 40, 4, playerList));
		//monsterList.add(new Monster(this, xMArray2, yMArray2, 970, 40, 20, 3, playerList));
		//monsterList.add(new Monster(this, xMArray2, yMArray2, 1000, 40, 20,2, playerList));
		//monsterList.add(new Monster(this, xMArray2, yMArray2, 1030, 40, 2-30,3, playerList));
		
		Rect rect1 = new Rect(this, wallList, new Point(1818, 000), 102, 816 );							//these create some squares 
		rectList.add(rect1);
		
		Rect rect2 = new Rect(this, wallList, new Point(1200, 800), 208, 106);
		rectList.add(rect2);
		
		Rect rect3 = new Rect(this, wallList, new Point(200, 200), 204, 408 );
		rectList.add(rect3);
		
		Rect rect4 = new Rect(this, wallList, new Point(400, 775), 102, 310 );
		rectList.add(rect4);
		
		Rect rect5 = new Rect(this, wallList, new Point(800, -100), 102, 408 );
		rectList.add(rect5);
		
		Rect rect6 = new Rect(this, wallList, new Point(1200, 200), 408, 204 );
		rectList.add(rect6);
		
		loadBorderWalls();
		state = 1;
	}
	
	
	/**
	 * loads the next level whatever that may be
	 */
	protected void loadNextLevel() {
		if(levelNum == 0) {
			loadLevel1();
		} else if (levelNum == 1) {
			loadLevel2();
		} else {
			loadMenu2(3);
		}
	}
	
	/**
	 * restarts the current level
	 */
	protected void loadSameLevel() {
		if(levelNum == 1) {
			loadLevel1();
		}
		if(levelNum == 2) {
			loadLevel2();
		}
	}
	
	@Override
	protected void mousePress(int x, int y) {
		
	}

	@Override
	protected void mouseMove(int x, int y) {

	}
	
	/**
	 * self explanatory
	 */
	private void checkCollisions() {
		for (int i = 0; i < monsterList.size(); i++) {
			Area areaMonster;
			Monster monsterI = monsterList.get(i);
			for(int j = 0; j < wallList.size(); j++) {
				Wall wallJ = wallList.get(j);
				areaMonster = monsterI.getArea();													//checks collisions of Monsters and Walls
				Area wallSword = wallJ.getArea();
				areaMonster.intersect(wallSword);
				if (!areaMonster.isEmpty()) {
					
					if(monsterI.isKnockback()) {
						if(wallJ.isX()) 
							monsterI.setXVel(-monsterI.getXVel());
						else
							monsterI.setYVel(-monsterI.getYVel());
					}
					wallJ.resolveCollision(monsterI);
				}
			}
			
			for(int j = 0; j < playerList.size(); j++) {
				Player playerJ = playerList.get(j);
				areaMonster = monsterI.getArea();
				Area areaSword = playerJ.getSword().getArea();
				areaMonster.intersect(areaSword);	
				if (!areaMonster.isEmpty() && playerJ.getSword().isSwinging()) {					//checks the collisions of Monsters and Swords
					if(!monsterI.isInvulnerable()) {
						playerJ.getCurrGun().addAmmo(1);
					}
					if (monsterI.hit(playerJ, playerJ.getSword().getAD())) {
						monsterList.remove(i);
					}
					
				}
			}
			
			for(int b = i + 1; b < monsterList.size(); b++) {
				Monster monsterB = monsterList.get(b);
				double x = monsterB.getCenterPosX() - monsterI.getCenterPosX();
				double y = monsterB.getCenterPosY() - monsterI.getCenterPosY();
				double distance = monsterB.getPoly().getBounds().getWidth() / 2 + monsterI.getPoly().getBounds().getWidth() / 2;
				if (distance * distance > x * x + y * y) {
					double dif = distance - Math.sqrt(x * x + y * y);                                            //checks the collisions of Monsters and other Monsters
					double theta = Math.atan2(y, x);
					monsterB.translate(dif / monsterB.getJellyness() * Math.cos(theta), dif / monsterB.getJellyness() * Math.sin(theta));
					monsterI.translate(dif / monsterI.getJellyness() * Math.cos(-theta), dif / monsterI.getJellyness() * Math.sin(-theta));
				}
			}
			if(monsterList.size() > i) {
				for(int a = 0; a < bulletList.size(); a++) {
					if(monsterList.size() > i && bulletList.get(a).isAlly()) {
						BulletFactory bulletA = bulletList.get(a);
						areaMonster = monsterI.getArea();
						Area areaBullet = bulletA.getArea();
						areaMonster.intersect(areaBullet);
						if(!areaMonster.isEmpty()) {
							if(!playerList.isEmpty())
								monsterI.setAggro(playerList.get(0));
							
							for(int b = 0; b < monsterList.size(); b++) {
								Monster monsterB = monsterList.get(b);
								if(monsterI.getCenterPos().distance(monsterB.getCenterPos()) < monsterI.getYellDistance()) {
									monsterB.setAggro(bulletA.getGun().getLink());
								}
							}
							
							if(monsterI.hit(bulletA.getDir()+180, bulletA.getDamage(), bulletA.getGun().getCurrKnockback())) {
								monsterList.remove(i);
							}
							if(bulletA.getType() == 1)
								bulletList.remove(a);
						}
				
					}
				}
			}
		}
		for (int i = 0; i < playerList.size(); i++) {
			Player playerI = playerList.get(i);
			Area areaLink;
			for(int j = 0; j < wallList.size(); j++) {
				Wall wallJ = wallList.get(j);
				areaLink = playerI.getArea();
				Area wallSword = wallJ.getArea();													//checks the collisions of Walls and playable characters
				areaLink.intersect(wallSword);
				if (!areaLink.isEmpty()) {

					if((playerI.isDashing() & playerI.willSlide()) || playerI.isSliding()) {
						playerI.startSlide();
						if(wallJ.isX()) 
							playerI.setXVel(-playerI.getXVel());
						else
							playerI.setYVel(-playerI.getYVel());
					} else if(playerI.isDashing() & !playerI.willSlide()) {
						playerI.stopDash();
					}
					wallJ.resolveCollision(playerI);
				}
			}
			if(hell) {
				for (int k = 0; k < monsterList.size(); k++) {
					Monster monsterK = monsterList.get(k);
					areaLink = playerList.get(i).getArea();
					Area areaMonster = monsterK.getArea();
					areaMonster.intersect(areaLink);
					if(!areaMonster.isEmpty()) {																//checks the collisions of playable characters and Monsters
						if (playerI.hit(monsterK.getAD())) {
							playerList.remove(i);
							setHell(false);
							break;
						}
					}
				}
			}
		}
		for(int g = 0; g < bulletList.size(); g++ ) {
			BulletFactory bulletG = bulletList.get(g);
			Area areaBullet;
			for(Wall wall: wallList) {
				if(bulletList.size() > g) {
					areaBullet = bulletG.getArea();
					Area areaWall = wall.getArea();
					areaBullet.intersect(areaWall);
					if(!areaBullet.isEmpty()) {
						bulletList.remove(g);
					}
				}
			}
			if(!bulletG.isAlly()) {
				//System.out.println("enemy bullet");
				for(int l = 0; l < playerList.size(); l++) {
					Player playerL = playerList.get(0);
					Area linkArea = playerList.get(l).getArea();
					areaBullet = bulletG.getArea();
					areaBullet.intersect(linkArea);
					if(!areaBullet.isEmpty()) {
						if (playerL.hit(bulletG.getDamage())) {
							playerList.remove(l);
							setHell(false);
							break;
						}
						bulletList.remove(g);
					}
				}
			}
		}
	}

	@Override
	protected void simpleRender(Graphics2D dbg) {
		switch (state) {
			case 1: 
				back.draw(dbg);
				dbg.setColor(Color.red);
				
				for (Rect rect: rectList)
					rect.draw(dbg);

				for (Monster monster: monsterList)
					monster.draw(dbg);

				for (Player player: playerList)
					player.draw(dbg);
				
				for (BulletFactory bullet: bulletList)
					bullet.draw(dbg);
				
				/*if(monsterList.isEmpty() && levelNum != maxLevel) {
					menu3.draw(dbg);
				} else if(monsterList.isEmpty() && levelNum == maxLevel) {
					menu4.draw(dbg);
				}
				if(playerList.isEmpty()) {
					subMenu.draw(dbg);
				}*/
				if(subMenu != null) {
					subMenu.draw(dbg);
				}
				break;
			case 0:
				mainMenu.draw(dbg);
		}
		dbg.drawString("Average FPS/UPS: " + df.format(averageFPS) + ", "
				+ df.format(averageUPS), 20, 25);
		//drawButtons(dbg);
		dbg.translate(1000,1000);
	}

	@Override
	protected void simpleUpdate() {
		switch (state) {
			case 1:
				for (Player player: playerList) {
					player.update(justPressed, pressed, justReleased, direction, magnitude, butA, butB, butX, leftTrigger, rightTrigger);				//updates playable characters
				}
				
				for (Monster monster: monsterList) {								//updates Monsters
					monster.update();
				}
				
				for (BulletFactory bullet: bulletList) {								//updates Monsters
					bullet.update();
				}
				
				checkCollisions();															//updates Collisions
				if(subMenu == null) {
					if(playerList.isEmpty())
						loadMenu2(1);
					if(monsterList.isEmpty() && levelNum != maxLevel)
						loadMenu2(2);
					if(monsterList.isEmpty() && levelNum == maxLevel)
						loadMenu2(3);
				} else {
					subMenu.update(direction, magnitude, butA, butB);
				}
				break;
			case 0:
				mainMenu.update(direction, magnitude, butA, butB);
				break;
		}
		//fightTop.setTimeSpent(timeSpentInGame);
	}

	public static void main(String args[]) {
		int fps = DEFAULT_FPS;
		if (args.length != 0)
			fps = Integer.parseInt(args[0]);

		long period = (long) 1000.0 / fps;
		System.out.println("fps: " + fps + "; period: " + period + " ms");
		new NotHyrule(period * 1000000L); // ms --> nanosecs
	}// end of main()

} // end of WormChase class

