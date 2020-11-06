import java.awt.Graphics;
import java.util.ArrayList;

public class Menu {
	int type = 0;
	ArrayList<Button> buttons;
	ArrayList<Button> buttons4;
	ArrayList<Image> images0;
	ArrayList<Image> images4;
	int currState = 0;
	int currState4 = 0;
	int inputState = 0;
	int activeButtonNum4 = 0;
	int lastDir = -1;
	int activeButtonNum;
	StripImageC rarity;
	NotHyrule nH;
	boolean firstA = true;
	boolean firstB = true;
	Image win;
	Image clown;
	Image text;
	Image instructions;
	//AudioClip oofTitle = new AudioClip("oof.wav");
	
	/**
	 * creates a certint type of menu
	 * @param nH		the NotHyrule it belongs to 
	 * @param type		the version of the menu you want, 0 == main 1 == lose 2 == next level 3 == win
	 */
	public Menu(NotHyrule nH, int type) {
		this.type = type;
		int mid1 = 540;
		int mid = 800;
		int y = 800;
		buttons = new ArrayList<Button>(4);
		this.nH = nH;
		if(type == 0) {
			
			buttons4 = new ArrayList<Button>(2);
			images0 = new ArrayList<Image>(2);
			images4 = new ArrayList<Image>(4);
			rarity = new StripImageC("rarityStrip.png", 8);
			clown = new Image("clown.png");
			clown.setPos(1360, 100);
			text = new Image("text.png");
			instructions = new Image("instructions.png");
			images0.add(new Image("test.png"));
			//images0.add(new Image("full.png"));
			
			images4.add(new Image("god.jpg"));
			images4.add(new Image("god2.jpg"));
			
			buttons.add(new Button("Please Let Me Play This Game", y + 80, mid - 80, true));
			buttons.add(new Button("I Really Want To Play It", y, mid, true));
			buttons.add(new Button("I NEED to Play it", y - 80, mid + 80, true));
	
			buttons.add(new Button("Instructions", y + 80, mid - 80, false));
			buttons.add(new Button("Mailler is the greatest human being", y, mid, false));
			buttons.add(new Button("EXIT", y - 80, mid + 80, false));
			
			buttons4.add(new Button("Previous", y, mid, true));
			buttons4.add(new Button("Next", y, mid, false));
			buttons4.add(new Button("Back", y, mid+80, false));
		} else if(type == 1) {
			buttons.add(new Button("Try Again", y, mid, true));
			buttons.add(new Button("EXIT", y, mid, false));
		} else if(type == 2) {
			buttons.add(new Button("Next Level", y, mid, true));
			buttons.add(new Button("EXIT", y, mid, false));
		} else {
			buttons.add(new Button("EXIT", y, mid, false));
			win = new Image("winner.png");
		}
	}
	/**
	 * navigates the different menus
	 * @param direction		direction of stick input
	 * @param magnitude		magitude of stick input
	 * @param butA			A button	
	 * @param butB			B button
	 */
	private void updateInputState(double direction, double magnitude, int butA, int butB) {
		if(type == 0) {
		if(magnitude > 0.5) {
			if(direction < 135 && direction > 45 && lastDir != 0) {
				lastDir = 0;
				if(currState == 0) {
					
					switch (activeButtonNum) {
					case 0:
						activeButtonNum = 3;
						break;
					case 1:
						activeButtonNum = 4;
						break;
					case 2:
						activeButtonNum = 5;
						break;
					case 3:
						activeButtonNum = 3;
						break;
					case 4:
						activeButtonNum = 4;
						break;
					case 5:
						activeButtonNum = 5;
						break;
					}
				} else if(currState == 4) {
					switch(activeButtonNum4) {
					case 0:
						activeButtonNum4 = 1;
						break;
					}
				}
				
			}
			if(direction < 225 && direction > 135 && lastDir != 1) {
				//System.out.println("bottom");
				lastDir = 1;
				if(currState == 0) {
					
					switch (activeButtonNum) {
					case 0:
						activeButtonNum = 1;
						break;
					case 1:
						activeButtonNum = 2;
						break;
					case 2:
						activeButtonNum = 0;
						break;
					case 3:
						activeButtonNum = 4;
						break;
					case 4:
						activeButtonNum = 5;
						break;
					case 5:
						activeButtonNum = 3;
						break;
					}
				} else if(currState == 4) {
					switch(activeButtonNum4) {
					case 1:
						activeButtonNum4 = 2;
						break;
					case 2:
						activeButtonNum4 = 1;
						break;
					}
				}
			}
			
			if(direction < 315 && direction > 225  && lastDir != 2) {
				//System.out.println("left");
				lastDir = 2;
				if(currState == 0) {
					
					switch (activeButtonNum) {
					case 0:
						activeButtonNum = 0;
						break;
					case 1:
						activeButtonNum = 1;
						break;
					case 2:
						activeButtonNum = 2;
						break;
					case 3:
						activeButtonNum = 0;
						break;
					case 4:
						activeButtonNum = 1;
						break;
					case 5:
						activeButtonNum = 2;
						break;
					}
				} else if(currState == 4) {
					switch(activeButtonNum4) {
					case 1:
						activeButtonNum4 = 0;
						break;
					case 2:
						activeButtonNum4 = 0;
						break;
					}
				}
			}
			if((direction < 45 || direction > 315) && lastDir != 3) {
				//System.out.println("top");
				lastDir = 3;
				if(currState == 0) {
					
					switch (activeButtonNum) {
					case 0:
						activeButtonNum = 2;
						break;
					case 1:
						activeButtonNum = 0;
						break;
					case 2:
						activeButtonNum = 1;
						break;
					case 3:
						activeButtonNum = 5;
						break;
					case 4:
						activeButtonNum = 3;
						break;
					case 5:
						activeButtonNum = 4;
						break;
					}
				} else if(currState == 4) {
					switch(activeButtonNum4) {
					case 1:
						activeButtonNum4 = 2;
						break;
					case 2:
						activeButtonNum4 = 1;
						break;
					}
				}
			}
		} else {
			lastDir = -1;
		}
		if(butA == 1 && firstA) {
			if(currState == 0) {
				switch (activeButtonNum) {
				case 0:
					nH.changeState(1);
					break;
				case 1:
					nH.changeState(1);
					break;
				case 2:
					nH.changeState(1);
					break;
				case 3:
					currState = 3;
					break;
				case 4:
					currState = 4;
					//System.out.println("currState4 == " + currState4);
					break;
				case 5:
					currState = 5;
					nH.running = false;
					//System.exit(0);
					break;
				}
			} else if(currState == 4) {
				if(currState4 == 0) {
					switch(activeButtonNum4) {
					case 0:
						currState4 = 1;
						break;
					case 1:
						currState4 = 1;
						break;
					case 2:
						currState = 0;
						break;
					}
				} else if(currState4 == 1) {
					switch(activeButtonNum4) {
					case 0:
						currState4 = 0;
						break;
					case 1:
						currState4 = 0;
						break;
					case 2:
						currState = 0;
						break;
					}
				}
			}
			firstA = false;
		}
		if(butA == 0 && !firstA) {
			firstA = true;
		}
		if(butB == 1 && firstB) {
			if(currState == 2 | currState == 3) {
				currState = 0;
				//System.out.println("GETEM");
			}
			else if(currState == 4) {
				switch (currState4) {
				case 0:
					currState = 0;
					break;
				case 1:
					currState4 = 0;
					break;
				}
			}
			firstB = false;
		}
		if(butB == 0 && !firstB) {
			firstB = true;
		}
		} else if (type == 1) {
			if(magnitude > 0.5) {
				if(direction < 135 && direction > 45 && lastDir != 0) {
					lastDir = 0;
					switch (activeButtonNum) {
					case 0:
						activeButtonNum = 1;
						break;
					case 1:
						activeButtonNum = 1;
						break;
					}
				}
				else if(direction < 315 && direction > 225  && lastDir != 2) {
					//System.out.println("left");
					lastDir = 2;
					switch (activeButtonNum) {
					case 0:
						activeButtonNum = 0;
						break;
					case 1:
						activeButtonNum = 0;
						break;
					}
				}
			}
			if(butA == 1 && firstA) {
				switch (activeButtonNum) {
				case 0:
					//System.out.println("nH = " + nH);
					nH.loadSameLevel();
					break;
				case 1:
					nH.loadMenu();
					break;
				}
			}
		} else if(type == 2){
			if(magnitude > 0.5) {
				if(direction < 135 && direction > 45 && lastDir != 0) {
					lastDir = 0;
					switch (activeButtonNum) {
					case 0:
						activeButtonNum = 1;
						break;
					case 1:
						activeButtonNum = 1;
						break;
					}
				}
				else if(direction < 315 && direction > 225  && lastDir != 2) {
					//System.out.println("left");
					lastDir = 2;
					switch (activeButtonNum) {
					case 0:
						activeButtonNum = 0;
						break;
					case 1:
						activeButtonNum = 0;
						break;
					}
				}
			}
			if(butA == 1 && firstA) {
				switch (activeButtonNum) {
				case 0:
					//System.out.println("nH = " + nH);
					nH.loadNextLevel();
					break;
				case 1:
					nH.loadMenu();
					break;
				}
			}
		} else {
			if(magnitude > 0.5) {
				if(direction < 135 && direction > 45 && lastDir != 0) {
					lastDir = 0;
					switch (activeButtonNum) {
					case 0:
						activeButtonNum = 0;
						break;
					}
				}
				else if(direction < 315 && direction > 225  && lastDir != 2) {
					//System.out.println("left");
					lastDir = 2;
					switch (activeButtonNum) {
					case 0:
						activeButtonNum = 0;
						break;
					}
				}
			}
			if(butA == 1 && firstA) {
				switch (activeButtonNum) {
				case 0:
					//System.out.println("nH = " + nH);
					nH.loadMenu();
				}
			}
		}
	} 
	
	/**
	 * updates everything about the menu
	 * @param direction		direction of the stick input	
	 * @param magnitude		magnitude of the stick input
	 * @param butA			A button
	 * @param butB			B button
	 */
	public void update(double direction, double magnitude, int butA, int butB) {
		
		updateInputState(direction, magnitude, butA, butB);
		
		if(currState == 0) {
			buttons.get(activeButtonNum).update(true);
			for(int i = 0; i< buttons.size();i++) {
				if(i != activeButtonNum)
					buttons.get(i).update(false);
			}
		} else if(currState == 4) {
			//System.out.println("currState == 4");
			//if(!oofTitle.isActive())
				//oofTitle.play(true);
			buttons4.get(activeButtonNum4).update(true);
			for(int i = 0; i < buttons4.size();i++) {
				if(i != activeButtonNum4)
					buttons4.get(i).update(false);
			}
		}
		//System.out.println(activeButtonNum);
	}
	
	/**
	 * draws the menu
	 * @param g Graphics
	 */
	public void draw(Graphics g) {
		if(type == 3) {
			win.draw(g);
		}
		if(type == 0) {
			if(currState == 0) {
				for(int i = 0; i< images0.size();i++) {
					images0.get(i).draw(g);
				}
				for(int i = 0; i< buttons.size();i++) {
					buttons.get(i).draw(g);
				}
				clown.drawFadedImage(g, (float)0.2);
				text.draw(g);
			}
			if(currState == 3) {
				instructions.draw(g);
			}
			if(currState == 4) {
				if(currState4 ==0)
					images4.get(0).draw(g);
				else if(currState4 ==1)
					images4.get(1).draw(g);
				for(int i = 0; i< buttons4.size();i++) {
					buttons4.get(i).draw(g);
				}
			}
		} else {
			for(int i = 0; i< buttons.size();i++) {
				buttons.get(i).draw(g);
			}	
		}
		
	}
}
